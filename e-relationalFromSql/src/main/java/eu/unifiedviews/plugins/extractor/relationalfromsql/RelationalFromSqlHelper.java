package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
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
                ColumnDefinition column = new ColumnDefinition(columnLabel, typeName, type, columnNotNull);
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

    public static boolean testDatabaseConnection(RelationalFromSqlConfig_V1 config) {
        boolean bConnectResult = true;
        try {
            Class.forName(config.getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            LOG.error("Failed to load driver for the database", e);
            bConnectResult = false;
            return bConnectResult;
        }

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

    public static Connection createConnection(RelationalFromSqlConfig_V1 config) throws SQLException {
        Connection connection = null;
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", config.getUserName());
        connectionProperties.setProperty("password", config.getUserPassword());
        // TODO: Do we need to validate client / server certificates?
        if (config.isUseSSL()) {
            connectionProperties.setProperty("ssl", "true");
            // TODO: SSL support for other databases generically
            connectionProperties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        }
        connection = DriverManager.getConnection(config.getDatabaseURL(), connectionProperties);
        connection.setAutoCommit(false);

        return connection;
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

}
