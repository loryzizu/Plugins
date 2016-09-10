package eu.unifiedviews.plugins.loader.relationaltosql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        LOG.debug(String.format("SQL insert query created: %s", query.toString()));

        return query.toString();
    }

    public static void fillInsertQueryData(PreparedStatement stmt, ResultSet rs, List<ColumnDefinition> columns) throws SQLException {
        for (int i = 1; i <= columns.size(); i++) {
            stmt.setObject(i, rs.getObject(i));
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
            query.append(column.getColumnTypeName());
            if (column.getColumnSize() > 4000) {
                query.append("(4000)");
            } else {
                query.append("(" + column.getColumnSize() + ")");
            }
            query.append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");

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

        return query.toString();
    }

}
