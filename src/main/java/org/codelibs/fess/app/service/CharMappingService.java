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
import org.codelibs.fess.app.pager.CharMappingPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.mapping.CharMappingFile;
import org.codelibs.fess.dict.mapping.CharMappingItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing character mapping operations.
 * <p>
 * This service handles character mapping management including CRUD operations
 * and list retrieval. Character mappings are used for text normalization
 * and character substitution during document processing and search operations.
 * </p>
 *
 * @author FessProject
 */
public class CharMappingService {
    /**
     * Dictionary manager for accessing and managing dictionary files.
     */
    @Resource
    protected DictionaryManager dictionaryManager;

    /**
     * Fess configuration settings.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of character mapping items from the specified dictionary.
     * <p>
     * This method fetches character mapping items with pagination support and updates
     * the pager with the current page information including total count and page ranges.
     * </p>
     *
     * @param dictId the dictionary ID to retrieve character mappings from
     * @param charMappingPager the pager object containing pagination parameters
     * @return a list of character mapping items for the current page, or empty list if dictionary not found
     */
    public List<CharMappingItem> getCharMappingList(final String dictId, final CharMappingPager charMappingPager) {
        return getCharMappingFile(dictId).map(file -> {
            final int pageSize = charMappingPager.getPageSize();
            final PagingList<CharMappingItem> charMappingList =
                    file.selectList((charMappingPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(charMappingList, charMappingPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            charMappingList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            charMappingPager.setPageNumberList(charMappingList.createPageNumberList());

            return (List<CharMappingItem>) charMappingList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Retrieves the character mapping file for the specified dictionary ID.
     * <p>
     * This method looks up the dictionary file and ensures it is a character mapping file
     * before returning it wrapped in an OptionalEntity.
     * </p>
     *
     * @param dictId the dictionary ID to retrieve the character mapping file for
     * @return an OptionalEntity containing the character mapping file if found and valid, empty otherwise
     */
    public OptionalEntity<CharMappingFile> getCharMappingFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(CharMappingFile.class::isInstance)
                .map(file -> OptionalEntity.of((CharMappingFile) file)).orElse(OptionalEntity.empty());
    }

    /**
     * Retrieves a specific character mapping item by its ID from the specified dictionary.
     * <p>
     * This method looks up a character mapping item using its unique identifier
     * within the context of the specified dictionary.
     * </p>
     *
     * @param dictId the dictionary ID containing the character mapping item
     * @param id the unique identifier of the character mapping item
     * @return an OptionalEntity containing the character mapping item if found, empty otherwise
     */
    public OptionalEntity<CharMappingItem> getCharMappingItem(final String dictId, final long id) {
        return getCharMappingFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Stores a character mapping item in the specified dictionary.
     * <p>
     * This method performs either an insert operation (for new items with ID 0)
     * or an update operation (for existing items with non-zero ID) depending on
     * the item's current state.
     * </p>
     *
     * @param dictId the dictionary ID to store the character mapping item in
     * @param charMappingItem the character mapping item to store
     */
    public void store(final String dictId, final CharMappingItem charMappingItem) {
        getCharMappingFile(dictId).ifPresent(file -> {
            if (charMappingItem.getId() == 0) {
                file.insert(charMappingItem);
            } else {
                file.update(charMappingItem);
            }
        });
    }

    /**
     * Deletes a character mapping item from the specified dictionary.
     * <p>
     * This method removes the specified character mapping item from the dictionary
     * if the dictionary file exists and is accessible.
     * </p>
     *
     * @param dictId the dictionary ID to delete the character mapping item from
     * @param charMappingItem the character mapping item to delete
     */
    public void delete(final String dictId, final CharMappingItem charMappingItem) {
        getCharMappingFile(dictId).ifPresent(file -> {
            file.delete(charMappingItem);
        });
    }
}
