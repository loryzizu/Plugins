package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
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

    // TODO: maybe add some filtering of data types --> probably not all types are supported by H2 database
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
            // convert some specific column data types to general type
            query.append(convertColumnTypeIfNeeded(meta.getColumnTypeName(i)));
            // Add size modification to String data types
            if (shouldAppendSizeToColumnType(meta.getColumnClassName(i))) {
                query.append("(");
                query.append(meta.getPrecision(i));
                query.append(")");
            }
            if (meta.isNullable(i) == ResultSetMetaData.columnNoNulls) {
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

    private static String convertColumnTypeIfNeeded(String columnTypeName) {
        switch (columnTypeName.toLowerCase()) {
            case "serial":
                return "integer";
            default:
                return columnTypeName;
        }
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
