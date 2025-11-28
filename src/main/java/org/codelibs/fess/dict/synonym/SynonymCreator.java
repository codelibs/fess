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
package org.codelibs.fess.dict.synonym;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

import jakarta.annotation.PostConstruct;

/**
 * A dictionary creator for synonym files.
 * This class is responsible for creating {@link SynonymFile} instances
 * from files that match the pattern "synonym.*\\.txt".
 */
public class SynonymCreator extends DictionaryCreator {
    private static final Logger logger = LogManager.getLogger(SynonymCreator.class);

    /**
     * Constructs a new creator for synonym dictionaries.
     * It sets the file pattern to match files starting with "synonym"
     * and ending with ".txt".
     */
    public SynonymCreator() {
        super("synonym.*\\.txt");
    }

    /**
     * Registers this creator with the dictionary manager upon initialization.
     * This method is annotated with {@link PostConstruct} to be executed after
     * dependency injection is complete.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded {}", this.getClass().getSimpleName());
        }
        dictionaryManager.addCreator(this);
    }

    /**
     * Creates a new {@link SynonymFile} instance.
     *
     * @param id        The unique identifier for the dictionary file.
     * @param path      The file path of the dictionary.
     * @param timestamp The last modified timestamp of the file.
     * @return A new {@link SynonymFile} associated with the dictionary manager.
     */
    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new SynonymFile(id, path, timestamp).manager(dictionaryManager);
    }

}