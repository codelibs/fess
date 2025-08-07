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
import org.codelibs.fess.app.pager.ProtwordsPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.protwords.ProtwordsFile;
import org.codelibs.fess.dict.protwords.ProtwordsItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service for managing protected words dictionary.
 * This service provides operations for managing protected words dictionary files and items.
 */
public class ProtwordsService {

    /**
     * Default constructor.
     */
    public ProtwordsService() {
        // Default constructor
    }

    /** Dictionary manager for handling dictionary files */
    @Resource
    protected DictionaryManager dictionaryManager;

    /** Configuration for Fess */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Gets a paginated list of protected words items.
     * @param dictId the dictionary ID
     * @param protwordsPager the pager for pagination
     * @return the list of protected words items
     */
    public List<ProtwordsItem> getProtwordsList(final String dictId, final ProtwordsPager protwordsPager) {
        return getProtwordsFile(dictId).map(file -> {
            final int pageSize = protwordsPager.getPageSize();
            final PagingList<ProtwordsItem> protwordsList =
                    file.selectList((protwordsPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(protwordsList, protwordsPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            protwordsList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            protwordsPager.setPageNumberList(protwordsList.createPageNumberList());

            return (List<ProtwordsItem>) protwordsList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Gets the protected words file for the specified dictionary ID.
     * @param dictId the dictionary ID
     * @return the protected words file if found
     */
    public OptionalEntity<ProtwordsFile> getProtwordsFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId)
                .filter(ProtwordsFile.class::isInstance)
                .map(file -> OptionalEntity.of((ProtwordsFile) file))
                .orElse(OptionalEntity.empty());
    }

    /**
     * Gets a specific protected words item by ID.
     * @param dictId the dictionary ID
     * @param id the item ID
     * @return the protected words item if found
     */
    public OptionalEntity<ProtwordsItem> getProtwordsItem(final String dictId, final long id) {
        return getProtwordsFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Stores a protected words item (insert or update).
     * @param dictId the dictionary ID
     * @param protwordsItem the item to store
     */
    public void store(final String dictId, final ProtwordsItem protwordsItem) {
        getProtwordsFile(dictId).ifPresent(file -> {
            if (protwordsItem.getId() == 0) {
                file.insert(protwordsItem);
            } else {
                file.update(protwordsItem);
            }
        });
    }

    /**
     * Deletes a protected words item.
     * @param dictId the dictionary ID
     * @param protwordsItem the item to delete
     */
    public void delete(final String dictId, final ProtwordsItem protwordsItem) {
        getProtwordsFile(dictId).ifPresent(file -> {
            file.delete(protwordsItem);
        });
    }
}
