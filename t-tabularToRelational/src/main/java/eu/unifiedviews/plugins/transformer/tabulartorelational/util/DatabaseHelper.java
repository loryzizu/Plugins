package eu.unifiedviews.plugins.transformer.tabulartorelational.util;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelational;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helping class containing methods to connect to relational dataunit.
 */
public class DatabaseHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TabularToRelational.class);

    /**
     * Executes arbitrary update/insert/create query on input dataunit.
     *
     * @param query              Query to execute.
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

    public static class BulkInserter implements Closeable {

        private final WritableRelationalDataUnit relationalDataUnit;

        private final String tableName;

        private final List<ColumnMappingEntry> columnMappingEntryList;

        private Connection connection;

        private PreparedStatement preparedStatement;

        private final int BATCH_SIZE = 1000;

        private int count = 0;

        public BulkInserter (WritableRelationalDataUnit relationalDataUnit, String tableName, List<ColumnMappingEntry> columnMappingEntryList) {
            this.relationalDataUnit = relationalDataUnit;
            this.tableName = tableName;
            this.columnMappingEntryList = columnMappingEntryList;
        }

        public void insertData(List<String> data) throws DataUnitException, SQLException {
            // lazily create prepared statement
            if(preparedStatement == null) {
                connection = relationalDataUnit.getDatabaseConnection();
                connection.setAutoCommit(false);
                preparedStatement = buildPreparedStatement();
            }

            // load data to prepared statement
            for(int i=0; i < data.size(); i++) {
                preparedStatement.setString(i+1, data.get(i));
            }
            preparedStatement.addBatch();
            // execute batch after fixed amount of data
            if(++count % BATCH_SIZE == 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }
        }

        public void close() {
            try {
                preparedStatement.executeBatch(); // execute batch also for the rest of the data
                connection.commit();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                LOG.error("Error at executing query to relational dataunit: ", e);
                DatabaseHelper.tryRollbackConnection(connection);
            } finally {
                DatabaseHelper.tryCloseConnection(connection);
            }
        }

        private PreparedStatement buildPreparedStatement() throws SQLException, DataUnitException {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("INSERT INTO %s (%s) VALUES (", tableName, StringUtils.join(getColumnNamesFromColumnMappings(columnMappingEntryList), ", ")));
            for(int i=0; i < columnMappingEntryList.size(); i++) {
                if(i == 0) {
                    sb.append("?");
                } else {
                    sb.append(", ?");
                }
            }
            sb.append(");");
            LOG.debug("Created preparedStatement from SQL: " + sb.toString());
            return  connection.prepareStatement(sb.toString());
        }

        private List<String> getColumnNamesFromColumnMappings(List<ColumnMappingEntry> columnMappingEntryList) {
            List<String> list = new ArrayList<>();
            for (ColumnMappingEntry e : columnMappingEntryList) {
                list.add(e.getColumnName());
            }
            return list;
        }
    }
}
