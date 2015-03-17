package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit.Entry;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.relationalhelper.RelationalHelper;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * {@link Relational} transforms N input internal database tables into 1 output table using user typed SELECT SQL queries.
 * Output table is also stored in internal database to be accessible by other relational DPUs
 * <p/>
 * <b>WARNING:</b>This DPU is a part of optional UV relational functionality and relational DPUs currently do not fully follow UV philosophy as the user has
 * control of physical database table names
 * <p/>
 * The general philosophy of UV so far is, that DPU developer cannot influence the physical location of the internal files, graphs -- UV manages its internal
 * stores. As a result, such approach should be similar in relational data unit and DPU developer should NOT be able to set the target table name (currently,
 * there is a configuration option for extractor and transformer, which allows to set up the target table name). Target table name is ok for loader, but not for
 * extractor/transformer, where the target table is given by the data flow in the pipeline.
 * <p/>
 * Current implementation is a compromise to be able to provide a general, SQL query based transformer for users. If user did not know the real table name, he
 * would not be able to write SQL queries. Not without complex parsing of SQL queries. Further discussion is needed to solve this issue and this will be aim of
 * future releases.
 */
@DPU.AsTransformer
public class Relational extends ConfigurableBase<RelationalConfig_V1> implements ConfigDialogProvider<RelationalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Relational.class);

    private static final String DB_USER_BASE_NAME = "tRelational_";

    private static final String DB_PASSWORD = "dummy123";

    private Messages messages;

    private DPUContext context;

    @DataUnit.AsInput(name = "inputTables")
    public RelationalDataUnit inputTables;

    @DataUnit.AsOutput(name = "outputTable")
    public WritableRelationalDataUnit outputTable;

    public Relational() {
        super(RelationalConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        this.context = context;
        this.messages = new Messages(this.context.getLocale(), this.getClass().getClassLoader());

        String targetTableName = this.config.getTargetTableName().toUpperCase();
        String symbolicName = targetTableName;
        Set<RelationalDataUnit.Entry> tables = null;

        try {
            tables = RelationalHelper.getTables(this.inputTables);
        } catch (DataUnitException ex) {
            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.tables.iterator"), ex);
            return;
        }

        if (tables.isEmpty()) {
            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.tables.input"));
            return;
        }

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ResultSetMetaData meta = null;
        String dbUserName = DB_USER_BASE_NAME + this.context.getDpuInstanceId();
        try {
            if (checkTableExists(targetTableName)) {
                this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.db.tableunique.short", targetTableName), this.messages.getString("errors.db.tableunique.long"));
                return;
            }

            LOG.debug("Going to create database user {} who will execute SELECT queries in internal DB", dbUserName);
            executeSqlQueryInInternalDatabaseAsAdmin(DatabaseHelper.createDropUserQuery(dbUserName));
            executeSqlQueryInInternalDatabaseAsAdmin(DatabaseHelper.createDatabaseUserQuery(dbUserName, DB_PASSWORD));
            LOG.debug("Database user {} successfully created", dbUserName);
            grantSelectOnAllInputTables(tables, dbUserName);

            LOG.debug("Going to create SQL connection to internal database for user {}", dbUserName);
            conn = this.inputTables.getDatabaseConnectionForUser(dbUserName, DB_PASSWORD);
            LOG.debug("SQL connection to internal database for user {} successfully created", dbUserName);
            LOG.debug("Executing SQL query in internal database");
            stmnt = conn.createStatement();

            try {
                LOG.info("Executing query " + this.config.getSqlQuery());
                rs = stmnt.executeQuery(this.config.getSqlQuery());
                meta = rs.getMetaData();
                LOG.debug("SQL query executed successfully");
            } catch (SQLException e) {
                this.context.sendMessage(MessageType.ERROR, this.messages.getString("errors.db.query"));
                throw e;
            }

            List<ColumnDefinition> tableColumns = DatabaseHelper.getTableColumnsFromMetaData(meta);
            String createTableQuery = DatabaseHelper.getCreateTableQueryFromMetaData(tableColumns, targetTableName);

            LOG.debug("Creating internal db representation as " + createTableQuery);
            executeSqlQueryInInternalDatabaseAsAdmin(createTableQuery);
            LOG.debug("Database table in internal database successfully created");

            // For now, symbolic name and real table name are the same - user inserted
            this.outputTable.addExistingDatabaseTable(symbolicName, targetTableName);
            LOG.debug("New database table {} added to relational data unit", targetTableName);

            LOG.debug("Inserting data from source table into internal table");
            insertDataFromSelect(tableColumns, rs, targetTableName);
            LOG.debug("Inserting data from source table into internal table successful");

            // Create primary keys
            if (this.config.getPrimaryKeyColumns() == null || this.config.getPrimaryKeyColumns().isEmpty()) {
                LOG.debug("No primary keys defined, nothing to do");
            } else {
                LOG.debug("Going to create primary keys for table {}", targetTableName);
                String alterTablesQuery = DatabaseHelper.createPrimaryKeysQuery(targetTableName, this.config.getPrimaryKeyColumns());
                executeSqlQueryInInternalDatabaseAsAdmin(alterTablesQuery);
            }

            // Create indexes
            if (this.config.getIndexedColumns() == null || this.config.getIndexedColumns().isEmpty()) {
                LOG.debug("No indexed columns defined for target table, nothing to do");
            } else {
                LOG.debug("Going to create indexes for table {}", targetTableName);
                for (String indexedColumn : this.config.getIndexedColumns()) {
                    String indexQuery = DatabaseHelper.getCreateIndexQuery(targetTableName, indexedColumn);
                    executeSqlQueryInInternalDatabaseAsAdmin(indexQuery);
                }
            }

            Resource resource = ResourceHelpers.getResource(this.outputTable, symbolicName);
            Date now = new Date();
            resource.setCreated(now);
            resource.setLast_modified(now);
            ResourceHelpers.setResource(this.outputTable, symbolicName, resource);
            LOG.debug("Resource parameters for table updated");

        } catch (SQLException se) {
            LOG.error("Database error occured during transforming database tables", se);
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DPUException(this.messages.getString("errors.db.transformfailed"), se);
        } catch (Exception e) {
            LOG.error("Error occured during transforming database tables", e);
            throw new DPUException(this.messages.getString("errors.transformfailed"), e);
        } finally {
            DatabaseHelper.tryCloseResultSet(rs);
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
            try {
                executeSqlQueryInInternalDatabaseAsAdmin(DatabaseHelper.createDropUserQuery(dbUserName));
            } catch (DataUnitException e) {
                LOG.warn("Failed to drop DPU database user");
            }
        }
    }

    private void grantSelectOnAllInputTables(Set<Entry> tables, String dbUserName) throws DataUnitException {
        LOG.debug("Going to GRANT SELECT on all input tables to user {}", dbUserName);
        for (RelationalDataUnit.Entry table : tables) {
            String grantSelectQuery = DatabaseHelper.createGrantSelectOnTableQuery(table.getTableName(), dbUserName);
            executeSqlQueryInInternalDatabaseAsAdmin(grantSelectQuery);
        }
        LOG.debug("GRANT SELECT on input tables successful");
    }

    private boolean checkTableExists(String tableName) throws DataUnitException {
        boolean result = false;
        Connection conn = null;
        try {
            conn = getAdminConnectionInternal();
            result = DatabaseHelper.checkTableExists(conn, tableName);
        } catch (SQLException e) {
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error executing statement in internal dataunit database", e);
        } finally {
            DatabaseHelper.tryCloseConnection(conn);
        }

        return result;
    }

    private void insertDataFromSelect(List<ColumnDefinition> tableColumns, ResultSet rs, String tableName) throws SQLException, DataUnitException {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getAdminConnectionInternal();
            String insertQuery = DatabaseHelper.getInsertQueryForPreparedStatement(tableColumns, tableName);
            LOG.debug("Insert query for inserting into internal DB table: {}", insertQuery);
            ps = conn.prepareStatement(insertQuery);
            LOG.debug("Prepared statement for inserting data created");
            while (rs.next()) {
                fillInsertData(ps, tableColumns, rs);
                ps.execute();
            }
            conn.commit();
        } catch (SQLException | DataUnitException e) {
            LOG.error("Failed to load data into internal table", e);
            throw e;
        } finally {
            DatabaseHelper.tryCloseConnection(conn);
            DatabaseHelper.tryCloseStatement(ps);
        }
    }

    private void executeSqlQueryInInternalDatabaseAsAdmin(String query) throws DataUnitException {
        Statement stmnt = null;
        Connection conn = null;
        try {
            conn = getAdminConnectionInternal();
            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } catch (SQLException e) {
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error executing statement in internal dataunit database", e);
        } finally {
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
        }
    }

    private void fillInsertData(PreparedStatement ps, List<ColumnDefinition> columns, ResultSet rs) throws SQLException {
        int index = 1;
        for (ColumnDefinition column : columns) {
            Object sourceValue = rs.getObject(column.getColumnName());
            ps.setObject(index, sourceValue);
            index++;
        }
    }

    @Override
    public AbstractConfigDialog<RelationalConfig_V1> getConfigurationDialog() {
        return new RelationalVaadinDialog();
    }

    private Connection getAdminConnectionInternal() throws DataUnitException {
        return this.inputTables.getDatabaseConnection();
    }

}
