package eu.unifiedviews.plugins.extractor.sqltorelational;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class QueryBuilder {

    public static String getInsertQueryForPreparedStatement(ResultSetMetaData meta, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            query.append(meta.getColumnLabel(i));
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(") VALUES (");

        for (int i = 1; i <= meta.getColumnCount(); i++) {
            query.append("?");
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(")");

        return query.toString();
    }

    public static String getCreateTableQueryFromMetaData(ResultSetMetaData meta, String tableName) throws SQLException {
        int columnsCount = meta.getColumnCount();
        StringBuilder query = new StringBuilder();
        Set<String> columns = new HashSet<String>();
        query.append("CREATE TABLE ");
        query.append(tableName);
        query.append(" (");
        // If result set contains multiple columns with the same name, add index
        for (int i = 1; i <= columnsCount; i++) {
            String columnLabel = meta.getColumnLabel(i);
            if (columns.contains(columnLabel)) {
                int index = 1;
                String newLabel = columnLabel + "_" + index;
                while (columns.contains(newLabel)) {
                    index++;
                    newLabel = columnLabel + "_" + index;
                }
                columnLabel = newLabel;
            }
            columns.add(columnLabel);
            query.append(columnLabel);
            query.append(" ");
            query.append(meta.getColumnTypeName(i));
            // Add size modification to String data types
            if (shouldAppendSizeToColumnType(meta.getColumnClassName(i))) {
                query.append("(");
                query.append(meta.getPrecision(i));
                query.append(")");
            }
            query.append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");

        return query.toString();
    }

    private static boolean shouldAppendSizeToColumnType(String typeClass) {
        boolean bResult = false;
        try {
            if (Class.forName(typeClass).isAssignableFrom(String.class)) {
                bResult = true;
            }
        } catch (ClassNotFoundException e) {
            // just ignore
        }

        return bResult;
    }

}
