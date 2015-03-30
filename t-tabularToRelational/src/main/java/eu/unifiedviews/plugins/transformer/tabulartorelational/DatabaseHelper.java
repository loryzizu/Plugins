package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Helping class containing methods to connect to relational dataunit.
 */
public class DatabaseHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TabularToRelational.class);

    /**
     * Executes arbitrary update/insert/create query on input dataunit.
     *
     * @param query Query to execute.
     * @param relationalDataUnit On which dataunit should be query executed.
     * @throws DataUnitException
     */
    public static void executeUpdate(String query, WritableRelationalDataUnit relationalDataUnit) throws DataUnitException {
        Statement stmnt = null;
        Connection conn = null;
        try {
            conn = relationalDataUnit.getDatabaseConnection();
            stmnt = conn.createStatement();
            stmnt.executeUpdate(query);
            conn.commit();
        } catch (SQLException e) {
            LOG.error("Error at executing query to relational dataunit: ", e);
            DatabaseHelper.tryRollbackConnection(conn);
            throw new DataUnitException("Error at executing query: ", e);
        } finally {
            DatabaseHelper.tryCloseStatement(stmnt);
            DatabaseHelper.tryCloseConnection(conn);
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

    public static void tryCloseStatement(Statement stmnt) {
        try {
            if (stmnt != null) {
                stmnt.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error occurred during closing statement", e);
        }
    }

    public static void tryCloseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.warn("Error occurred during rollback of connection", e);
            }
        }
    }
}
