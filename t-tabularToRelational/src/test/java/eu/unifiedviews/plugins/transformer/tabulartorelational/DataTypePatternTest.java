package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.plugins.transformer.tabulartorelational.unused.DataType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DataTypePatternTest {

    @Test
    public void testInteger() {
        assertTrue(DataType.INTEGER.getPattern().matcher("1").matches());
        assertTrue(DataType.INTEGER.getPattern().matcher("-1").matches());
        assertTrue(DataType.INTEGER.getPattern().matcher("131562456").matches());
        assertTrue(DataType.INTEGER.getPattern().matcher("0").matches());

        assertFalse(DataType.INTEGER.getPattern().matcher("1.0").matches());
        assertFalse(DataType.INTEGER.getPattern().matcher("boo").matches());
        assertFalse(DataType.INTEGER.getPattern().matcher("100a").matches());
        assertFalse(DataType.INTEGER.getPattern().matcher("-1.0").matches());
        assertFalse(DataType.INTEGER.getPattern().matcher("").matches());
    }

    @Test
    public void testDecimal() {
        assertTrue(DataType.DECIMAL.getPattern().matcher("1").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("-1").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("131562456").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("0").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("1.0").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("-1.0").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("0.16895196").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("-0.1561").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("1141.0996").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("-100.0001").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("1141,0996").matches());
        assertTrue(DataType.DECIMAL.getPattern().matcher("-100,0001").matches());

        assertFalse(DataType.DECIMAL.getPattern().matcher("-").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("1.0.0").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("1..0").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("1.,0").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("1.0a").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher(".1").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("1.").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("boo").matches());
        assertFalse(DataType.DECIMAL.getPattern().matcher("").matches());
    }

    @Test
    public void testVarchar() {
        assertTrue(DataType.VARCHAR.getPattern().matcher("1").matches());
        assertTrue(DataType.VARCHAR.getPattern().matcher("a").matches());
        assertTrue(DataType.VARCHAR.getPattern().matcher("").matches());
    }
}
