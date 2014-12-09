package eu.unifiedviews.plugins.loader.filestovirtuoso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class ConvertUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS000";

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            return df.format(date);
        } else {
            return null;
        }
    }

    public static Date stringToDate(String strDate) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date result = null;
        if (!StringUtils.isBlank(strDate)) {
            result = df.parse(strDate);
        }
        return result;
    }
}
