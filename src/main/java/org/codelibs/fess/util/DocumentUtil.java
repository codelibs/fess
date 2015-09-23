package org.codelibs.fess.util;

import java.util.List;
import java.util.Map;

public final class DocumentUtil {

    private DocumentUtil() {
    }

    public static <T> T getValue(Map<String, Object> doc, String key, Class<T> clazz) {
        if (doc == null || key == null) {
            return null;
        }

        Object value = doc.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            if (clazz.isAssignableFrom(List.class)) {
                return (T) value;
            }

            if (((List<?>) value).isEmpty()) {
                return null;
            }

            return (T) ((List) value).get(0);
        }

        return (T) value;
    }
}
