package eu.unifiedviews.plugins.transformer.filesrenamer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that implements renaming logic.
 * 
 * @author mva
 */
public class RenamerEngine {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyymmdd");

    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmssSSS");

    public static final String CONTROL_MARKS_REGEX = "\\[[NCDT]\\]";

    public static final String EXTENSION_CONTROL_MARKS_REGEX = "\\[[EC]\\]";

    private Map<Integer, String> positionToControlMarkMap;

    private Map<Integer, String> positionToControlMarkExtensionMap;

    private RenameConfig_V1 config;

    private Counter counter;

    public void initialize() {
        this.counter = new Counter(config.getCounterStart(), config.getCounterStep(), config.getCounterDigits());
        this.positionToControlMarkMap = new HashMap<Integer, String>();
        this.positionToControlMarkExtensionMap = new HashMap<Integer, String>();

        // find control marks occurrences in mask and map their position
        Pattern controlPattern = Pattern.compile(RenamerEngine.CONTROL_MARKS_REGEX);
        Matcher controlMatcher = controlPattern.matcher(config.getMask());
        while (controlMatcher.find()) {
            positionToControlMarkMap.put(controlMatcher.start(), controlMatcher.group());
        }

        // find control marks occurrences in extension mask and map their position
        controlPattern = Pattern.compile(RenamerEngine.EXTENSION_CONTROL_MARKS_REGEX);
        controlMatcher = controlPattern.matcher(config.getExtensionMask());
        while (controlMatcher.find()) {
            positionToControlMarkExtensionMap.put(controlMatcher.start(), controlMatcher.group());
        }
    }

    /**
     * Rename next input filename.
     * 
     * @param input
     *            String to rename.
     * @return renamed String based on engine configuration.
     */
    public String renameNext(String input) {
        //divide input into name and extension (if any)
        String[] parts = input.split("\\.");
        String fileName = "";
        String fileExtension = "";
        if (parts.length == 1) {
            // no extension is provided
            fileName = parts[0];
        } else if (parts.length == 2) {
            // first part is file name
            fileName = parts[0];
            // second is extension
            fileExtension = parts[1];
        } else {
            // file name consists of merged first {n-1} parts
            for (int i = 0; i < parts.length - 1; i++) {
                fileName += parts[i];
            }
            // extension is last part
            fileExtension = parts[parts.length - 1];
        }

        String newFilename = resolveNewFilename(fileName);
        String newExtension = resolveNewExtension(fileExtension);
        counter.next();

        if (fileExtension.equals("")) {
            return newFilename; // if extension was not provided, change just filename
        }
        return newFilename + "." + newExtension;
    }

    private String resolveNewFilename(String oldFileName) {
        StringBuilder sb = new StringBuilder();
        int position = 0;
        while (position != config.getMask().length()) {
            String controlMark = positionToControlMarkMap.get(position);
            if (controlMark == null) {
                // no position entry in map, copy character to output
                sb.append(config.getMask().charAt(position));
                position++;
            } else {
                // parse control mark
                switch (controlMark) {
                    case "[N]":
                        sb.append(oldFileName);
                        break;
                    case "[C]":
                        sb.append(counter.get());
                        break;
                    case "[D]":
                        Date date = new Date();
                        sb.append(DATE_FORMAT.format(date));
                        break;
                    case "[T]":
                        Date time = new Date();
                        sb.append(TIME_FORMAT.format(time));
                        break;
                    default:
                        break;
                }
                position += controlMark.length();
            }
        }
        return sb.toString();
    }

    private String resolveNewExtension(String oldExtension) {
        StringBuilder sb = new StringBuilder();
        int position = 0;
        while (position != config.getExtensionMask().length()) {
            String controlMark = positionToControlMarkExtensionMap.get(position);
            if (controlMark == null) {
                // no position entry in map, copy character to output
                sb.append(config.getExtensionMask().charAt(position));
                position++;
            } else {
                // parse control mark
                switch (controlMark) {
                    case "[E]":
                        sb.append(oldExtension);
                        break;
                    case "[C]":
                        sb.append(counter.get());
                        break;
                    default:
                        break;
                }
                position += controlMark.length();
            }
        }
        return sb.toString();
    }

    class Counter {

        private int step;

        private int counter;

        private NumberFormat numberFormat;

        public Counter(int from, int step, int digits) {
            this.counter = from;
            this.step = step;

            numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
            numberFormat.setMinimumIntegerDigits(digits);
            numberFormat.setMaximumIntegerDigits(digits);
        }

        public String next() {
            String output = numberFormat.format(counter);
            this.counter += step;
            return output;
        }

        public String get() {
            return numberFormat.format(counter);
        }
    }

    public RenameConfig_V1 getConfig() {
        return config;
    }

    public void setConfig(RenameConfig_V1 config) {
        this.config = config;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }
}
