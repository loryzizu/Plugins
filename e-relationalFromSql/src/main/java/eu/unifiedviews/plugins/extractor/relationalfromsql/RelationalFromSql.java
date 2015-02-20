package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * {@link RelationalFromSql} extracts data from external source relational database (currently PostgreSQL supported)
 * and stores the data in the internal database table so other relational DPU can access them.
 * In current implementation, user typed SQL queries are used to extract data
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
@DPU.AsExtractor
public class RelationalFromSql extends ConfigurableBase<RelationalFromSqlConfig_V1> implements ConfigDialogProvider<RelationalFromSqlConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalFromSql.class);

    private Messages messages;

    private DPUContext context;

    @DataUnit.AsOutput(name = "outputTables")
    public WritableRelationalDataUnit outputTables;

    public RelationalFromSql() {
        super(RelationalFromSqlConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        this.context = context;
        this.messages = new Messages(this.context.getLocale(), this.getClass().getClassLoader());
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: DatabaseUrl: %s, username: %s, password: %s, "
                + "useSSL: %s, SQL query: %s",
                this.config.getDatabaseURL(), this.config.getUserName(), "***",
                this.config.isUseSSL(), this.config.getSqlQuery());
        LOG.info(shortMessage + " " + longMessage);

        try {
            Class.forName(this.config.getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            throw new DPUException(this.messages.getString("errors.driver.loadfailed"), e);
        }

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        ResultSetMetaData meta = null;

        try {
            String tableName = this.config.getTargetTableName().toUpperCase();
            String symbolicName = tableName;

            try {
                if (checkInternalTableExists(tableName)) {
                    this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.db.tableunique.short", tableName),
                            this.messages.getString("errors.db.tableunique.long"));
                    return;
                }
            } catch (SQLException | DataUnitException e) {
                throw new DPUException(this.messages.getString("errors.db.internaltable"));
            }

            try {
                LOG.debug("Connecting to the source database");
                conn = RelationalFromSqlHelper.createConnection(this.config);
                LOG.debug("Connected to the source database");
            } catch (SQLException e) {
                throw new DPUException(this.messages.getString("errors.db.connectionfailed"), e);
            }

            try {
                stmnt = conn.createStatement();
                LOG.info("Executing query " + this.config.getSqlQuery());
                rs = stmnt.executeQuery(this.config.getSqlQuery());
                meta = rs.getMetaData();
            } catch (SQLException e) {
                throw new DPUException(this.messages.getString("errors.db.queryfailed"), e);
            }

            try {
                String createTableQuery = QueryBuilder.getCreateTableQueryFromMetaData(meta, tableName);

                LOG.debug("Creating internal db representation as " + createTableQuery);
                executeSqlQueryInInternalDatabase(createTableQuery);
                LOG.debug("Database table in internal database successfully created");

                // For now, symbolic name and real table name are the same - user inserted
                this.outputTables.addExistingDatabaseTable(symbolicName, tableName);
                LOG.debug("New database table {} added to relational data unit", tableName);

                LOG.debug("Inserting data from source table into internal table");
                insertDataFromSelect(meta, rs, tableName);
                LOG.debug("Inserting data from source table into internal table successful");

                if (this.config.getPrimaryKeyColumns() == null || this.config.getPrimaryKeyColumns().isEmpty()) {
                    LOG.debug("No primary keys defined for table, nothing to do");
                } else {
                    LOG.debug("Going to add primary keys to the output database table");
                    String alterQuery = QueryBuilder.getPrimaryKeysQuery(tableName, this.config.getPrimaryKeyColumns());
                    executeSqlQueryInInternalDatabase(alterQuery);
                    LOG.debug("Primary keys successfully added to the output table");
                }

                Resource resource = ResourceHelpers.getResource(this.outputTables, symbolicName);
                Date now = new Date();
                resource.setCreated(now);
                resource.setLast_modified(now);
                ResourceHelpers.setResource(this.outputTables, symbolicName, resource);
                LOG.debug("Resource parameters for table updated");
            } catch (Exception e) {
                throw new DPUException(this.messages.getString("errors.db.transformfailed"), e);
            }
        } finally {
            RelationalFromSqlHelper.tryCloseDbResources(conn, stmnt, rs);
        }
    }

    private void executeSqlQueryInInternalDatabase(String query) throws DataUnitException {
        Statement stmnt = null;
        Connection conn = null;
        try {
            conn = getConnectionInternal();
            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } catch (SQLException e) {
            RelationalFromSqlHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error executing statement in internal dataunit database", e);
        } finally {
            RelationalFromSqlHelper.tryCloseStatement(stmnt);
            RelationalFromSqlHelper.tryCloseConnection(conn);
        }
    }

    private void insertDataFromSelect(ResultSetMetaData meta, ResultSet rs, String tableName) throws DataUnitException {
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnectionInternal();
            String insertQuery = QueryBuilder.getInsertQueryForPreparedStatement(meta, tableName);
            LOG.debug("Insert query for inserting into internal DB table: {}", insertQuery);
            ps = conn.prepareStatement(insertQuery);
            LOG.debug("Prepared statement for inserting data created");
            while (rs.next()) {
                fillInsertData(ps, meta, rs);
                ps.execute();
            }
            conn.commit();
        } catch (SQLException e) {
            LOG.error("Failed to load data into internal table", e);
            RelationalFromSqlHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error loading SQL data into internal database");
        } finally {
            RelationalFromSqlHelper.tryCloseStatement(ps);
            RelationalFromSqlHelper.tryCloseConnection(conn);
        }
    }

    private boolean checkInternalTableExists(String targetTableName) throws SQLException, DataUnitException {
        Connection conn = null;
        boolean bTableExists = false;
        DatabaseMetaData dbm = null;
        ResultSet tables = null;
        try {
            conn = getConnectionInternal();
            dbm = conn.getMetaData();
            tables = dbm.getTables(null, null, targetTableName, null);
            if (tables.next()) {
                bTableExists = true;
            }
        } finally {
            RelationalFromSqlHelper.tryCloseResultSet(tables);
            RelationalFromSqlHelper.tryCloseConnection(conn);
        }
        return bTableExists;
    }

    private void fillInsertData(PreparedStatement ps, ResultSetMetaData meta, ResultSet rs) throws SQLException {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            ps.setObject(i, rs.getObject(i));
        }
    }

    private Connection getConnectionInternal() throws DataUnitException {
        return this.outputTables.getDatabaseConnection();
    }

    @Override
    public AbstractConfigDialog<RelationalFromSqlConfig_V1> getConfigurationDialog() {
        return new RelationalFromSqlVaadinDialog();
    }

}
