package eu.unifiedviews.plugins.transformer.tabulartorelational;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Detecting of possible SQL datatypes from string input.
 *
 * First feed the instance of the class with sample data, and then get set of all possible data types.
 */
public class DataTypeDetector {

    private List<ColumnMappingEntry> columnMapping;

    private Map<String, Set<DataType>> columnDataTypes;

    public DataTypeDetector(List<ColumnMappingEntry> columnMapping) {
        this.columnDataTypes = new HashMap<>();
        this.columnMapping = columnMapping;
    }

    public void addSample(String[] strings) {
        if(strings == null) {
            return;
        }

        for (int i = 0; i < columnMapping.size(); i++) {
            // if column name is not present in data type map, create empty one
            if (!columnDataTypes.containsKey(columnMapping.get(i).getColumnName())) {
                columnDataTypes.put(columnMapping.get(i).getColumnName(), EnumSet.noneOf(DataType.class));
            }

            // check, if we have enough data in sample, if not, ignore it
            if (i < strings.length) {
                String value = strings[i];
                Set<DataType> possibleDataTypes = columnDataTypes.get(columnMapping.get(i).getColumnName());
                // for all data types, check if pattern passes
                for (DataType type : DataType.values()) {
                    Matcher matcher = type.getPattern().matcher(value);
                    if (matcher.matches()) {
                        // here we will implements additional checks for particular data types
                        switch (type) {
                            case INTEGER:
                                try {
                                    // for integer, we need to check if number will not overflow max integer value
                                    Integer.valueOf(value); // we dont need the result
                                } catch (NumberFormatException e) {
                                    possibleDataTypes.remove(type);
                                    continue;
                                }
                                break;
                            default:
                        }
                        // add this data type to set of possible data types
                        possibleDataTypes.add(type);
                    } else {
                        // remove type form set of possible data types
                        possibleDataTypes.remove(type);
                    }
                }

            }
        }
    }

    public Set<DataType> getAllPossibleDatatypes(String columnName) {
        return columnDataTypes.get(columnName);
    }
}
