package jp.sf.fess.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.codelibs.core.CoreLibConstants;

import com.ibm.icu.text.SimpleDateFormat;

public class CsvUtil {
    private CsvUtil() {
    }

    public static String get(final List<String> list, final int i) {
        return get(list, i, null);
    }

    public static String get(final List<String> list, final int i, final String defaultValue) {
        if (list == null || list.isEmpty() || i >= list.size()) {
            return defaultValue;
        }
        final String value = list.get(i);
        if (value == null) {
            return defaultValue;
        }
        return value.replaceFirst("^\"", "").replaceFirst("\"$", "");
    }

    public static Timestamp getAsTimestamp(final List<String> list, final int i, final Timestamp defaultValue) throws ParseException {
        final String value = get(list, i);
        if (value == null) {
            return defaultValue;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
        return new Timestamp(sdf.parse(value).getTime());
    }

    public static int getAsInt(final List<String> list, final int i, final int defaultValue) {
        final String value = get(list, i);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static long getAsLong(final List<String> list, final int i, final long defaultValue) {
        final String value = get(list, i);
        if (value == null) {
            return defaultValue;
        }
        return Long.parseLong(value);
    }
}
