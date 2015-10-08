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
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.dict.DictionaryExpiredException;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;

public class KuromojiService {
    @Resource
    protected DictionaryManager dictionaryManager;

    public List<KuromojiItem> getUserDictList(final String dictId, final KuromojiPager kuromojiPager) {
        final KuromojiFile kuromojiFile = getUserDictFile(dictId);

        final int pageSize = kuromojiPager.getPageSize();
        final PagingList<KuromojiItem> userDictList =
                kuromojiFile.selectList((kuromojiPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

        // update pager
        BeanUtil.copyBeanToBean(userDictList, kuromojiPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        userDictList.setPageRangeSize(5);
        kuromojiPager.setPageNumberList(userDictList.createPageNumberList());

        return userDictList;

    }

    public KuromojiFile getUserDictFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(file -> file instanceof KuromojiFile).map(file -> (KuromojiFile) file)
                .orElseThrow(() -> new DictionaryExpiredException());
    }

    public KuromojiItem getUserDict(final String dictId, final Map<String, String> paramMap) {
        final KuromojiFile kuromojiFile = getUserDictFile(dictId);

        final String idStr = paramMap.get("id");
        if (StringUtil.isNotBlank(idStr)) {
            try {
                final long id = Long.parseLong(idStr);
                return kuromojiFile.get(id);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }

        return null;
    }

    public void store(final String dictId, final KuromojiItem kuromojiItem) {
        final KuromojiFile kuromojiFile = getUserDictFile(dictId);

        if (kuromojiItem.getId() == 0) {
            kuromojiFile.insert(kuromojiItem);
        } else {
            kuromojiFile.update(kuromojiItem);
        }
    }

    public void delete(final String dictId, final KuromojiItem kuromojiItem) {
        final KuromojiFile kuromojiFile = getUserDictFile(dictId);
        kuromojiFile.delete(kuromojiItem);
    }
}
