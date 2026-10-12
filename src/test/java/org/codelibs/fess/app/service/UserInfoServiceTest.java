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

import java.time.Instant;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.log.cbean.UserInfoCB;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.bhv.readable.CBCall;
import org.junit.jupiter.api.Test;

public class UserInfoServiceTest extends UnitFessTestCase {

    @Test
    public void test_deleteBefore_cutoffIsUtc() {
        // a non-UTC default zone, as the stored dates are UTC instants and a zone-less condition is read as UTC
        final TimeZone original = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
        try {
            final AtomicReference<Object> query = new AtomicReference<>();
            final UserInfoService service = new UserInfoService();
            service.systemHelper = new SystemHelper() {
                @Override
                public long getCurrentTimeAsLong() {
                    return Instant.parse("2026-10-12T00:00:00Z").toEpochMilli();
                }
            };
            service.userInfoBhv = new UserInfoBhv() {
                @Override
                public int queryDelete(final CBCall<UserInfoCB> cbLambda) {
                    final UserInfoCB cb = new UserInfoCB();
                    cbLambda.callback(cb);
                    query.set(cb.query().getQuery());
                    return 0;
                }
            };

            service.deleteBefore(7);

            final String json = query.get().toString().replaceAll("\\s+", "");
            assertTrue(json.contains("\"updatedAt\":{\"from\":null,\"to\":\"2026-10-05T00:00:00\""), json);
        } finally {
            TimeZone.setDefault(original);
        }
    }
}
