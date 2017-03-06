/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.SeunjeonPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.seunjeon.SeunjeonFile;
import org.codelibs.fess.dict.seunjeon.SeunjeonItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class SeunjeonService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

    public List<SeunjeonItem> getSeunjeonList(final String dictId, final SeunjeonPager seunjeonPager) {
        return getSeunjeonFile(dictId).map(file -> {
            final int pageSize = seunjeonPager.getPageSize();
            final PagingList<SeunjeonItem> seunjeonList = file.selectList((seunjeonPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
                BeanUtil.copyBeanToBean(seunjeonList, seunjeonPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
                seunjeonList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
                seunjeonPager.setPageNumberList(seunjeonList.createPageNumberList());

                return (List<SeunjeonItem>) seunjeonList;
            }).orElse(Collections.emptyList());
    }

    public OptionalEntity<SeunjeonFile> getSeunjeonFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(file -> file instanceof SeunjeonFile)
                .map(file -> OptionalEntity.of((SeunjeonFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<SeunjeonItem> getSeunjeonItem(final String dictId, final long id) {
        return getSeunjeonFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final SeunjeonItem seunjeonItem) {
        getSeunjeonFile(dictId).ifPresent(file -> {
            if (seunjeonItem.getId() == 0) {
                file.insert(seunjeonItem);
            } else {
                file.update(seunjeonItem);
            }
        });
    }

    public void delete(final String dictId, final SeunjeonItem seunjeonItem) {
        getSeunjeonFile(dictId).ifPresent(file -> {
            file.delete(seunjeonItem);
        });
    }
}
