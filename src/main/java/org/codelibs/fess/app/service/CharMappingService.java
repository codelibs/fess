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
package org.codelibs.fess.app.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.CharMappingPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.mapping.CharMappingFile;
import org.codelibs.fess.dict.mapping.CharMappingItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class CharMappingService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

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

    public OptionalEntity<CharMappingFile> getCharMappingFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(CharMappingFile.class::isInstance)
                .map(file -> OptionalEntity.of((CharMappingFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<CharMappingItem> getCharMappingItem(final String dictId, final long id) {
        return getCharMappingFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final CharMappingItem charMappingItem) {
        getCharMappingFile(dictId).ifPresent(file -> {
            if (charMappingItem.getId() == 0) {
                file.insert(charMappingItem);
            } else {
                file.update(charMappingItem);
            }
        });
    }

    public void delete(final String dictId, final CharMappingItem charMappingItem) {
        getCharMappingFile(dictId).ifPresent(file -> {
            file.delete(charMappingItem);
        });
    }
}
