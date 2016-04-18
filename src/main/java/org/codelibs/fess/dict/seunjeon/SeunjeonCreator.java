package org.codelibs.fess.dict.seunjeon;

import java.util.Date;

import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

public class SeunjeonCreator extends DictionaryCreator {

    public SeunjeonCreator() {
        super("seunjeon.*\\.txt");
    }

    public SeunjeonCreator(final String pattern) {
        super(pattern);
    }

    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new SeunjeonFile(id, path, timestamp).manager(dictionaryManager);
    }

}
