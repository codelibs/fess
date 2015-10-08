package org.codelibs.fess.dict.synonym;

import java.util.Date;

import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

public class SynonymCreator extends DictionaryCreator {

    public SynonymCreator() {
        super("synonym.*\\.txt");
    }

    public SynonymCreator(String pattern) {
        super(pattern);
    }

    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(String id, String path, Date timestamp) {
        return new SynonymFile(id, path, timestamp).manager(dictionaryManager);
    }

}
