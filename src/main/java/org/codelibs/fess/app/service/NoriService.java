/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.NoriPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.nori.NoriFile;
import org.codelibs.fess.dict.nori.NoriItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

public class NoriService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

    public List<NoriItem> getNoriList(final String dictId, final NoriPager noriPager) {
        return getNoriFile(dictId).map(file -> {
            final int pageSize = noriPager.getPageSize();
            final PagingList<NoriItem> noriList = file.selectList((noriPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

            // update pager
                BeanUtil.copyBeanToBean(noriList, noriPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
                noriList.setPageRangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
                noriPager.setPageNumberList(noriList.createPageNumberList());

                return (List<NoriItem>) noriList;
            }).orElse(Collections.emptyList());
    }

    public OptionalEntity<NoriFile> getNoriFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(file -> file instanceof NoriFile)
                .map(file -> OptionalEntity.of((NoriFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<NoriItem> getNoriItem(final String dictId, final long id) {
        return getNoriFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final NoriItem noriItem) {
        getNoriFile(dictId).ifPresent(file -> {
            if (noriItem.getId() == 0) {
                file.insert(noriItem);
            } else {
                file.update(noriItem);
            }
        });
    }

    public void delete(final String dictId, final NoriItem noriItem) {
        getNoriFile(dictId).ifPresent(file -> {
            file.delete(noriItem);
        });
    }
}
