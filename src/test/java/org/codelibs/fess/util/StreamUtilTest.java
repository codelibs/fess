package org.codelibs.fess.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;

public class StreamUtilTest extends UnitFessTestCase {

    public void test_ofValues() {
        String[] values = { "value1", "value2" };
        Stream<String> stream = StreamUtil.of(values[0], values[1]);
        Object[] array = stream.toArray();
        for (int i = 0; i < 2; i++) {
            assertEquals(values[i], array[i]);
        }
    }

    public void test_ofNull() {
        assertEquals(0, StreamUtil.of().toArray().length);
        Object[] o = {};
        assertEquals(0, StreamUtil.of(o).toArray().length);
        Map<Object, Object> map = new HashMap<Object, Object>();
        assertEquals(0, StreamUtil.of(map).toArray().length);
    }

    public void test_ofMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Stream<Map.Entry<String, String>> stream = StreamUtil.of(map);
        stream.forEach(m -> assertEquals(map.get(m.getKey()), m.getValue()));
    }

}
