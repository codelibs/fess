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
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.dict.synonym.SynonymItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service for managing synonyms.
 * This class provides methods to interact with synonym dictionaries,
 * including retrieving, storing, and deleting synonyms.
 */
public class SynonymService {
    /** The dictionary manager for accessing dictionary files. */
    @Resource
    protected DictionaryManager dictionaryManager;

    /** The Fess configuration for accessing system settings. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Constructs a new synonym service.
     */
    public SynonymService() {
        // do nothing
    }

    /**
     * Retrieves a list of synonyms for a given dictionary and pager.
     *
     * @param dictId       The ID of the dictionary.
     * @param synonymPager The pager for controlling pagination.
     * @return A list of synonyms.
     */
    public List<SynonymItem> getSynonymList(final String dictId, final SynonymPager synonymPager) {
        return getSynonymFile(dictId).map(file -> {
            final int pageSize = synonymPager.getPageSize();
            final PagingList<SynonymItem> synonymList = file.selectList((synonymPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(synonymList, synonymPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            synonymList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            synonymPager.setPageNumberList(synonymList.createPageNumberList());

            return (List<SynonymItem>) synonymList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Retrieves a synonym file for a given dictionary ID.
     *
     * @param dictId The ID of the dictionary.
     * @return An optional entity containing the synonym file, or empty if not found.
     */
    public OptionalEntity<SynonymFile> getSynonymFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId)
                .filter(SynonymFile.class::isInstance)
                .map(file -> OptionalEntity.of((SynonymFile) file))
                .orElse(OptionalEntity.empty());
    }

    /**
     * Retrieves a specific synonym item by its ID.
     *
     * @param dictId The ID of the dictionary.
     * @param id     The ID of the synonym item.
     * @return An optional entity containing the synonym item, or empty if not found.
     */
    public OptionalEntity<SynonymItem> getSynonymItem(final String dictId, final long id) {
        return getSynonymFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Stores a synonym item in the specified dictionary.
     *
     * @param dictId      The ID of the dictionary.
     * @param synonymItem The synonym item to store.
     */
    public void store(final String dictId, final SynonymItem synonymItem) {
        getSynonymFile(dictId).ifPresent(file -> {
            if (synonymItem.getId() == 0) {
                file.insert(synonymItem);
            } else {
                file.update(synonymItem);
            }
        });
    }

    /**
     * Deletes a synonym item from the specified dictionary.
     *
     * @param dictId      The ID of the dictionary.
     * @param synonymItem The synonym item to delete.
     */
    public void delete(final String dictId, final SynonymItem synonymItem) {
        getSynonymFile(dictId).ifPresent(file -> {
            file.delete(synonymItem);
        });
    }
}