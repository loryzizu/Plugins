package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.SQLException;
import java.util.List;

public class QueryBuilder {

    /**
     * Generates INSERT query for prepared statement (values as ?) with given column names and for given table
     * 
     * @param columns
     *            Columns to insert into
     * @param tableName
     *            Table to insert into
     * @return INSERT query for prepared statement
     * @throws SQLException
     */
    public static String getInsertQueryForPreparedStatement(List<ColumnDefinition> columns, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        for (ColumnDefinition column : columns) {
            query.append(column.getColumnName());
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(") VALUES (");

        for (int i = 1; i <= columns.size(); i++) {
            query.append("?");
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(")");

        return query.toString();
    }

    /**
     * Generates CREATE table query with given column definitions
     * 
     * @param columns
     *            List of column definitions
     * @param tableName
     *            Name of created table
     * @return CREATE TABLE query
     * @throws SQLException
     */
    public static String getCreateTableQueryFromMetaData(List<ColumnDefinition> columns, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ");
        query.append(tableName);
        query.append(" (");
        for (ColumnDefinition column : columns) {
            query.append(column.getColumnName());
            query.append(" ");
            query.append(column.getColumnTypeName());
            if (column.getColumnSize() != -1) {
                query.append("(");
                query.append(column.getColumnSize());
                query.append(")");
            }
            if (column.isNotNull()) {
                query.append(" ");
                query.append("NOT NULL");
            }
            query.append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");

        return query.toString();
    }

    public static String getPrimaryKeysQuery(String tableName, List<String> primaryKeys) {
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

}
