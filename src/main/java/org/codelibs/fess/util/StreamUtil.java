package org.codelibs.fess.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> of(final T... values) {
        if (values != null) {
            return Arrays.stream(values);
        } else {
            return Collections.<T> emptyList().stream();
        }
    }
}
