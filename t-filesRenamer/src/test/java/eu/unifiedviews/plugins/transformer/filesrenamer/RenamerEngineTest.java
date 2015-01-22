package eu.unifiedviews.plugins.transformer.filesrenamer;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RenamerEngineTest {

    @Test
    public void counterTest() {
        RenameConfig_V2 config = new RenameConfig_V2();
        config.setCounterStart(0);
        config.setCounterStep(1);
        config.setCounterDigits(2);
        config.setMask("");
        config.setExtensionMask("");

        RenamerEngine engine = new RenamerEngine();
        engine.setConfig(config);
        engine.initialize();

        assertEquals("00", engine.getCounter().next());
        assertEquals("01", engine.getCounter().next());
        assertEquals("02", engine.getCounter().next());

        config = new RenameConfig_V2();
        config.setCounterStart(1);
        config.setCounterStep(2);
        config.setCounterDigits(3);
        config.setMask("");
        config.setExtensionMask("");

        engine.setConfig(config);
        engine.initialize();

        assertEquals("001", engine.getCounter().next());
        assertEquals("003", engine.getCounter().next());
        assertEquals("005", engine.getCounter().next());

        config = new RenameConfig_V2();
        config.setCounterStart(99);
        config.setCounterStep(1);
        config.setCounterDigits(2);
        config.setMask("");
        config.setExtensionMask("");

        engine.setConfig(config);
        engine.initialize();

        assertEquals("99", engine.getCounter().next());
        assertEquals("00", engine.getCounter().next());
        assertEquals("01", engine.getCounter().next());
    }

    @Test
    public void maskCommandsRegexTest() {
        Pattern p = Pattern.compile(RenamerEngine.CONTROL_MARKS_REGEX);
        Matcher m = p.matcher("[]test[N]test[C][NC]");

        assertEquals(true, m.find());
        assertEquals(6, m.start());
        assertEquals("[N]", m.group());
        assertEquals(true, m.find());
        assertEquals(13, m.start());
        assertEquals("[C]", m.group());
        assertEquals(false, m.find());
    }

    @Test
    public void renameTest() {
        RenameConfig_V2 config = new RenameConfig_V2();
        config.setMask("te[N]st[C]ing");
        config.setExtensionMask("[E]");
        config.setCounterStart(0);
        config.setCounterStep(1);
        config.setCounterDigits(2);

        RenamerEngine engine = new RenamerEngine();
        engine.setConfig(config);
        engine.initialize();

        assertEquals("telogs\\test\\file1st00ing.txt", engine.renameNext("logs\\test\\file1.txt"));
        assertEquals("telogs\\test\\file2st01ing.txt", engine.renameNext("logs\\test\\file2.txt"));
        assertEquals("telogs\\test\\file3st02ing.txt", engine.renameNext("logs\\test\\file3.txt"));
    }
}
