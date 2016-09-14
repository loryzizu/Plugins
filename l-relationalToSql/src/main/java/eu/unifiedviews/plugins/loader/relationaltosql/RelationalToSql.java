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
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.relational.RelationalHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;

@DPU.AsLoader
public class RelationalToSql extends AbstractDpu<RelationalToSqlConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalToSql.class);

    @DataUnit.AsInput(name = "input")
    public RelationalDataUnit inTablesData;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.loader.relationaltosql.RelationalToSqlConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public RelationalToSql() {
        super(RelationalToSqlVaadinDialog.class, ConfigHistory.noHistory(RelationalToSqlConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: Database host name: %s, port: %s, database name: %s, " +
                        "username: %s, password: %s, useSSL: %s, targetTable: %s, clearTargetTable: %s, dropTargetTable: %s",
                this.config.getDatabaseHost(),
                this.config.getDatabasePort(),
                this.config.getDatabaseName(),
                this.config.getUserName(), "***",
                this.config.isUseSSL(),
                this.config.getTableNamePrefix(),
                this.config.isClearTargetTable(),
                this.config.isDropTargetTable());
        LOG.info(shortMessage + " " + longMessage);

        try {
            Class.forName(DatabaseConfig.getDatabaseInfo(this.config.getDatabaseType()).getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            throw ContextUtils.dpuException(ctx, ("errors.driver.loadfailed"), e);
        }

        Iterator<RelationalDataUnit.Entry> tablesIteration;
        try {
            tablesIteration = RelationalHelper.getTables(this.inTablesData).iterator();
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "errors.tables.iterator");
        }

        boolean bTableExists = false;
        PreparedStatement insertStmnt = null;
        Connection conn = null;
        int index = 1;
        List<ColumnDefinition> sourceColumns = new ArrayList<>();
        try {
            conn = RelationalToSqlHelper.createConnection(this.config);
            if (config.isOneTable()) {
                bTableExists = checkTableExists(conn, this.config.getTableNamePrefix());
                if (bTableExists) {
                    LOG.debug("Target table already exists");
                    if (this.config.isDropTargetTable()) {
                        LOG.info(String.format("Dropping table %s", this.config.getTableNamePrefix()));
                        dropTable(conn, this.config.getTableNamePrefix());
                        bTableExists = false;
                    } else if (this.config.isClearTargetTable()) {
                        LOG.info(String.format("Truncating table %s", this.config.getTableNamePrefix()));
                        truncateTable(conn);
                    }
                }

                if (config.isUserDefined()) {
                    sourceColumns = config.getColumnDefinitions();
                } else {
                    sourceColumns = getColumnDefinitionsFromSourceTable(config.getTableNamePrefix());
                }

                if (!bTableExists) {
                    LOG.info("Target table does not exist. Recreating");
                    createTable(conn, config.getTableNamePrefix(), sourceColumns);
                } else {
                    if (!checkTablesConsistent(conn, config.getTableNamePrefix(), sourceColumns)) {
                        throw ContextUtils.dpuException(ctx, "errors.table.inconsistent");
                    }
                }
            }

            while (!this.ctx.canceled() && tablesIteration.hasNext()) {
                if (config.isOneTable()) {
                    index = 0;
                } else {
                    index++;
                }

                final RelationalDataUnit.Entry entry = tablesIteration.next();
                final String sourceTableName = entry.getTableName();
                final String targetTableName = createTargetTableName(index);

                try {
                    if (!config.isOneTable()) {
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

                        if (config.isUserDefined()) {
                            sourceColumns = config.getColumnDefinitions();
                        } else {
                            sourceColumns = getColumnDefinitionsFromSourceTable(sourceTableName);
                        }

                        if (!bTableExists) {
                            LOG.info("Target table does not exist. Recreating");
                            createTable(conn, targetTableName, sourceColumns);
                        } else {
                            if (!checkTablesConsistent(conn, targetTableName, sourceColumns)) {
                                throw ContextUtils.dpuException(ctx, "errors.table.inconsistent");
                            }
                        }
                    }
                    String insertQuery = QueryBuilder.getInsertQueryForPreparedStatement(targetTableName, sourceColumns);
                    LOG.info("Creating prepared statement for insert into external database using query: {}", insertQuery);
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
            throw ContextUtils.dpuException(ctx, ("errors.dpu.insertfail"), se);
        } catch (Exception e) {
            LOG.error("Error during loading data from internal database to external SQL database", e);
            throw ContextUtils.dpuException(ctx, ("errors.dpu.insertfail"), e);
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
        if (index != 0) {
            sb.append("_");
            sb.append(index);
        }
        return sb.toString();
    }

    private final void createTable(Connection conn, String targetTableName, List<ColumnDefinition> sourceColumns) throws SQLException {
        Statement stmnt = null;
        try {
            String query = QueryBuilder.getQueryForCreateTable(targetTableName, sourceColumns);
            LOG.info("Creating table in external database using query: {}", query);

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

    private List<ColumnDefinition> getColumnDefinitionsFromSourceTable(String sourceTableName) throws Exception {
        List<ColumnDefinition> columns = new ArrayList<>();
        ResultSet rs = null;
        DatabaseMetaData dbm = null;
        Connection conn = null;
        try {
            conn = this.inTablesData.getDatabaseConnection();
            dbm = conn.getMetaData();
            rs = dbm.getColumns(null, null, sourceTableName, null);
            String dbType  = config.getDatabaseName();
            while (rs.next()) {
                LOG.info(String.format("Column - name: %s, datatype name: %s, jdbc datatype: %s, size: %s, nullable: %s ",
                        rs.getString("COLUMN_NAME").toLowerCase(),
                        rs.getString("TYPE_NAME"),
                        rs.getInt("DATA_TYPE"),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getString("IS_NULLABLE")));
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = mapJdbcDatatypeToDatabaseDatatype(rs.getString("TYPE_NAME"), rs.getInt("DATA_TYPE"), dbType);
                if (columnType == null) {
                    throw new SQLException("Column type cannot be decided automatically, please try to add column definitions manually");
                }
                columns.add(new ColumnDefinition(columnName,
                        columnType,
                        rs.getInt("NULLABLE") == 0,
                        0));
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

    private String mapJdbcDatatypeToDatabaseDatatype(String typeName, int typeId, String dbType) {
        if (dbType.equals("PostgreSQL")) {
            if (SqlDatatype.POSTGRESQL_DATATYPE.containsKey(typeName)) {
                return SqlDatatype.POSTGRESQL_DATATYPE.get(typeName).getDatatypeName();
            } else if (SqlDatatype.JDBC_TO_POSTGRESQL_DATATYPE.containsKey(typeId)) {
                return SqlDatatype.JDBC_TO_POSTGRESQL_DATATYPE.get(typeId).getDatatypeName();
            } else {
                return null;
            }
        } else if (dbType.equals("ORACLE")) {
            if (SqlDatatype.ORACLE_DATATYPE.containsKey(typeName)) {
                return SqlDatatype.ORACLE_DATATYPE.get(typeName).getDatatypeName();
            } else if (SqlDatatype.JDBC_TO_ORACLE_DATATYPE.containsKey(typeId)) {
                return SqlDatatype.JDBC_TO_ORACLE_DATATYPE.get(typeId).getDatatypeName();
            } else {
                return null;
            }
        } else {
            return null;
        }
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
                targetColumns.put(columnName.toUpperCase(), new ColumnDefinition(
                        columnName,
                        rs.getString("TYPE_NAME"),
                        rs.getInt("NULLABLE") == 0,
                        0));
            }
        } catch (SQLException e) {
            throw new Exception("Failed to check consistency between existing table and provided files");
        } finally {
            RelationalToSqlHelper.tryCloseResultSet(rs);
        }

        for (ColumnDefinition sourceColumn : sourceColumns) {
            if (!targetColumns.containsKey(sourceColumn.getColumnName().toUpperCase())) {
                bTableConsistent = false;
                break;
            }
            // Datatypes of two columns are considered equal As long as their JDBC datatypes are equal
            ColumnDefinition targetColumn = targetColumns.get(sourceColumn.getColumnName().toUpperCase());
            if (!SqlDatatype.ALL_DATATYPE.containsKey(targetColumn.getColumnType())
                    || !SqlDatatype.ALL_DATATYPE.containsKey(sourceColumn.getColumnType())
                    || SqlDatatype.ALL_DATATYPE.get(targetColumn.getColumnType()).getSqlTypeId()
                        != SqlDatatype.ALL_DATATYPE.get(sourceColumn.getColumnType()).getSqlTypeId()
                    || targetColumn.isColumnNotNull() != sourceColumn.isColumnNotNull()) {
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
            tables = dbm.getTables(null, null, targetTableName.toLowerCase(), null);
            if (tables.next()) {
                bTableExists = true;
            }
        } finally {
            RelationalToSqlHelper.tryCloseResultSet(tables);
        }
        return bTableExists;
    }

}
