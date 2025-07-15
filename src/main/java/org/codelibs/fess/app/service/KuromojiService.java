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
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for Kuromoji.
 */
public class KuromojiService {
    /** The dictionary manager. */
    @Resource
    protected DictionaryManager dictionaryManager;

    /** The Fess config. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public KuromojiService() {
        // do nothing
    }

    /**
     * Get a list of Kuromoji items.
     *
     * @param dictId The dictionary ID.
     * @param kuromojiPager The pager for Kuromoji.
     * @return A list of Kuromoji items.
     */
    public List<KuromojiItem> getKuromojiList(final String dictId, final KuromojiPager kuromojiPager) {
        return getKuromojiFile(dictId).map(file -> {
            final int pageSize = kuromojiPager.getPageSize();
            final PagingList<KuromojiItem> kuromojiList = file.selectList((kuromojiPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
            BeanUtil.copyBeanToBean(kuromojiList, kuromojiPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
            kuromojiList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
            kuromojiPager.setPageNumberList(kuromojiList.createPageNumberList());

            return (List<KuromojiItem>) kuromojiList;
        }).orElse(Collections.emptyList());
    }

    /**
     * Get a Kuromoji file.
     *
     * @param dictId The dictionary ID.
     * @return An optional entity of the Kuromoji file.
     */
    public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(KuromojiFile.class::isInstance)
                .map(file -> OptionalEntity.of((KuromojiFile) file)).orElse(OptionalEntity.empty());
    }

    /**
     * Get a Kuromoji item.
     *
     * @param dictId The dictionary ID.
     * @param id The ID of the Kuromoji item.
     * @return An optional entity of the Kuromoji item.
     */
    public OptionalEntity<KuromojiItem> getKuromojiItem(final String dictId, final long id) {
        return getKuromojiFile(dictId).map(file -> file.get(id).get());
    }

    /**
     * Store a Kuromoji item.
     *
     * @param dictId The dictionary ID.
     * @param kuromojiItem The Kuromoji item to store.
     */
    public void store(final String dictId, final KuromojiItem kuromojiItem) {
        getKuromojiFile(dictId).ifPresent(file -> {
            if (kuromojiItem.getId() == 0) {
                file.insert(kuromojiItem);
            } else {
                file.update(kuromojiItem);
            }
        });
    }

    /**
     * Delete a Kuromoji item.
     *
     * @param dictId The dictionary ID.
     * @param kuromojiItem The Kuromoji item to delete.
     */
    public void delete(final String dictId, final KuromojiItem kuromojiItem) {
        getKuromojiFile(dictId).ifPresent(file -> {
            file.delete(kuromojiItem);
        });
    }
}
