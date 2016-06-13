/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.MappingPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.mapping.MappingFile;
import org.codelibs.fess.dict.mapping.MappingItem;
import org.dbflute.optional.OptionalEntity;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public class MappingService {
    @Resource
    protected DictionaryManager dictionaryManager;

    public List<MappingItem> getMappingList(final String dictId, final MappingPager mappingPager) {
        return getMappingFile(dictId).map(file -> {
            final int pageSize = mappingPager.getPageSize();
            final PagingList<MappingItem> mappingList = file.selectList((mappingPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
                BeanUtil.copyBeanToBean(mappingList, mappingPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
                mappingList.setPageRangeSize(5);
                mappingPager.setPageNumberList(mappingList.createPageNumberList());

                return (List<MappingItem>) mappingList;
            }).orElse(Collections.emptyList());
    }

    public OptionalEntity<MappingFile> getMappingFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(file -> file instanceof MappingFile)
                .map(file -> OptionalEntity.of((MappingFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<MappingItem> getMappingItem(final String dictId, final long id) {
        return getMappingFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final MappingItem mappingItem) {
        getMappingFile(dictId).ifPresent(file -> {
            if (mappingItem.getId() == 0) {
                file.insert(mappingItem);
            } else {
                file.update(mappingItem);
            }
        });
    }

    public void delete(final String dictId, final MappingItem mappingItem) {
        getMappingFile(dictId).ifPresent(file -> {
            file.delete(mappingItem);
        });
    }
}
