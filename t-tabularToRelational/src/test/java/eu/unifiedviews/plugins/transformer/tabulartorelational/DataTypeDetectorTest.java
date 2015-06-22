package eu.unifiedviews.plugins.transformer.tabulartorelational;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class DataTypeDetectorTest {

    @Test
    public void testAllPossibleDataTypes() {
        List<ColumnMappingEntry> entries = new ArrayList<>();
        ColumnMappingEntry entry = new ColumnMappingEntry();
        entry.setColumnName("col");
        entries.add(entry);
        entry = new ColumnMappingEntry();
        entry.setColumnName("col2");
        entries.add(entry);
        DataTypeDetector detector = new DataTypeDetector(entries);
        // feed detector with samples
        detector.addSample(new String[]{"-123"});
        detector.addSample(new String[]{"abc"});
        detector.addSample(null);
        detector.addSample(new String[]{});

        System.out.println(detector.getAllPossibleDatatypes("col"));
    }
}
