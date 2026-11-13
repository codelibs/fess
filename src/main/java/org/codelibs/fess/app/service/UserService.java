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

    /** Passkey service, so that a deleted user does not leave passkeys behind for a later user of the same name */
    @Resource
    protected PasskeyService passkeyService;

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
     * <p>Passkeys are bound to a user name only, so a name that is new to the stored row starts
     * without any: when the row is created, every passkey left under its name (by a deletion that did
     * not go through {@link #delete(User)}, or one that raced with a registration) is removed first,
     * and when the row is renamed the passkeys of both the previous and the new name are removed, since
     * credentials do not follow a rename. A failure to remove them fails the store before the user is
     * written.</p>
     *
     * <p>A rename onto a name another user already holds is refused before anything is removed.</p>
     *
     * @param user the user entity to store
     * @throws IllegalArgumentException if the user is renamed to the name of another user
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

            removeInheritablePasskeys(user);

            ComponentUtil.getAuthenticationManager().insert(user);

            // The original password is set only when the caller sets a new one.
            final Long passwordChangedAt = StringUtil.isNotBlank(user.getOriginalPassword()) ? nextPasswordChangedAt(user) : null;
            if (passwordChangedAt != null) {
                user.setPasswordChangedAt(passwordChangedAt);
            }

            userBhv.insertOrUpdate(user, op -> {
                op.setRefreshPolicy(Constants.TRUE);
            });
            if (passwordChangedAt != null) {
                acceptPasswordChange(username, passwordChangedAt);
            }

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
     * Removes the passkeys a user would otherwise inherit: those of its name when no row is stored under
     * its ID yet, and those of the previous and the new name when the stored row has another name.
     *
     * @param user the user about to be stored
     * @throws IllegalArgumentException if the stored row is renamed to the name of another user
     */
    protected void removeInheritablePasskeys(final User user) {
        final String username = user.getName();
        final String previousName =
                StringUtil.isBlank(user.getId()) ? null : userBhv.selectByPK(user.getId()).map(User::getName).orElse(null);
        if (previousName == null) {
            passkeyService.deleteByUserName(username);
        } else if (!previousName.equals(username)) {
            if (getUserByName(username).filter(other -> !user.getId().equals(other.getId())).isPresent()) {
                throw new IllegalArgumentException("A user named " + username + " already exists");
            }
            passkeyService.deleteByUserName(previousName);
            passkeyService.deleteByUserName(username);
            if (logger.isInfoEnabled()) {
                logger.info("Removed the passkeys of a renamed user: previousName={}, username={}, id={}", previousName, username,
                        user.getId());
            }
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
                    final String encodedPassword = ComponentUtil.getPasswordHashHelper().encode(password);
                    entity.setPassword(encodedPassword);
                    final long passwordChangedAt = nextPasswordChangedAt(entity);
                    entity.setPasswordChangedAt(passwordChangedAt);
                    userBhv.insertOrUpdate(entity, op -> op.setRefreshPolicy(Constants.TRUE));
                    acceptPasswordChange(username, passwordChangedAt);

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
     * Ends the login sessions of a local user that were opened before now, except the one that makes
     * the request when it is the user's own. It records a new time in the field a password change
     * records, so the sessions end the way they do after a password change: when they next check the
     * user index, within {@code password.changed.check.interval} seconds. A user that is not stored
     * is left alone.
     *
     * <p>Removing a passkey calls this, so a session opened with a lost authenticator does not outlive it.</p>
     *
     * @param username the name of the user
     */
    public void endOtherSessions(final String username) {
        userBhv.selectEntity(cb -> cb.query().setName_Equal(username)).ifPresent(entity -> {
            final long changedAt = nextPasswordChangedAt(entity);
            entity.setPasswordChangedAt(changedAt);
            userBhv.insertOrUpdate(entity, op -> op.setRefreshPolicy(Constants.TRUE));
            acceptPasswordChange(username, changedAt);
            if (logger.isInfoEnabled()) {
                logger.info("Ended the login sessions of the user: username={}, id={}", username, entity.getId());
            }
        });
    }

    /**
     * Gets the time to record for a password change of the user: now, but later than the time
     * already recorded, so that a node whose clock is behind still ends the sessions opened before.
     *
     * @param user the user whose password is changed
     * @return epoch milliseconds
     */
    protected long nextPasswordChangedAt(final User user) {
        final Long previous = user.getPasswordChangedAt();
        final long now = System.currentTimeMillis();
        return previous == null ? now : Math.max(now, previous.longValue() + 1);
    }

    /**
     * Tells the login assist that a password was changed, so that the login session that made the
     * change stays open while the other sessions of the user end.
     *
     * <p>There is no login session to keep when the change is not made by a request, and the
     * password has been changed by then, so a failure here is not one of the change.</p>
     *
     * @param username the name of the user
     * @param changedAt the recorded time
     */
    protected void acceptPasswordChange(final String username, final long changedAt) {
        try {
            ComponentUtil.getFessLoginAssist().acceptPasswordChange(username, changedAt);
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not update the login session for the password change: username={}", username, e);
            }
        }
    }

    /**
     * Gets when the password of a local user was last changed.
     *
     * @param username the username of the user
     * @return epoch milliseconds, or 0 when the user has no recorded password change or does not exist
     */
    public long getPasswordChangedAt(final String username) {
        return userBhv.selectEntity(cb -> cb.query().setName_Equal(username)).map(User::getPasswordChangedAt).orElse(0L);
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
                    op.setIfSeqNo(seqNo);
                }
                if (primaryTerm != null && primaryTerm.longValue() >= 0L) {
                    op.setIfPrimaryTerm(primaryTerm);
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
     * Removes the user's passkeys first, then the user from both the authentication manager and the
     * database, then the passkeys once more. Passkeys are bound to a user by name only, so they must
     * not outlive the user: a user created later under the same name would otherwise inherit them. If
     * the first removal fails the exception propagates and the user is left untouched. The second one
     * catches a passkey registered while the user was being deleted; it is best effort, since the user
     * is gone by then and {@link #store(User)} removes the passkeys of a name again when it is created.
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
            passkeyService.deleteByUserName(username);

            ComponentUtil.getAuthenticationManager().delete(user);

            userBhv.delete(user, op -> {
                op.setRefreshPolicy(Constants.TRUE);
            });

            try {
                passkeyService.deleteByUserName(username);
            } catch (final RuntimeException e) {
                logger.warn("Failed to remove the passkeys registered while the user was deleted: username={}, id={}", username, userId, e);
            }

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
