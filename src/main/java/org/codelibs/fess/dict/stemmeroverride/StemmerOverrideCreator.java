/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.stemmeroverride;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.dict.DictionaryCreator;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;

public class StemmerOverrideCreator extends DictionaryCreator {
    private static final Logger logger = LogManager.getLogger(StemmerOverrideCreator.class);

    public StemmerOverrideCreator() {
        super("stemmer_override.*\\.txt");
    }

    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Load {}", this.getClass().getSimpleName());
        }
        dictionaryManager.addCreator(this);
    }

    @Override
    protected DictionaryFile<? extends DictionaryItem> newDictionaryFile(final String id, final String path, final Date timestamp) {
        return new StemmerOverrideFile(id, path, timestamp).manager(dictionaryManager);
    }

}
