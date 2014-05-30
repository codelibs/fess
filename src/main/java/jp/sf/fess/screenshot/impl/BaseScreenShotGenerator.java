package jp.sf.fess.screenshot.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import jp.sf.fess.screenshot.ScreenShotGenerator;

public abstract class BaseScreenShotGenerator implements ScreenShotGenerator {

    @Resource
    protected ServletContext application;

    protected final Map<String, String> conditionMap = new HashMap<String, String>();

    public int directoryNameLength = 5;

    public void addCondition(final String key, final String regex) {
        conditionMap.put(key, regex);
    }

    @Override
    public boolean isTarget(final Map<String, Object> docMap) {
        for (final Map.Entry<String, String> entry : conditionMap.entrySet()) {
            final Object value = docMap.get(entry.getKey());
            if (value instanceof String
                    && !((String) value).matches(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

}