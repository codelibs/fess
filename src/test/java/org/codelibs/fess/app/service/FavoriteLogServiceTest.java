/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.fesen.opensearch.action.admin.indices.refresh.RefreshResponse;
import org.codelibs.fess.app.service.FavoriteLogService.FavoriteResult;
import org.codelibs.fess.entity.SearchLogEvent;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.opensearch.log.cbean.FavoriteLogCB;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.opensearch.log.exentity.FavoriteLog;
import org.codelibs.fess.opensearch.log.exentity.UserInfo;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;

/**
 * A favorite posted right after the user's first search: the user_info is written asynchronously by that search and
 * selectByPK is a search, so the entry is not found until the index refreshes.
 */
public class FavoriteLogServiceTest extends UnitFessTestCase {

    private static final class LaggingUserInfoBhv extends UserInfoBhv {
        int lookups;
        int refreshes;

        @Override
        public OptionalEntity<UserInfo> selectByPK(final String id) {
            lookups++;
            if (refreshes == 0) {
                return OptionalEntity.empty();
            }
            final UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            return OptionalEntity.of(userInfo);
        }

        @Override
        public RefreshResponse refresh() {
            refreshes++;
            return null;
        }
    }

    private static final class RecordingFavoriteLogBhv extends FavoriteLogBhv {
        int inserted;

        @Override
        public int selectCount(final CBCall<FavoriteLogCB> cbLambda) {
            return 0;
        }

        @Override
        public void insert(final FavoriteLog entity) {
            inserted++;
        }

        @Override
        public RefreshResponse refresh() {
            return null;
        }
    }

    private FavoriteLogService newService(final UserInfoBhv userInfoBhv, final FavoriteLogBhv favoriteLogBhv) {
        final FavoriteLogService service = new FavoriteLogService();
        service.userInfoBhv = userInfoBhv;
        service.favoriteLogBhv = favoriteLogBhv;
        service.fessConfig = ComponentUtil.getFessConfig();
        return service;
    }

    @Test
    public void test_addUrl_userNotYetSearchableIsFoundAfterOneRefresh() {
        // a fresh add is also written to the search log file; the real helper is not needed for that
        ComponentUtil.register(new SearchLogHelper() {
            @Override
            public void writeSearchLogEvent(final SearchLogEvent event) {
                // no-op
            }
        }, "searchLogHelper");
        final LaggingUserInfoBhv userInfoBhv = new LaggingUserInfoBhv();
        final RecordingFavoriteLogBhv favoriteLogBhv = new RecordingFavoriteLogBhv();

        final FavoriteResult result = newService(userInfoBhv, favoriteLogBhv).addUrl("code",
                (userInfo, favoriteLog) -> favoriteLog.setUrl("http://example.com/"));

        assertEquals(FavoriteResult.ADDED, result);
        assertEquals(1, userInfoBhv.refreshes);
        assertEquals(2, userInfoBhv.lookups);
        assertEquals(1, favoriteLogBhv.inserted);
    }

    @Test
    public void test_addUrl_userFoundAtOnceIsNotRefreshedFor() {
        ComponentUtil.register(new SearchLogHelper() {
            @Override
            public void writeSearchLogEvent(final SearchLogEvent event) {
                // no-op
            }
        }, "searchLogHelper");
        final LaggingUserInfoBhv userInfoBhv = new LaggingUserInfoBhv();
        userInfoBhv.refreshes = 1; // already searchable
        final RecordingFavoriteLogBhv favoriteLogBhv = new RecordingFavoriteLogBhv();

        final FavoriteResult result = newService(userInfoBhv, favoriteLogBhv).addUrl("code",
                (userInfo, favoriteLog) -> favoriteLog.setUrl("http://example.com/"));

        assertEquals(FavoriteResult.ADDED, result);
        assertEquals(1, userInfoBhv.refreshes);
        assertEquals(1, userInfoBhv.lookups);
    }

    @Test
    public void test_addUrl_userStillUnknownAfterTheRefreshIsNoSuchUser() {
        final FavoriteLogService service = newService(new UserInfoBhv() {
            @Override
            public OptionalEntity<UserInfo> selectByPK(final String id) {
                return OptionalEntity.empty();
            }

            @Override
            public RefreshResponse refresh() {
                return null;
            }
        }, new RecordingFavoriteLogBhv());

        assertEquals(FavoriteResult.NO_SUCH_USER,
                service.addUrl("nobody", (userInfo, favoriteLog) -> favoriteLog.setUrl("http://example.com/")));
    }
}
