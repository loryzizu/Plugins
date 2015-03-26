package eu.unifiedviews.plugins.transformer.relational;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseHelperTest {

    private static final String TABLE_NAME_1 = "INTERNAL_TABLE_1";

    private static final String SELECT_QUERY_1 = "SELECT * FROM USERS";

    private static final String EXPECTED_CREATE_TABLE_QUERY_1 = "CREATE TABLE " + TABLE_NAME_1 + " AS " + SELECT_QUERY_1;

    private static final String TABLE_NAME_2 = "INTERNAL_TABLE_2";

    private static final String SELECT_QUERY_2 = "SELECT * FROM USERS JOIN ADDRESS ON USERS.ID = ADDRESS.USER_ID";

    private static final String EXPECTED_CREATE_TABLE_QUERY_2 = "CREATE TABLE " + TABLE_NAME_2 + " AS " + SELECT_QUERY_2;

    private static final String PRIMARY_KEY_1 = "user_id";

    private static final String PRIMARY_KEY_2 = "order_id";

    private static final String EXPECTED_ALTER_TABLE_QUERY = "ALTER TABLE " + TABLE_NAME_1 + " ADD PRIMARY KEY (" + PRIMARY_KEY_1 + "," + PRIMARY_KEY_2 + ")";

    private static final String INDEXED_COLUMN = "user_id";

    private static final String EXPECTED_INDEXED_QUERY = "CREATE INDEX " + INDEXED_COLUMN + "_idx ON " + TABLE_NAME_1 + "(" + INDEXED_COLUMN + ")";

    @Test
    public void convertSelectQueryToSelectIntoQueryTest() {
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY_1, DatabaseHelper.convertSelectQueryToSelectIntoQuery(SELECT_QUERY_1, TABLE_NAME_1));
        Assert.assertEquals(EXPECTED_CREATE_TABLE_QUERY_2, DatabaseHelper.convertSelectQueryToSelectIntoQuery(SELECT_QUERY_2, TABLE_NAME_2));
    }

    @Test
    public void createPrimaryKeysQueryTest() {
        List<String> keys = new ArrayList<>();
        keys.add(PRIMARY_KEY_1);
        keys.add(PRIMARY_KEY_2);
        Assert.assertEquals(EXPECTED_ALTER_TABLE_QUERY, DatabaseHelper.createPrimaryKeysQuery(TABLE_NAME_1, keys));
    }

    @Test
    public void getCreateIndexQueryTest() {
        Assert.assertEquals(EXPECTED_INDEXED_QUERY, DatabaseHelper.getCreateIndexQuery(TABLE_NAME_1, INDEXED_COLUMN));
    }

}
