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
import org.codelibs.fess.app.pager.StemmerOverridePager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideFile;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class StemmerOverrideService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

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

    public OptionalEntity<StemmerOverrideFile> getStemmerOverrideFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(StemmerOverrideFile.class::isInstance)
                .map(file -> OptionalEntity.of((StemmerOverrideFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<StemmerOverrideItem> getStemmerOverrideItem(final String dictId, final long id) {
        return getStemmerOverrideFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final StemmerOverrideItem stemmerOvberrideItem) {
        getStemmerOverrideFile(dictId).ifPresent(file -> {
            if (stemmerOvberrideItem.getId() == 0) {
                file.insert(stemmerOvberrideItem);
            } else {
                file.update(stemmerOvberrideItem);
            }
        });
    }

    public void delete(final String dictId, final StemmerOverrideItem stemmerOvberrideItem) {
        getStemmerOverrideFile(dictId).ifPresent(file -> {
            file.delete(stemmerOvberrideItem);
        });
    }
}
