package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    public static boolean checkTableExists(Connection conn, String targetTableName) throws SQLException {
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
            tryCloseResultSet(tables);
        }
        return bTableExists;
    }

    public static String convertSelectQueryToSelectIntoQuery(String query, String targetTable) {
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE ");
        createQuery.append(targetTable);
        createQuery.append(" AS ");
        createQuery.append(query);

        return createQuery.toString().toUpperCase();
    }

    public static String createAlterColumnSetNotNullQuery(String tableName, String keyColumn) {
        StringBuilder query = new StringBuilder();
        query.append("ALTER TABLE ");
        query.append(tableName);
        query.append(" ");
        query.append("ALTER COLUMN ");
        query.append(keyColumn);
        query.append(" ");
        query.append("SET NOT NULL");

        return query.toString();
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

    public static void tryRollbackConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                LOG.warn("Error occurred during rollback of connection", e);
            }
        }
    }

    public static String createPrimaryKeysQuery(String tableName, List<String> primaryKeys) {
        StringBuilder query = new StringBuilder("ALTER TABLE ");
        query.append(tableName);
        query.append(" ADD PRIMARY KEY (");
        for (String key : primaryKeys) {
            query.append(key);
            query.append(",");
        }
        query.setLength(query.length() - 1);
        query.append(")");

        return query.toString();
    }

    public static void tryCloseDbResources(Connection conn, Statement stmnt, ResultSet rs) {
        tryCloseResultSet(rs);
        tryCloseStatement(stmnt);
        tryCloseConnection(conn);
    }

}
