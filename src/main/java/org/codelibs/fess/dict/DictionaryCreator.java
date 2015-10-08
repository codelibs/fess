package org.codelibs.fess.dict;

import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

import org.codelibs.fess.Constants;

public abstract class DictionaryCreator {
    protected Pattern pattern;

    protected DictionaryManager dictionaryManager;

    public DictionaryCreator(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public DictionaryFile<? extends DictionaryItem> create(String path, Date timestamp) {
        if (!isTarget(path)) {
            return null;
        }

        return newDictionaryFile(encodePath(path), path, timestamp);
    }

    protected String encodePath(String path) {
        return Base64.getEncoder().encodeToString(path.getBytes(Constants.CHARSET_UTF_8));
    }

    protected boolean isTarget(String path) {
        return pattern.matcher(path).find();
    }

    protected abstract DictionaryFile<? extends DictionaryItem> newDictionaryFile(String id, String path, Date timestamp);

    public void setDictionaryManager(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }
}
