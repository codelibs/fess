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

        if (logger.isDebugEnabled()) {
            logger.debug("User {} operation initiated: username={}, id={}", isUpdate ? "update" : "create", username,
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
        } finally {
            user.clearOriginalPassword();
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
        if (logger.isDebugEnabled()) {
            logger.debug("Password change initiated for user: username={}", username);
        }

        try {
            final boolean changed = ComponentUtil.getAuthenticationManager().changePassword(username, password);
            if (changed) {
                userBhv.selectEntity(cb -> cb.query().setName_Equal(username)).ifPresent(entity -> {
                    final String encodedPassword = ComponentUtil.getPasswordManager().encode(password);
                    entity.setPassword(encodedPassword);
                    userBhv.insertOrUpdate(entity, op -> op.setRefreshPolicy(Constants.TRUE));

                    if (logger.isInfoEnabled()) {
                        logger.info("Password changed successfully for user: username={}, id={}", username, entity.getId());
                    }
                }).orElse(() -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to change password - user not found: username={}", username);
                    }
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
     * Updates the stored password hash directly, bypassing AuthenticationManager chain.
     * Used by lazy re-hashing on successful login when the current stored hash
     * is in a legacy format. The input value MUST already be an encoded hash
     * (e.g., "{bcrypt}$2a$10$...") - this method does NOT hash it again.
     *
     * Does NOT propagate to LDAP/SSO backends because we do not have the plaintext
     * (and LDAP manages its own credential).
     *
     * <p>This overload performs an unconditional update. Prefer
     * {@link #updateStoredPasswordHash(String, String, String)} when a race with
     * concurrent password changes must be avoided.</p>
     *
     * @param username target user (must exist)
     * @param encodedPassword already-hashed password value
     * @return true if updated; false if user not found or update failed
     */
    public boolean updateStoredPasswordHash(final String username, final String encodedPassword) {
        return updateStoredPasswordHash(username, null, encodedPassword);
    }

    /**
     * Updates the stored password hash with an optional compare-and-set guard.
     * When {@code expectedCurrentHash} is non-null, the update is only applied
     * if the latest stored value equals that expected hash, mitigating a race
     * where another code path (e.g. explicit password change) writes a new hash
     * while this lazy re-hash is computing the new encoded value.
     *
     * @param username target user (must exist)
     * @param expectedCurrentHash the hash value observed at match time, or
     *        {@code null} to perform an unconditional update
     * @param newEncodedPassword already-hashed password value to store
     * @return {@code true} if the update was applied; {@code false} if the user
     *         was not found, the guard did not match, or the update failed
     */
    public boolean updateStoredPasswordHash(final String username, final String expectedCurrentHash, final String newEncodedPassword) {
        if (StringUtil.isBlank(username) || StringUtil.isBlank(newEncodedPassword)) {
            return false;
        }

        try {
            final OptionalEntity<User> optEntity = userBhv.selectEntity(cb -> cb.query().setName_Equal(username));
            if (!optEntity.isPresent()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("User not found for stored password hash update: username={}", username);
                }
                return false;
            }
            final User entity = optEntity.get();
            if (expectedCurrentHash != null && !expectedCurrentHash.equals(entity.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Stored password hash changed concurrently; skipping lazy upgrade: username={}", username);
                }
                return false;
            }
            entity.setPassword(newEncodedPassword);
            // Propagate the seqNo/primaryTerm observed at select time into the
            // index request itself so that OpenSearch rejects the update with a
            // version_conflict_engine_exception if another writer (e.g. an
            // explicit password change) landed between our select and update.
            // DBFlute's ESBhv update path does NOT do this automatically for
            // us; see EsAbstractBehavior.createUpdateRequest which only copies
            // the values back onto the entity. This is the atomic half of the
            // CAS — the in-memory equals() check above remains as a first-line
            // filter that avoids a wasted round-trip on the common case.
            final Long seqNo = entity.asDocMeta().seqNo();
            final Long primaryTerm = entity.asDocMeta().primaryTerm();
            userBhv.update(entity, op -> {
                op.setRefreshPolicy(Constants.TRUE);
                if (seqNo != null && seqNo.longValue() >= 0L) {
                    op.setIfSeqNo(seqNo.longValue());
                }
                if (primaryTerm != null && primaryTerm.longValue() >= 0L) {
                    op.setIfPrimaryTerm(primaryTerm.longValue());
                }
            });
            if (logger.isDebugEnabled()) {
                logger.debug("Upgraded stored password hash for user: username={}", username);
            }
            return true;
        } catch (final Exception e) {
            if (isVersionConflict(e)) {
                // A concurrent writer won the race; lazy rehash is best-effort
                // and the next successful login will retry, so a noisy WARN is
                // not warranted here.
                if (logger.isDebugEnabled()) {
                    logger.debug("Version conflict while upgrading password hash; skipping. username={}", username, e);
                }
                return false;
            }
            logger.warn("Failed to upgrade password hash for user. username={}", username, e);
            return false;
        }
    }

    /**
     * Tests whether the given throwable (or any of its causes) represents an
     * OpenSearch optimistic-concurrency conflict. Walks the cause chain and
     * inspects class name / message so we do not have to depend on a specific
     * OpenSearch exception type from the DBFlute layer above.
     *
     * @param t the throwable to inspect
     * @return {@code true} if {@code t} looks like a version-conflict
     */
    protected boolean isVersionConflict(final Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            final String name = cur.getClass().getName();
            if (name.endsWith("VersionConflictEngineException")) {
                return true;
            }
            final String msg = cur.getMessage();
            if (msg != null && msg.contains("version_conflict_engine_exception")) {
                return true;
            }
            final Throwable next = cur.getCause();
            if (next == cur) {
                break;
            }
            cur = next;
        }
        return false;
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

        if (logger.isDebugEnabled()) {
            logger.debug("User deletion initiated: username={}, id={}", username, userId);
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
