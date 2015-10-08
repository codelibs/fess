/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.dict.DictionaryExpiredException;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.dict.synonym.SynonymItem;

public class SynonymService {
    @Resource
    protected DictionaryManager dictionaryManager;

    public List<SynonymItem> getSynonymList(final String dictId, final SynonymPager synonymPager) {
        final SynonymFile synonymFile = getSynonymFile(dictId);

        final int pageSize = synonymPager.getPageSize();
        final PagingList<SynonymItem> synonymList = synonymFile.selectList((synonymPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

        // update pager
        BeanUtil.copyBeanToBean(synonymList, synonymPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        synonymList.setPageRangeSize(5);
        synonymPager.setPageNumberList(synonymList.createPageNumberList());

        return synonymList;

    }

    public SynonymFile getSynonymFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(file -> file instanceof SynonymFile).map(file -> (SynonymFile) file)
                .orElseThrow(() -> new DictionaryExpiredException());
    }

    public SynonymItem getSynonym(final String dictId, final Map<String, String> paramMap) {
        final SynonymFile synonymFile = getSynonymFile(dictId);

        final String idStr = paramMap.get("id");
        if (StringUtil.isNotBlank(idStr)) {
            try {
                final long id = Long.parseLong(idStr);
                return synonymFile.get(id);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }

        return null;
    }

    public void store(final String dictId, final SynonymItem synonymItem) {
        final SynonymFile synonymFile = getSynonymFile(dictId);

        if (synonymItem.getId() == 0) {
            synonymFile.insert(synonymItem);
        } else {
            synonymFile.update(synonymItem);
        }
    }

    public void delete(final String dictId, final SynonymItem synonymItem) {
        final SynonymFile synonymFile = getSynonymFile(dictId);
        synonymFile.delete(synonymItem);
    }
}
