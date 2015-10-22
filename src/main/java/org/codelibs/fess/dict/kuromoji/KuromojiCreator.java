package org.codelibs.fess.dict.kuromoji;

import java.util.Date;

import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

public class KuromojiCreator extends DictionaryCreator {

    public KuromojiCreator() {
        super("kuromoji.*\\.txt");
    }

    public KuromojiCreator(final String pattern) {
        super(pattern);
    }

    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new KuromojiFile(id, path, timestamp).manager(dictionaryManager);
    }

}
