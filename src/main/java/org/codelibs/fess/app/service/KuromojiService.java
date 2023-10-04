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
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;

public class KuromojiService {
    @Resource
    protected DictionaryManager dictionaryManager;

    @Resource
    protected FessConfig fessConfig;

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

    public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
        return dictionaryManager.getDictionaryFile(dictId).filter(KuromojiFile.class::isInstance)
                .map(file -> OptionalEntity.of((KuromojiFile) file)).orElse(OptionalEntity.empty());
    }

    public OptionalEntity<KuromojiItem> getKuromojiItem(final String dictId, final long id) {
        return getKuromojiFile(dictId).map(file -> file.get(id).get());
    }

    public void store(final String dictId, final KuromojiItem kuromojiItem) {
        getKuromojiFile(dictId).ifPresent(file -> {
            if (kuromojiItem.getId() == 0) {
                file.insert(kuromojiItem);
            } else {
                file.update(kuromojiItem);
            }
        });
    }

    public void delete(final String dictId, final KuromojiItem kuromojiItem) {
        getKuromojiFile(dictId).ifPresent(file -> {
            file.delete(kuromojiItem);
        });
    }
}
