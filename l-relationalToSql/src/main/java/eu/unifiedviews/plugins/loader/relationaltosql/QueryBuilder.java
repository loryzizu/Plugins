package eu.unifiedviews.plugins.loader.relationaltosql;

import java.sql.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);

    public static String getInsertQueryForPreparedStatement(String tableName, List<ColumnDefinition> sourceColumns) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        for (ColumnDefinition column : sourceColumns) {
            query.append(column.getColumnName());
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(") VALUES (");

        for (int i = 0; i < sourceColumns.size(); i++) {
            query.append("?");
            query.append(",");
        }
        query.setLength(query.length() - 1);
        query.append(")");

        LOG.info(String.format("SQL insert query created: %s", query.toString()));

        return query.toString();
    }

    public static void fillInsertQueryData(PreparedStatement stmt, ResultSet rs, List<ColumnDefinition> columns) throws SQLException {
        Object value;
        int type;
        for (int i = 1; i <= columns.size(); i++) {
            type = SqlDatatype.ALL_DATATYPE.get(columns.get(i-1).getColumnType()).getSqlTypeId();
            value = rs.getObject(i);
            // TODO: generalize date
            if (value != null && !value.toString().equals("") && type == Types.DATE) {
                value = Timestamp.valueOf(value.toString());
            }
            stmt.setObject(i, value, type);
        }
    }

    public static String getQueryForCreateTable(String tableName, List<ColumnDefinition> columns) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ");
        query.append(tableName);
        query.append(" (");

        for (ColumnDefinition column : columns) {
            query.append(column.getColumnName());
            query.append(" ");
            query.append(column.getColumnType());
            if (column.getColumnSize() > 0) {
                query.append("(");
                query.append(column.getColumnSize());
                query.append(")");
            }
            if (column.isColumnNotNull()) {
                query.append(" NOT NULL ");
            }
            query.append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");

        LOG.info(String.format("SQL table creation query created: %s", query.toString()));

        return query.toString();
    }

    public static String getQueryForTruncateTable(String tableName) {
        StringBuilder query = new StringBuilder();
        query.append("TRUNCATE TABLE ");
        query.append(tableName);

        return query.toString();
    }

    public static String getQueryForDropTable(String tableName) {
        StringBuilder query = new StringBuilder();
        query.append("DROP TABLE ");
        query.append(tableName);

        return query.toString();
    }

    public static String getQueryFromSourceTableSelect(List<ColumnDefinition> columns, String sourceTableName) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        for (ColumnDefinition column : columns) {
            query.append(column.getColumnName());
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(" FROM ");
        query.append(sourceTableName);

        LOG.info(String.format("SQL get query from source table created: %s", query.toString()));

        return query.toString();
    }

}
