package eu.unifiedviews.plugins.transformer.relational;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseHelperTest {

    private static final String TABLE_NAME_1 = "INTERNAL_TABLE_1";

    private static final String SELECT_QUERY_1 = "SELECT * FROM USERS";

    private static final String EXPECTED_CREATE_TABLE_QUERY_1 = "CREATE TABLE " + TABLE_NAME_1 + " AS " + SELECT_QUERY_1;

    private static final String TABLE_NAME_2 = "INTERNAL_TABLE_2";

    private static final String SELECT_QUERY_2 = "SELECT * FROM USERS JOIN ADDRESS ON USERS.ID = ADDRESS.USER_ID";

    private static final String EXPECTED_CREATE_TABLE_QUERY_2 = "CREATE TABLE " + TABLE_NAME_2 + " AS " + SELECT_QUERY_2;

    @Test
    public void convertSelectQueryToSelectIntoQueryTest() {
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY_1, DatabaseHelper.convertSelectQueryToSelectIntoQuery(SELECT_QUERY_1, TABLE_NAME_1));
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY_2, DatabaseHelper.convertSelectQueryToSelectIntoQuery(SELECT_QUERY_2, TABLE_NAME_2));
    }

}
