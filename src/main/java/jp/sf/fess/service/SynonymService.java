/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.dict.DictionaryFile;
import jp.sf.fess.dict.DictionaryFile.PagingList;
import jp.sf.fess.dict.DictionaryManager;
import jp.sf.fess.dict.synonym.SynonymFile;
import jp.sf.fess.dict.synonym.SynonymItem;
import jp.sf.fess.pager.SynonymPager;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.exception.ActionMessagesException;

public class SynonymService {
    @Resource
    protected DictionaryManager dictionaryManager;

    public List<SynonymItem> getSynonymList(final String dictId,
            final SynonymPager synonymPager) {
        final SynonymFile synonymFile = getSynonymFile(dictId);

        final int pageSize = synonymPager.getPageSize();
        final PagingList<SynonymItem> synonymList = synonymFile.selectList(
                (synonymPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

        // update pager
        Beans.copy(synonymList, synonymPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        synonymList.setPageRangeSize(5);
        synonymPager.setPageNumberList(synonymList.createPageNumberList());

        return synonymList;

    }

    protected SynonymFile getSynonymFile(final String dictId) {
        final DictionaryFile<?> dictionaryFile = dictionaryManager
                .getDictionaryFile(dictId);
        if (dictionaryFile instanceof SynonymFile) {
            return (SynonymFile) dictionaryFile;
        }
        throw new ActionMessagesException("errors.expired_dict_id");
    }

    public SynonymItem getSynonym(final String dictId,
            final Map<String, String> paramMap) {
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
