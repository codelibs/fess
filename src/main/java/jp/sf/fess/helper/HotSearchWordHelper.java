/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import jp.sf.fess.db.bsbhv.BsSearchLogBhv;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exbhv.pmbean.HotSearchWordPmb;

import org.seasar.dbflute.jdbc.CursorHandler;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class HotSearchWordHelper {

    @Resource
    protected SearchLogBhv searchLogBhv;

    protected Map<Range, List<String>> cacheMap = new ConcurrentHashMap<HotSearchWordHelper.Range, List<String>>();

    public int size = 10;

    public Pattern excludedWordPattern;

    @InitMethod
    public void init() {
        final long now = System.currentTimeMillis();
        cacheMap.put(Range.ONE_DAY, getHotSearchWordListByFromDate(new Date(now
                - Range.ONE_DAY.getTime())));
        cacheMap.put(Range.ONE_WEEK, getHotSearchWordListByFromDate(new Date(
                now - Range.ONE_WEEK.getTime())));
        cacheMap.put(Range.ONE_MONTH, getHotSearchWordListByFromDate(new Date(
                now - Range.ONE_MONTH.getTime())));
        cacheMap.put(Range.ONE_YEAR, getHotSearchWordListByFromDate(new Date(
                now - Range.ONE_YEAR.getTime())));
        cacheMap.put(Range.ENTIRE, getHotSearchWordListByFromDate(null));
    }

    protected List<String> getHotSearchWordListByFromDate(final Date fromDate) {
        final HotSearchWordPmb pmb = new HotSearchWordPmb();

        if (fromDate != null) {
            pmb.setFromRequestedTime(new Timestamp(fromDate.getTime()));
        }

        final List<String> wordList = new ArrayList<String>();

        final String path = BsSearchLogBhv.PATH_selectHotSearchWord;
        searchLogBhv.outsideSql().cursorHandling()
                .selectCursor(path, pmb, new CursorHandler() {
                    @Override
                    public Object handle(final ResultSet rs)
                            throws SQLException {
                        while (rs.next()) {
                            final String word = rs.getString("name");
                            if (excludedWordPattern != null) {
                                if (!excludedWordPattern.matcher(word)
                                        .matches()) {
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
                    }
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
