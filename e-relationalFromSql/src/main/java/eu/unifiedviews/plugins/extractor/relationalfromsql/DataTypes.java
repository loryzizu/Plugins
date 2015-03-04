package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides list of supported SQL types
 */
public class DataTypes {

    private static Set<String> DATA_TYPE_NAMES = new HashSet<>();

    private static Map<String, String[]> DATA_TYPES = new HashMap<>();

    static {
        add("STRING", new String[] { "VARCHAR", "VARCHAR2", "NVARCHAR", "NVARCHAR2",
                "VARCHAR_CASESENSITIVE", "CHARACTER VARYING", "TID",
                "LONGVARCHAR", "LONGNVARCHAR" });
        add("STRING_FIXED", new String[] { "CHAR", "CHARACTER", "NCHAR" });
        add("STRING_IGNORECASE", new String[] { "VARCHAR_IGNORECASE" });
        add("BOOLEAN", new String[] { "BOOLEAN", "BIT", "BOOL" });
        add("BYTE", new String[] { "TINYINT" });
        add("SHORT", new String[] { "SMALLINT", "YEAR", "INT2" });
        add("INT", new String[] { "INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED" });
        add("LONG", new String[] { "BIGINT", "INT8", "LONG", "IDENTITY", "BIGSERIAL" });
        add("DECIMAL", new String[] { "DECIMAL", "DEC", "NUMERIC", "NUMBER" });
        add("FLOAT", new String[] { "REAL", "FLOAT4" });
        add("DOUBLE", new String[] { "DOUBLE", "DOUBLE PRECISION", "FLOAT", "FLOAT8" });
        add("TIME", new String[] { "TIME" });
        add("DATE", new String[] { "DATE" });
        add("TIMESTAMP", new String[] { "TIMESTAMP", "DATETIME", "DATETIME2", "SMALLDATETIME" });
    }

    private static void add(String type, String[] typeNames) {
        DATA_TYPES.put(type, typeNames);
        for (String typeName : typeNames) {
            DATA_TYPE_NAMES.add(typeName);
        }
    }

    public static boolean isDataTypeSupported(String typeName) {
        return DATA_TYPE_NAMES.contains(typeName.toUpperCase());
    }

}
