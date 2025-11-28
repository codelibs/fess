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
package org.codelibs.fess.dict.protwords;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

import jakarta.annotation.PostConstruct;

/**
 * Creator for protected words dictionary files.
 * This class manages the creation and registration of protected words dictionary files.
 */
public class ProtwordsCreator extends DictionaryCreator {
    private static final Logger logger = LogManager.getLogger(ProtwordsCreator.class);

    /**
     * Constructor for ProtwordsCreator.
     * Initializes the creator with a pattern to match protwords files.
     */
    public ProtwordsCreator() {
        super("protwords.*\\.txt");
    }

    /**
     * Registers this creator with the dictionary manager.
     * This method is called automatically after construction to add this creator to the dictionary manager.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded {}", this.getClass().getSimpleName());
        }
        dictionaryManager.addCreator(this);
    }

    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new ProtwordsFile(id, path, timestamp).manager(dictionaryManager);
    }

}
