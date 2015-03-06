package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.plugins.extractor.relationalfromsql.SqlDatabase.DatabaseType;

public class RelationalFromSqlHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalFromSqlHelper.class);

    public static List<ColumnDefinition> getTableColumnsFromMetaData(ResultSetMetaData meta) throws SQLException {
        int columnsCount = meta.getColumnCount();
        List<ColumnDefinition> columns = new ArrayList<>();
        Set<String> uniqueColumns = new HashSet<String>();
        // If result set contains multiple columns with the same name, add index
        for (int i = 1; i <= columnsCount; i++) {
            int type = meta.getColumnType(i);
            String columnLabel = meta.getColumnLabel(i);
            String typeName = meta.getColumnTypeName(i);
            String typeClass = meta.getColumnClassName(i);
            typeName = convertColumnTypeIfNeeded(typeName, type);
            LOG.debug("Column name: {}, type name: {}, SQL type: {}", columnLabel, typeName, type);
            if (isSupportedDataType(type, typeName)) {
                boolean columnNotNull = (meta.isNullable(i) == ResultSetMetaData.columnNoNulls);
                if (uniqueColumns.contains(columnLabel)) {
                    int index = 1;
                    String newLabel = columnLabel + "_" + index;
                    while (uniqueColumns.contains(newLabel)) {
                        index++;
                        newLabel = columnLabel + "_" + index;
                    }
                    columnLabel = newLabel;
                }
                uniqueColumns.add(columnLabel);
                ColumnDefinition column = new ColumnDefinition(columnLabel, typeName, type, columnNotNull, typeClass);
                columns.add(column);
            } else {
                LOG.warn("Unsupported column skipped: Name: {}, Data type: {}", columnLabel, typeName);
            }
        }

        return columns;
    }

    private static boolean isSupportedDataType(int type, String typeName) {
        switch (type) {
            case Types.CLOB:
            case Types.BLOB:
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.STRUCT:
            case Types.DISTINCT:
            case Types.REF:
            case Types.JAVA_OBJECT:
            case Types.OTHER:
            case Types.ARRAY:
                return false;
            default:
                return true;
        }
    }

    public static void tryCloseDbResources(Connection conn, Statement stmnt, ResultSet rs) {
        tryCloseResultSet(rs);
        tryCloseStatement(stmnt);
        tryCloseConnection(conn);
    }

    public static void tryRollbackConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                LOG.warn("Error occurred during rollback of connection", e);
            }
        }
    }

    public static void tryCloseStatement(Statement stmnt) {
        try {
            if (stmnt != null) {
                stmnt.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error occurred during closing statement", e);
        }
    }

    public static void tryCloseResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error occurred during closing result set", e);
        }
    }

    public static void tryCloseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.warn("Error occurred during closing result set", e);
            }
        }
    }

    public static boolean testDatabaseConnection(RelationalFromSqlConfig_V2 config) {
        boolean bConnectResult = true;
        Connection conn = null;
        try {
            conn = createConnection(config);
        } catch (Exception e) {
            LOG.error("Failed to establish connection to the database", e);
            bConnectResult = false;
        } finally {
            tryCloseConnection(conn);
        }

        return bConnectResult;
    }

    public static Connection createConnection(RelationalFromSqlConfig_V2 config) throws SQLException {

        try {
            Class.forName(SqlDatabase.getJdbcDriverNameForDatabase(config.getDatabaseType()));
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver", e);
        }

        Connection connection = null;
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", config.getUserName());
        connectionProperties.setProperty("password", config.getUserPassword());
        // TODO: Do we need to validate client / server certificates?
        if (config.getDatabaseType() == DatabaseType.POSTGRES && config.isUseSSL()) {
            connectionProperties.setProperty("ssl", "true");
            // TODO: SSL support for other databases generically
            connectionProperties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        }
        connection = DriverManager.getConnection(createDatabaseUrlFromConfig(config), connectionProperties);
        connection.setAutoCommit(false);

        return connection;
    }

    private static String createDatabaseUrlFromConfig(RelationalFromSqlConfig_V2 config) {
        StringBuilder url = new StringBuilder();
        url.append(SqlDatabase.getJdbcUrlPrefixForDatabase(config.getDatabaseType()));
        url.append(config.getDatabaseHost());
        if (config.getDatabasePort() != 0) {
            url.append(":");
            url.append(config.getDatabasePort());
        }
        if (config.getDatabaseType() == DatabaseType.H2_MEM || config.getDatabaseType() == DatabaseType.ORACLE) {
            url.append(":");
        } else if (config.getDatabaseType() == DatabaseType.MSSQL) {
            url.append(";databaseName=");
        } else {
            url.append("/");
        }
        url.append(config.getDatabaseName());

        if (config.getDatabaseType() == DatabaseType.MSSQL && config.getInstanceName() != null) {
            url.append(";instanceName=");
            url.append(config.getInstanceName());
        }

        return url.toString();
    }

    public static List<String> getColumnsForTable(RelationalFromSqlConfig_V2 config, String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        Connection connection = null;
        ResultSet tableColumns = null;
        try {
            connection = createConnection(config);
            DatabaseMetaData meta = connection.getMetaData();
            tableColumns = meta.getColumns(null, null, tableName, null);
            while (tableColumns.next()) {
                int type = tableColumns.getInt("DATA_TYPE");
                String typeName = tableColumns.getString("TYPE_NAME");
                if (isSupportedDataType(type, typeName)) {
                    columns.add(tableColumns.getString("COLUMN_NAME"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error in getColumnsForTable()", e);
            throw e;
        } finally {
            tryCloseResultSet(tableColumns);
            tryCloseConnection(connection);
        }

        return columns;
    }

    public static List<DatabaseTable> getTablesInSourceDatabase(RelationalFromSqlConfig_V2 config) throws SQLException {
        List<DatabaseTable> tables = new ArrayList<>();
        Connection connection = null;
        ResultSet dbTables = null;
        try {
            connection = createConnection(config);
            DatabaseMetaData meta = connection.getMetaData();
            if (config.getDatabaseType() == DatabaseType.ORACLE) {
                dbTables = meta.getTables(null, config.getUserName().toUpperCase(), "%", new String[] { "TABLE", "VIEW" });
            } else {
                dbTables = meta.getTables(null, null, "%", new String[] { "TABLE", "VIEW" });
            }
            while (dbTables.next()) {
                String tableName = dbTables.getString("TABLE_NAME");
                if (config.getDatabaseType() == DatabaseType.MSSQL) {
                    String tableSchema = dbTables.getString("TABLE_SCHEM");
                    if (!tableSchema.equals("sys")) {
                        tables.add(new DatabaseTable(tableName, tableSchema));
                    }
                } else {
                    tables.add(new DatabaseTable(dbTables.getString("TABLE_NAME")));
                }

            }
        } catch (SQLException e) {
            LOG.error("Error in getTablesInSourceDatabase()", e);
            throw e;
        } finally {
            tryCloseResultSet(dbTables);
            tryCloseConnection(connection);
        }

        return tables;
    }

    private static String convertColumnTypeIfNeeded(String columnTypeName, int columnSqlType) {
        switch (columnSqlType) {
            case Types.INTEGER:
                if (!DataTypes.isDataTypeSupported(columnTypeName)) {
                    return "INTEGER";
                }
            case Types.ARRAY:
                if (!DataTypes.isDataTypeSupported(columnTypeName)) {
                    return "ARRAY";
                }
            case Types.VARCHAR:
                if (!DataTypes.isDataTypeSupported(columnTypeName)) {
                    return "VARCHAR";
                }
            default:
                return columnTypeName;
        }
    }

    public static String getNormalizedQuery(String query) {
        if (query == null) {
            return null;
        }
        String normalizedQuery = query.trim();
        if (normalizedQuery.endsWith(";")) {
            normalizedQuery = normalizedQuery.substring(0, normalizedQuery.length() - 1);
        }

        return normalizedQuery;
    }

    public static String generateSelectForTable(DatabaseTable table, List<String> columns) {
        StringBuilder query = new StringBuilder("SELECT ");
        for (String column : columns) {
            query.append("\t");
            if (column.contains(" ")) {
                query.append("\"");
                query.append(column);
                query.append("\"");
            } else {
                query.append(column);
            }

            query.append(",\n");
        }
        query.setLength(query.length() - 2);
        query.append("\n");
        query.append(" FROM ");
        if (table.getTableSchema() != null) {
            query.append(table.getTableSchema());
            query.append(".");
        }
        query.append(table.getTableName());

        return query.toString();
    }

    public static String getPrimaryKeysAsCommaSeparatedString(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

}
