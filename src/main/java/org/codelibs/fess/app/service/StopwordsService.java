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
package org.codelibs.fess.app.service;

import java.util.Collections;
import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.StopwordsPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.stopwords.StopwordsFile;
import org.codelibs.fess.dict.stopwords.StopwordsItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service for managing stopwords.
 * This class provides methods to interact with stopwords dictionaries,
 * including retrieving, storing, and deleting stopwords.
 */
public class StopwordsService {
    /** The dictionary manager for accessing dictionary files. */
    @Resource
    protected DictionaryManager dictionaryManager;

    /** The Fess configuration for accessing system settings. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Constructs a new stopwords service.
     */
    public StopwordsService() {
        // do nothing
    }

    /**
     * Retrieves a list of stopwords for a given dictionary and pager.
     *
     * @param dictId         The ID of the dictionary.
     * @param stopwordsPager The pager for controlling pagination.
     * @return A list of stopwords.
     */
    public List<StopwordsItem> getStopwordsList(final String dictId, final StopwordsPager stopwordsPager) {
        return getStopwordsFile(dictId).map(file -> {
            final int pageSize = stopwordsPager.getPageSize();
            final PagingList<StopwordsItem> stopwordsList =
                    file.selectList((stopwordsPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(stopwordsList, stopwordsPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            stopwordsList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            stopwordsPager.setPageNumberList(stopwordsList.createPageNumberList());

            return (List<StopwordsItem>) stopwordsList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Retrieves a stopwords file for a given dictionary ID.
     *
     * @param dictId The ID of the dictionary.
     * @return An optional entity containing the stopwords file, or empty if not found.
     */
    public OptionalEntity<StopwordsFile> getStopwordsFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId)
                .filter(StopwordsFile.class::isInstance)
                .map(file -> OptionalEntity.of((StopwordsFile) file))
                .orElse(OptionalEntity.empty());
    }

    /**
     * Retrieves a specific stopword item by its ID.
     *
     * @param dictId The ID of the dictionary.
     * @param id     The ID of the stopword item.
     * @return An optional entity containing the stopword item, or empty if not found.
     */
    public OptionalEntity<StopwordsItem> getStopwordsItem(final String dictId, final long id) {
        return getStopwordsFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Stores a stopword item in the specified dictionary.
     *
     * @param dictId        The ID of the dictionary.
     * @param stopwordsItem The stopword item to store.
     */
    public void store(final String dictId, final StopwordsItem stopwordsItem) {
        getStopwordsFile(dictId).ifPresent(file -> {
            if (stopwordsItem.getId() == 0) {
                file.insert(stopwordsItem);
            } else {
                file.update(stopwordsItem);
            }
        });
    }

    /**
     * Deletes a stopword item from the specified dictionary.
     *
     * @param dictId        The ID of the dictionary.
     * @param stopwordsItem The stopword item to delete.
     */
    public void delete(final String dictId, final StopwordsItem stopwordsItem) {
        getStopwordsFile(dictId).ifPresent(file -> {
            file.delete(stopwordsItem);
        });
    }
}