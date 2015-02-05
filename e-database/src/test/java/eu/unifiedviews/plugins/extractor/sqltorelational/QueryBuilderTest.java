package eu.unifiedviews.plugins.extractor.sqltorelational;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.unifiedviews.plugins.extractor.sqltorelational.QueryBuilder;

public class QueryBuilderTest {

    private static final String TABLE_NAME = "db_table";

    private static final String EXPECTED_CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (column1 integer, column2 varchar(255), column3 varchar(255))";

    private static final String EXPECTED_INSERT_QUERY = "INSERT INTO " + TABLE_NAME + " (column1, column2, column3) VALUES (?, ?, ?)";

    @Test
    public void getCreateTableQueryFromMetaDataTest() throws SQLException {
        ResultSetMetaData meta = mockResultSetMetaData();
        String query = QueryBuilder.getCreateTableQueryFromMetaData(meta, TABLE_NAME);
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY, query);
    }

    @Test
    public void getInsertQueryForPreparedStatementTest() throws SQLException {
        ResultSetMetaData meta = mockResultSetMetaData();
        String query = QueryBuilder.getInsertQueryForPreparedStatement(meta, TABLE_NAME);
        Assert.assertEquals(EXPECTED_INSERT_QUERY, query);
    }

    private ResultSetMetaData mockResultSetMetaData() throws SQLException {
        ResultSetMetaData meta = Mockito.mock(ResultSetMetaData.class);

        Mockito.when(meta.getColumnCount()).thenReturn(3);

        Mockito.when(meta.getColumnLabel(1)).thenReturn("column1");
        Mockito.when(meta.getColumnTypeName(1)).thenReturn("integer");
        Mockito.when(meta.getColumnClassName(1)).thenReturn("java.lang.Integer");
        Mockito.when(meta.getPrecision(1)).thenReturn(10);

        Mockito.when(meta.getColumnLabel(2)).thenReturn("column2");
        Mockito.when(meta.getColumnTypeName(2)).thenReturn("varchar");
        Mockito.when(meta.getColumnClassName(2)).thenReturn("java.lang.String");
        Mockito.when(meta.getPrecision(2)).thenReturn(255);

        Mockito.when(meta.getColumnLabel(3)).thenReturn("column3");
        Mockito.when(meta.getColumnTypeName(3)).thenReturn("varchar");
        Mockito.when(meta.getColumnClassName(3)).thenReturn("java.lang.String");
        Mockito.when(meta.getPrecision(3)).thenReturn(255);

        return meta;
    }

}
