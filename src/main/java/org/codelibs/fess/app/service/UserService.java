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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.exception.FessUserNotFoundException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing user operations in the Fess search system.
 * This service provides CRUD operations for user management, including user authentication,
 * password management, and user listing with pagination support.
 *
 */
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * Default constructor for UserService.
     */
    public UserService() {
        // Default constructor
    }

    /** User behavior for database operations */
    @Resource
    protected UserBhv userBhv;

    /** Login assistance for authentication operations */
    @Resource
    protected FessLoginAssist fessLoginAssist;

    /** Fess configuration for system settings */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of users based on the provided pager criteria.
     * Updates the pager with pagination information including total count and page navigation.
     *
     * @param userPager the pager containing search criteria and pagination settings
     * @return a list of users matching the criteria
     */
    public List<User> getUserList(final UserPager userPager) {

        final PagingResultBean<User> userList = userBhv.selectPage(cb -> {
            cb.paging(userPager.getPageSize(), userPager.getCurrentPageNumber());
            setupListCondition(cb, userPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(userList, userPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        userPager.setPageNumberList(userList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return userList;
    }

    /**
     * Retrieves a user by their unique identifier.
     * Loads the user through the authentication manager for complete user data.
     *
     * @param id the unique identifier of the user
     * @return an OptionalEntity containing the user if found
     */
    public OptionalEntity<User> getUser(final String id) {
        return userBhv.selectByPK(id).map(u -> ComponentUtil.getAuthenticationManager().load(u));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return an OptionalEntity containing the user if found
     */
    public OptionalEntity<User> getUserByName(final String username) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
        });
    }

    /**
     * Stores (inserts or updates) a user in the system.
     * Handles user authentication setup and database persistence.
     * If the surname is blank, it will be set to the user's name.
     *
     * @param user the user entity to store
     */
    public void store(final User user) {
        final String username = user.getName();
        final boolean isUpdate = StringUtil.isNotBlank(user.getId());

        if (logger.isInfoEnabled()) {
            logger.info("User {} operation initiated: username={}, id={}", isUpdate ? "update" : "create", username,
                    user.getId() != null ? user.getId() : "new");
        }

        try {
            if (StringUtil.isBlank(user.getSurname())) {
                user.setSurname(user.getName());
            }

            ComponentUtil.getAuthenticationManager().insert(user);

            userBhv.insertOrUpdate(user, op -> {
                op.setRefreshPolicy(Constants.TRUE);
            });

            if (logger.isInfoEnabled()) {
                logger.info("User {} completed successfully: username={}, id={}", isUpdate ? "update" : "create", username, user.getId());
            }
        } catch (final Exception e) {
            logger.warn("Failed to {} user: username={}, id={}, error={}", isUpdate ? "update" : "create", username, user.getId(),
                    e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Changes the password for a user identified by username.
     * Updates both the authentication manager and the database with the new encrypted password.
     *
     * @param username the username of the user
     * @param password the new password in plain text
     * @throws FessUserNotFoundException if the user is not found
     */
    public void changePassword(final String username, final String password) {
        if (logger.isInfoEnabled()) {
            logger.info("Password change initiated for user: username={}", username);
        }

        try {
            final boolean changed = ComponentUtil.getAuthenticationManager().changePassword(username, password);
            if (changed) {
                userBhv.selectEntity(cb -> cb.query().setName_Equal(username)).ifPresent(entity -> {
                    final String encodedPassword = fessLoginAssist.encryptPassword(password);
                    entity.setPassword(encodedPassword);
                    userBhv.insertOrUpdate(entity, op -> op.setRefreshPolicy(Constants.TRUE));

                    if (logger.isInfoEnabled()) {
                        logger.info("Password changed successfully for user: username={}, id={}", username, entity.getId());
                    }
                }).orElse(() -> {
                    logger.error("Failed to change password - user not found: username={}", username);
                    throw new FessUserNotFoundException(username);
                });
            } else {
                logger.warn("Password change not applied by authentication manager: username={}", username);
            }
        } catch (final FessUserNotFoundException e) {
            throw e;
        } catch (final Exception e) {
            logger.warn("Failed to change password for user: username={}, error={}", username, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deletes a user from the system.
     * Removes the user from both the authentication manager and the database.
     *
     * @param user the user entity to delete
     */
    public void delete(final User user) {
        final String username = user.getName();
        final String userId = user.getId();

        if (logger.isInfoEnabled()) {
            logger.info("User deletion initiated: username={}, id={}", username, userId);
        }

        try {
            ComponentUtil.getAuthenticationManager().delete(user);

            userBhv.delete(user, op -> {
                op.setRefreshPolicy(Constants.TRUE);
            });

            if (logger.isInfoEnabled()) {
                logger.info("User deleted successfully: username={}, id={}", username, userId);
            }
        } catch (final Exception e) {
            logger.warn("Failed to delete user: username={}, id={}, error={}", username, userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Sets up the search conditions for user list queries based on pager criteria.
     * Configures the condition bean with search filters and ordering.
     *
     * @param cb the condition bean for the user query
     * @param userPager the pager containing search criteria
     */
    protected void setupListCondition(final UserCB cb, final UserPager userPager) {
        if (userPager.id != null) {
            cb.query().docMeta().setId_Equal(userPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    /**
     * Retrieves a list of all available users in the system.
     * Returns up to the maximum configured number of users.
     *
     * @return a list of all available users
     */
    public List<User> getAvailableUserList() {
        return userBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(fessConfig.getPageUserMaxFetchSizeAsInteger());
        });
    }

}
