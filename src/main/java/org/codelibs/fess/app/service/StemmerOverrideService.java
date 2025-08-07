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
import org.codelibs.fess.app.pager.StemmerOverridePager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideFile;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing stemmer override dictionary operations.
 *
 * This service provides functionality for CRUD operations on stemmer override
 * dictionaries. Stemmer override dictionaries allow administrators to define
 * custom stemming rules that override the default stemming behavior for
 * specific terms, improving search accuracy for domain-specific vocabularies.
 */
public class StemmerOverrideService {

    /** Dictionary manager for accessing dictionary files. */
    @Resource
    protected DictionaryManager dictionaryManager;

    /** Fess configuration for accessing system settings. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public StemmerOverrideService() {
        // Default constructor
    }

    /**
     * Retrieves a paginated list of stemmer override items from the specified dictionary.
     *
     * This method fetches stemmer override entries with pagination support, updating
     * the provided pager with the current page state and navigation information.
     *
     * @param dictId The ID of the stemmer override dictionary to query
     * @param stemmerOvberridePager The pager object for pagination control and state
     * @return A list of stemmer override items for the current page, or empty list if dictionary not found
     */
    public List<StemmerOverrideItem> getStemmerOverrideList(final String dictId, final StemmerOverridePager stemmerOvberridePager) {
        return getStemmerOverrideFile(dictId).map(file -> {
            final int pageSize = stemmerOvberridePager.getPageSize();
            final PagingList<StemmerOverrideItem> stemmerOvberrideList =
                    file.selectList((stemmerOvberridePager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(stemmerOvberrideList, stemmerOvberridePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            stemmerOvberrideList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            stemmerOvberridePager.setPageNumberList(stemmerOvberrideList.createPageNumberList());

            return (List<StemmerOverrideItem>) stemmerOvberrideList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Retrieves the stemmer override dictionary file by ID.
     *
     * @param dictId The ID of the stemmer override dictionary to retrieve
     * @return An OptionalEntity containing the stemmer override file if found, empty otherwise
     */
    public OptionalEntity<StemmerOverrideFile> getStemmerOverrideFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId)
                .filter(StemmerOverrideFile.class::isInstance)
                .map(file -> OptionalEntity.of((StemmerOverrideFile) file))
                .orElse(OptionalEntity.empty());
    }

    /**
     * Retrieves a specific stemmer override item by dictionary ID and item ID.
     *
     * @param dictId The ID of the stemmer override dictionary
     * @param id The unique ID of the stemmer override item to retrieve
     * @return An OptionalEntity containing the stemmer override item if found, empty otherwise
     */
    public OptionalEntity<StemmerOverrideItem> getStemmerOverrideItem(final String dictId, final long id) {
        return getStemmerOverrideFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Stores (creates or updates) a stemmer override item in the specified dictionary.
     *
     * If the item ID is 0, this method performs an insert operation. Otherwise,
     * it performs an update operation for the existing item.
     *
     * @param dictId The ID of the stemmer override dictionary
     * @param stemmerOvberrideItem The stemmer override item to store
     */
    public void store(final String dictId, final StemmerOverrideItem stemmerOvberrideItem) {
        getStemmerOverrideFile(dictId).ifPresent(file -> {
            if (stemmerOvberrideItem.getId() == 0) {
                file.insert(stemmerOvberrideItem);
            } else {
                file.update(stemmerOvberrideItem);
            }
        });
    }

    /**
     * Deletes a stemmer override item from the specified dictionary.
     *
     * @param dictId The ID of the stemmer override dictionary
     * @param stemmerOvberrideItem The stemmer override item to delete
     */
    public void delete(final String dictId, final StemmerOverrideItem stemmerOvberrideItem) {
        getStemmerOverrideFile(dictId).ifPresent(file -> {
            file.delete(stemmerOvberrideItem);
        });
    }
}
