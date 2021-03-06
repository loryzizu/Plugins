package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class QueryBuilderTest {

    private static final String TABLE_NAME = "db_table";

    private static final String EXPECTED_CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (column1 integer NOT NULL, column2 varchar(255) NOT NULL, column3 varchar(255))";

    private static final String EXPECTED_INSERT_QUERY = "INSERT INTO " + TABLE_NAME + " (column1, column2, column3) VALUES (?, ?, ?)";

    private static final ColumnDefinition COLUMN_1 = new ColumnDefinition("column1", "integer", Types.INTEGER, true, "java.lang.Integer", -1);

    private static final ColumnDefinition COLUMN_2 = new ColumnDefinition("column2", "varchar", Types.VARCHAR, true, "java.lang.String", 255);

    private static final ColumnDefinition COLUMN_3 = new ColumnDefinition("column3", "varchar", Types.VARCHAR, false, "java.lang.String", 255);

    @Test
    public void getCreateTableQueryFromMetaDataTest() throws SQLException {
        List<ColumnDefinition> columns = Arrays.asList(COLUMN_1, COLUMN_2, COLUMN_3);
        String query = QueryBuilder.getCreateTableQueryFromMetaData(columns, TABLE_NAME);
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY, query);
    }

    @Test
    public void getInsertQueryForPreparedStatementTest() throws SQLException {
        List<ColumnDefinition> columns = Arrays.asList(COLUMN_1, COLUMN_2, COLUMN_3);
        String query = QueryBuilder.getInsertQueryForPreparedStatement(columns, TABLE_NAME);
        Assert.assertEquals(EXPECTED_INSERT_QUERY, query);
    }

    private ResultSetMetaData mockResultSetMetaData() throws SQLException {
        ResultSetMetaData meta = Mockito.mock(ResultSetMetaData.class);

        Mockito.when(meta.getColumnCount()).thenReturn(3);

        Mockito.when(meta.getColumnLabel(1)).thenReturn("column1");
        Mockito.when(meta.getColumnTypeName(1)).thenReturn("integer");
        Mockito.when(meta.getColumnClassName(1)).thenReturn("java.lang.Integer");
        Mockito.when(meta.isNullable(1)).thenReturn(ResultSetMetaData.columnNoNulls);
        Mockito.when(meta.getPrecision(1)).thenReturn(10);

        Mockito.when(meta.getColumnLabel(2)).thenReturn("column2");
        Mockito.when(meta.getColumnTypeName(2)).thenReturn("varchar");
        Mockito.when(meta.getColumnClassName(2)).thenReturn("java.lang.String");
        Mockito.when(meta.isNullable(2)).thenReturn(ResultSetMetaData.columnNoNulls);
        Mockito.when(meta.getPrecision(2)).thenReturn(255);

        Mockito.when(meta.getColumnLabel(3)).thenReturn("column3");
        Mockito.when(meta.getColumnTypeName(3)).thenReturn("varchar");
        Mockito.when(meta.getColumnClassName(3)).thenReturn("java.lang.String");
        Mockito.when(meta.isNullable(3)).thenReturn(ResultSetMetaData.columnNullable);
        Mockito.when(meta.getPrecision(3)).thenReturn(255);

        return meta;
    }

}
