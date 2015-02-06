package eu.unifiedviews.plugins.loader.relationaltosql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.relationalhelper.RelationalHelper;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsLoader
public class RelationalToSql extends ConfigurableBase<RelationalToSqlConfig_V1> implements ConfigDialogProvider<RelationalToSqlConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalToSql.class);

    private DPUContext context;

    private Messages messages;

    @DataUnit.AsInput(name = "input")
    public RelationalDataUnit inTablesData;

    public RelationalToSql() {
        super(RelationalToSqlConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        this.context = dpuContext;
        this.messages = new Messages(this.context.getLocale(), this.getClass().getClassLoader());
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: DatabaseUrl: %s, username: %s, password: %s, "
                + "useSSL: %s, targetTable: %s, clearTargetTable: %s, dropTargetTable: %s",
                this.config.getDatabaseURL(), this.config.getUserName(), "***",
                this.config.isUseSSL(), this.config.getTableNamePrefix(), this.config.isClearTargetTable(),
                this.config.isDropTargetTable());
        LOG.info(shortMessage + " " + longMessage);

        try {
            Class.forName(this.config.getJDBCDriverName());
        } catch (ClassNotFoundException e) {
            throw new DPUException(this.messages.getString("errors.driver.loadfailed"), e);
        }

        Iterator<RelationalDataUnit.Entry> tablesIteration;
        try {
            tablesIteration = RelationalHelper.getTables(this.inTablesData).iterator();
        } catch (DataUnitException ex) {
            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.tables.iterator"), ex);
            return;
        }

        boolean bTableExists = false;
        PreparedStatement insertStmnt = null;
        Connection conn = null;
        try {
            conn = RelationalToSqlHelper.createConnection(this.config);

            int index = 1;
            while (!this.context.canceled() && tablesIteration.hasNext()) {
                final RelationalDataUnit.Entry entry = tablesIteration.next();
                final String sourceTableName = entry.getTableName();
                final String targetTableName = createTargetTableName(index);
                index++;

                try {
                    List<ColumnDefinition> sourceColumns = getColumnDefinitionsForSourceTable(sourceTableName);
                    bTableExists = checkTableExists(conn, targetTableName);
                    if (bTableExists) {
                        LOG.debug("Target table already exists");
                        if (this.config.isDropTargetTable()) {
                            LOG.info(String.format("Dropping table %s", this.config.getTableNamePrefix()));
                            dropTable(conn, targetTableName);
                            bTableExists = false;
                        } else if (this.config.isClearTargetTable()) {
                            LOG.info(String.format("Truncating table %s", this.config.getTableNamePrefix()));
                            truncateTable(conn);
                        }
                    }

                    if (!bTableExists) {
                        LOG.info("Target table does not exist. Recreating");
                        createTable(conn, targetTableName, sourceColumns);
                    } else {
                        if (!checkTablesConsistent(conn, targetTableName, sourceColumns)) {
                            this.context.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.table.inconsistent"));
                            return;
                        }
                    }
                    String insertQuery = QueryBuilder.getInsertQueryForPreparedStatement(targetTableName, sourceColumns);
                    LOG.debug("Creating prepared statement for insert into external database using query: {}", insertQuery);
                    insertStmnt = conn.prepareStatement(insertQuery);
                    insertDataFromSourceToTarget(insertStmnt, sourceColumns, sourceTableName);
                    conn.commit();
                } catch (Exception e) {
                    LOG.error("Failed to load data from internal table: {} into external table: {}", sourceTableName, targetTableName, e);
                    RelationalToSqlHelper.tryRollbackConnection(conn);
                } finally {
                    RelationalToSqlHelper.tryCloseStatement(insertStmnt);
                }
            }
        } catch (SQLException se) {
            RelationalToSqlHelper.tryRollbackConnection(conn);
            LOG.error("Database error occurred during loading data from internal database to external SQL database", se);
            throw new DPUException(this.messages.getString("errors.dpu.insertfail"), se);
        } catch (Exception e) {
            LOG.error("Error during loading data from internal database to external SQL database", e);
            throw new DPUException(this.messages.getString("errors.dpu.insertfail"), e);
        } finally {
            RelationalToSqlHelper.tryCloseConnection(conn);;
        }
    }

    private void insertDataFromSourceToTarget(PreparedStatement insertStmnt, List<ColumnDefinition> columns, String sourceTableName) throws Exception {
        Connection sourceConnection = null;
        ResultSet sourceData = null;
        Statement stmnt = null;

        try {
            sourceConnection = this.inTablesData.getDatabaseConnection();
            stmnt = sourceConnection.createStatement();
            sourceData = stmnt.executeQuery(QueryBuilder.getQueryFromSourceTableSelect(columns, sourceTableName));
            while (sourceData.next()) {
                QueryBuilder.fillInsertQueryData(insertStmnt, sourceData, columns);
                insertStmnt.execute();
            }
        } catch (Exception e) {
            LOG.error("Failed to insert data from internal source table into external table", e);
            throw e;
        } finally {
            RelationalToSqlHelper.tryCloseDbResources(sourceConnection, stmnt, sourceData);
        }
    }

    private String createTargetTableName(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.config.getTableNamePrefix());
        sb.append("_");
        sb.append(index);

        return sb.toString();
    }

    private final void createTable(Connection conn, String targetTableName, List<ColumnDefinition> sourceColumns) throws SQLException {
        Statement stmnt = null;
        try {
            String query = QueryBuilder.getQueryForCreateTable(targetTableName, sourceColumns);
            LOG.debug("Creating table in external database using query: {}", query);

            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } catch (SQLException e) {
            LOG.error("Failed to create target database table " + targetTableName, e);
            throw e;
        } finally {
            RelationalToSqlHelper.tryCloseStatement(stmnt);
        }
    }

    private List<ColumnDefinition> getColumnDefinitionsForSourceTable(String sourceTableName) throws Exception {
        List<ColumnDefinition> columns = new ArrayList<>();
        ResultSet rs = null;
        DatabaseMetaData dbm = null;
        Connection conn = null;
        try {
            conn = this.inTablesData.getDatabaseConnection();
            dbm = conn.getMetaData();
            rs = dbm.getColumns(null, null, sourceTableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toLowerCase();
                columns.add(new ColumnDefinition(columnName,
                        rs.getString("TYPE_NAME"),
                        rs.getInt("DATA_TYPE"),
                        rs.getInt("COLUMN_SIZE")));
            }
        } catch (SQLException | DataUnitException e) {
            LOG.error("Failed to obtain columns from the source internal database table " + sourceTableName, e);
            throw e;
        } finally {
            RelationalToSqlHelper.tryCloseResultSet(rs);
            RelationalToSqlHelper.tryCloseConnection(conn);
        }

        return columns;
    }

    private boolean checkTablesConsistent(Connection conn, String targetTableName, List<ColumnDefinition> sourceColumns) throws Exception {
        boolean bTableConsistent = true;
        ResultSet rs = null;
        DatabaseMetaData dbm = null;
        Map<String, ColumnDefinition> targetColumns = new HashMap<>();
        try {
            dbm = conn.getMetaData();
            rs = dbm.getColumns(null, null, targetTableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                targetColumns.put(columnName.toUpperCase(), new ColumnDefinition(columnName,
                        rs.getString("TYPE_NAME"),
                        rs.getInt("DATA_TYPE"),
                        rs.getInt("COLUMN_SIZE")));
            }
        } catch (SQLException e) {
            throw new Exception("Failed to check consistency between existing table and provided files");
        } finally {
            RelationalToSqlHelper.tryCloseResultSet(rs);
        }

        // TODO: check data types
        // TODO: check column count; what if target has more columns and some of them are not null?
        for (ColumnDefinition sourceColumn : sourceColumns) {
            if (!targetColumns.containsKey(sourceColumn.getColumnName().toUpperCase())) {
                bTableConsistent = false;
                break;
            }
        }

        return bTableConsistent;
    }

    private final void truncateTable(Connection conn) throws SQLException {
        String query = QueryBuilder.getQueryForTruncateTable(this.config.getTableNamePrefix());
        Statement stmnt = null;
        try {
            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } finally {
            RelationalToSqlHelper.tryCloseStatement(stmnt);
        }
    }

    private final void dropTable(Connection conn, String tableName) throws SQLException {
        String query = QueryBuilder.getQueryForDropTable(tableName);
        Statement stmnt = null;
        try {
            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } finally {
            RelationalToSqlHelper.tryCloseStatement(stmnt);
        }
    }

    private boolean checkTableExists(Connection conn, String targetTableName) throws SQLException {
        boolean bTableExists = false;
        DatabaseMetaData dbm = null;
        ResultSet tables = null;
        try {
            dbm = conn.getMetaData();
            tables = dbm.getTables(null, null, targetTableName, null);
            if (tables.next()) {
                bTableExists = true;
            }
        } finally {
            RelationalToSqlHelper.tryCloseResultSet(tables);
        }
        return bTableExists;
    }

    @Override
    public AbstractConfigDialog<RelationalToSqlConfig_V1> getConfigurationDialog() {
        return new RelationalToSqlVaadinDialog();
    }

}
