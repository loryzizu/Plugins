package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

//    public static String convertSelectQueryToSelectIntoQuery(String query, String targetTable) throws Exception {
//        String queryUpperCase = query.toUpperCase().trim();
//        String[] tokens = queryUpperCase.split(" ");
//        int fromIndex = -1;
//        int index = 0;
//        for (String token : tokens) {
//            if (token.equals("FROM")) {
//                fromIndex = index;
//                break;
//            }
//            index++;
//        }
//
//        if (fromIndex == -1) {
//            throw new Exception("FROM not found in SQL query, invalid query");
//        }
//
//        StringBuilder insertIntoQuery = new StringBuilder();
//        for (int i = 0; i < fromIndex; i++) {
//            insertIntoQuery.append(tokens[i]);
//            insertIntoQuery.append(" ");
//        }
//        insertIntoQuery.append("INTO ");
//        insertIntoQuery.append(targetTable);
//        insertIntoQuery.append(" ");
//
//        for (int i = fromIndex; i < tokens.length; i++) {
//            insertIntoQuery.append(tokens[i]);
//            insertIntoQuery.append(" ");
//        }
//        insertIntoQuery.setLength(insertIntoQuery.length() - 1);
//
//        return insertIntoQuery.toString();
//    }

    public static String convertSelectQueryToSelectIntoQuery(String query, String targetTable) {
        StringBuilder createQuery = new StringBuilder();
        createQuery.append("CREATE TABLE ");
        createQuery.append(targetTable);
        createQuery.append(" AS ");
        createQuery.append(query);

        return createQuery.toString().toUpperCase();
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

    public static void tryCloseDbResources(Connection conn, Statement stmnt, ResultSet rs) {
        tryCloseResultSet(rs);
        tryCloseStatement(stmnt);
        tryCloseConnection(conn);
    }

}
