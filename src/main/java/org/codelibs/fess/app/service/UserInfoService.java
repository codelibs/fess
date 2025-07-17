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

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;

import jakarta.annotation.Resource;

/**
 * Service class for managing user information data.
 * This service provides operations for maintaining and cleaning up user information records.
 */
public class UserInfoService {

    @Resource
    private UserInfoBhv userInfoBhv;

    @Resource
    private SystemHelper systemHelper;

    /**
     * Default constructor for UserInfoService.
     */
    public UserInfoService() {
        // Default constructor
    }

    /**
     * Deletes user information records older than the specified number of days.
     * This method is used for data cleanup and maintenance operations.
     *
     * @param days the number of days to keep user information records
     */
    public void deleteBefore(final int days) {
        userInfoBhv.queryDelete(cb -> {
            cb.query().setUpdatedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

}
