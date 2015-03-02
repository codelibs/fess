package org.codelibs.fess.beans.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.seasar.framework.beans.Converter;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.util.StringUtil;

public class LocalDateTimeConverter implements Converter {

    protected String pattern;

    public LocalDateTimeConverter(String pattern) {
        if (StringUtil.isEmpty(pattern)) {
            throw new EmptyRuntimeException("pattern");
        }
        this.pattern = pattern;
    }

    public Object getAsObject(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    public String getAsString(Object value) {
        return ((LocalDateTime) value).format(DateTimeFormatter
                .ofPattern(pattern));
    }

    public boolean isTarget(Class clazz) {
        return clazz == LocalDateTime.class;
    }

}
