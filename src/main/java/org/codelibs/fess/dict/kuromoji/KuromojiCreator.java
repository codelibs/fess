/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.dict.kuromoji;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

import jakarta.annotation.PostConstruct;

/**
 * A dictionary creator for Kuromoji.
 */
public class KuromojiCreator extends DictionaryCreator {
    private static final Logger logger = LogManager.getLogger(KuromojiCreator.class);

    /**
     * Constructs a new Kuromoji creator.
     */
    public KuromojiCreator() {
        super("kuromoji.*\\.txt");
    }

    /**
     * Registers this creator to the dictionary manager.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded {}", this.getClass().getSimpleName());
        }
        dictionaryManager.addCreator(this);
    }

    /**
     * Creates a new dictionary file.
     *
     * @param id The ID of the dictionary file.
     * @param path The path of the dictionary file.
     * @param timestamp The timestamp of the dictionary file.
     * @return A new dictionary file.
     */
    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new KuromojiFile(id, path, timestamp).manager(dictionaryManager);
    }

}
