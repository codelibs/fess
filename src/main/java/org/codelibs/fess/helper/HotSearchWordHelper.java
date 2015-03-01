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

package org.codelibs.fess.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.db.exbhv.SearchLogBhv;
import org.codelibs.fess.db.exbhv.pmbean.HotSearchWordPmb;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class HotSearchWordHelper {

    @Resource
    protected SearchLogBhv searchLogBhv;

    protected Map<Range, List<String>> cacheMap = new ConcurrentHashMap<HotSearchWordHelper.Range, List<String>>();

    public int size = 10;

    public Pattern excludedWordPattern;

    @InitMethod
    public void init() {
        final LocalDateTime now = ComponentUtil.getSystemHelper()
                .getCurrentTime();
        cacheMap.put(Range.ONE_DAY,
                getHotSearchWordListByFromDate(now.minusDays(1)));
        cacheMap.put(Range.ONE_WEEK,
                getHotSearchWordListByFromDate(now.minusWeeks(1)));
        cacheMap.put(Range.ONE_MONTH,
                getHotSearchWordListByFromDate(now.minusMonths(1)));
        cacheMap.put(Range.ONE_YEAR,
                getHotSearchWordListByFromDate(now.minusYears(1)));
        cacheMap.put(Range.ENTIRE, getHotSearchWordListByFromDate(null));
    }

    protected List<String> getHotSearchWordListByFromDate(
            final LocalDateTime fromDate) {
        final HotSearchWordPmb pmb = new HotSearchWordPmb();

        if (fromDate != null) {
            pmb.setFromRequestedTime(fromDate);
        }

        final List<String> wordList = new ArrayList<String>();

        searchLogBhv.outsideSql().selectCursor(pmb, rs -> {
            while (rs.next()) {
                final String word = rs.getString("name");
                if (StringUtil.isBlank(word)) {
                    continue;
                }
                if (excludedWordPattern != null) {
                    if (!excludedWordPattern.matcher(word).matches()) {
                        wordList.add(word);
                    }
                } else {
                    wordList.add(word);
                }
                if (wordList.size() >= size) {
                    break;
                }
            }
            return null;
        });

        return wordList;
    }

    public void reload() {
        init();
    }

    public List<String> getHotSearchWordList(final Range range) {
        return cacheMap.get(range);
    }

    public enum Range {
        ONE_DAY(1), ONE_WEEK(7), ONE_MONTH(30), ONE_YEAR(365), ENTIRE(0);
        private final long time;

        private Range(final long t) {
            time = t * 24L * 60L * 60L * 1000L;
        }

        public long getTime() {
            return time;
        }
    }
}
