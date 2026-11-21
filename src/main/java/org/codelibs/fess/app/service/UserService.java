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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.exception.FessUserNotFoundException;
import org.codelibs.fess.exception.UserChangedConcurrentlyException;
import org.codelibs.fess.exception.UserConflictException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.EntityRowHandler;
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

    /** How many times a change of a user is tried when the user is changed in between. */
    private static final int CONDITIONAL_WRITE_ATTEMPTS = 3;

    /** How many collision groups, and how many names with whitespace, the start-up warning lists. */
    private static final int MAX_REPORTED_NAMES = 20;

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
            // the version of each user is what an edit carries to tell that it is out of date
            cb.request().setVersion(true);
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
     * <p>A rename onto a name another user already holds is refused before anything is removed. So is a new user
     * (one that was not read, so it carries no sequence number) whose ID is already stored: it would replace that
     * user, with its password and roles. The new user is written with create semantics, so a request that
     * creates the same name at the same time is refused as well: exactly one of them is stored.</p>
     *
     * <p>With {@code authentication.local.username.policy=mapped}, a new user and a renamed one are also refused
     * when the name, once mapped by {@link SystemHelper#mapLocalUsername(String)}, is the mapped name of another
     * user or of an {@code authentication.admin.users} entry (other than the entry as written). Nothing is refused when a
     * user is stored under the name it already has, so a pair that collides already stays editable.</p>
     *
     * <p>A user that was read (it carries the sequence number and primary term it was read with) is written
     * on the condition that it has not been changed since. The passkeys that a rename removes and the
     * external authentication manager are handled before that write, so a refused save does not
     * undo them.</p>
     *
     * @param user the user entity to store
     * @throws UserConflictException if the user is renamed to the name of another user
     * @throws UserConflictException under the mapped policy, if a new or renamed user's name collides with another user's or is a variant of an admin user name
     * @throws UserConflictException if a new user has the ID of a stored one, or one is stored while it is being created
     * @throws UserChangedConcurrentlyException if the user was read and has been changed by someone else since
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

            if (hasVersion(user)) {
                // The user was read, and the whole document is written back: a change made in between
                // (a password change, for one) must not be put back to what was read.
                if (!updateIfUnchanged(user)) {
                    throw new UserChangedConcurrentlyException(
                            "The user was changed by someone else, so it was not saved. Load it again and retry: username=" + username);
                }
            } else {
                insertNew(user);
            }
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
     * Writes a new user that must not replace a stored one: the index rejects the write when a document with
     * the ID of the user exists, which is how a second request that creates the same name at the same time
     * is refused after {@link #removeInheritablePasskeys(User)} let both pass.
     *
     * @param user the new user
     * @throws UserConflictException if a user with that ID is stored
     */
    private void insertNew(final User user) {
        try {
            userBhv.insert(user, op -> {
                op.setCreate(true);
                op.setRefreshPolicy(Constants.TRUE);
            });
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                throw new UserConflictException("A user named " + user.getName() + " already exists");
            }
            throw e;
        }
    }

    /**
     * Removes the passkeys a user would otherwise inherit: those of its name when no row is stored under
     * its ID yet, and those of the previous and the new name when the stored row has another name.
     *
     * @param user the user about to be stored
     * @throws UserConflictException if the stored row is renamed to the name of another user
     */
    protected void removeInheritablePasskeys(final User user) {
        final String username = user.getName();
        final String previousName =
                StringUtil.isBlank(user.getId()) ? null : userBhv.selectByPK(user.getId()).map(User::getName).orElse(null);
        if (previousName == null) {
            refuseMappedNameConflict(user);
            passkeyService.deleteByUserName(username);
        } else if (!hasVersion(user)) {
            // A user that was not read is a new one, and its ID is made from its name, so the stored row is
            // that name's current holder: writing it would replace its password and roles.
            throw new UserConflictException("A user named " + username + " already exists");
        } else if (!previousName.equals(username)) {
            if (getUserByName(username).filter(other -> !user.getId().equals(other.getId())).isPresent()) {
                throw new UserConflictException("A user named " + username + " already exists");
            }
            refuseMappedNameConflict(user);
            passkeyService.deleteByUserName(previousName);
            passkeyService.deleteByUserName(username);
            if (logger.isInfoEnabled()) {
                logger.info("Removed the passkeys of a renamed user: previousName={}, username={}, id={}", previousName, username,
                        user.getId());
            }
        }
    }

    /**
     * Refuses a new name that collides with another user's, or is a variant of an authentication.admin.users entry, when the policy is mapped.
     *
     * <p>Does nothing, and reads nothing from the user index, under the default policy. Under mapped the
     * stored users are walked in full (a point-in-time scan, with no limit on their number), since no
     * mapped name is stored to look one up by. The user's own row is skipped, so that a rename that only
     * changes the case or width of its own name is allowed.</p>
     *
     * @param user the user about to be stored, with its new name
     * @throws UserConflictException if another user has the same mapped name, or the name is a variant of an admin user name
     */
    protected void refuseMappedNameConflict(final User user) {
        if (!ComponentUtil.getFessConfig().isLocalUsernameCaseMapped()) {
            return;
        }
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = user.getName();
        if (systemHelper.isAdminUserNameVariant(username)) {
            throw new UserConflictException("The name " + username
                    + " is a variant of a name in authentication.admin.users (compared without regard to case and width)"
                    + " by authentication.local.username.policy=mapped");
        }
        final String mappedName = systemHelper.mapLocalUsername(username);
        final String id = user.getId();
        final List<String> collidingNames = new ArrayList<>();
        userBhv.selectCursor(cb -> cb.query().matchAll(), new EntityRowHandler<User>() {
            @Override
            public void handle(final User other) {
                if (other.getName() != null && !Objects.equals(id, other.getId())
                        && mappedName.equals(systemHelper.mapLocalUsername(other.getName()))) {
                    collidingNames.add(other.getName());
                }
            }

            @Override
            public boolean isBreakCursor() {
                return !collidingNames.isEmpty();
            }
        });
        if (!collidingNames.isEmpty()) {
            throw new UserConflictException("A user named " + username + " already exists as " + collidingNames.get(0)
                    + ", and the names are compared without regard to case and width by authentication.local.username.policy=mapped");
        }
    }

    /**
     * Logs one WARN about the stored users whose names the mapped policy would not have let in, when the
     * policy is mapped: names that collide with another one once mapped, and names that contain whitespace
     * or control characters (which are easy to confuse with a name without them). Nothing is changed: the
     * users stay as they are and can still log in and be edited. At most {@value #MAX_REPORTED_NAMES}
     * collision groups and as many names with whitespace are listed, with the full counts.
     *
     * <p>It reads every user, so it is meant for the start of the webapp process. It never throws: a
     * failure to read the users is logged at debug.</p>
     *
     * @return true if the warning was written
     */
    public boolean reportLocalUsernameProblems() {
        try {
            if (!ComponentUtil.getFessConfig().isLocalUsernameCaseMapped()) {
                return false;
            }
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final Map<String, List<String>> namesByMappedName = new LinkedHashMap<>();
            final List<String> spacedNames = new ArrayList<>();
            userBhv.selectCursor(cb -> cb.query().matchAll(), other -> {
                final String name = other.getName();
                if (name == null) {
                    return;
                }
                namesByMappedName.computeIfAbsent(systemHelper.mapLocalUsername(name), k -> new ArrayList<>()).add(name);
                if (name.codePoints().anyMatch(c -> Character.isWhitespace(c) || Character.isSpaceChar(c) || Character.isISOControl(c))) {
                    spacedNames.add(name);
                }
            });
            final List<List<String>> collisions =
                    namesByMappedName.values().stream().filter(names -> names.size() > 1).collect(Collectors.toList());
            if (collisions.isEmpty() && spacedNames.isEmpty()) {
                return false;
            }
            logger.warn(
                    "authentication.local.username.policy=mapped, and the users stored now would not all be accepted by it. Nothing is changed: they can log in and be edited"
                            + " as before, but a new or renamed user is refused when its name collides. {} group(s) of names are the same once mapped"
                            + " (first {}: {}). {} name(s) contain whitespace or control characters (first {}: {}).",
                    collisions.size(), MAX_REPORTED_NAMES,
                    collisions.stream()
                            .limit(MAX_REPORTED_NAMES)
                            .map(names -> names.stream().map(UserService::quoteName).collect(Collectors.joining(" = ")))
                            .collect(Collectors.joining(", ")),
                    spacedNames.size(), MAX_REPORTED_NAMES,
                    spacedNames.stream().limit(MAX_REPORTED_NAMES).map(UserService::quoteName).collect(Collectors.joining(", ")));
            return true;
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not check the local user names against authentication.local.username.policy.", e);
            }
            return false;
        }
    }

    /**
     * Writes a user name for a log line: in quotes, with whitespace other than a plain space and control
     * characters as their {@code \\uXXXX} escape, so that a name with a trailing blank or a line break is visible.
     */
    private static String quoteName(final String name) {
        final StringBuilder buf = new StringBuilder(name.length() + 2).append('"');
        name.codePoints().forEach(c -> {
            if (c != ' ' && (Character.isWhitespace(c) || Character.isSpaceChar(c) || Character.isISOControl(c))) {
                buf.append(String.format("\\u%04X", c));
            } else {
                buf.appendCodePoint(c);
            }
        });
        return buf.append('"').toString();
    }

    /**
     * Changes the password for a user identified by username.
     * Updates both the authentication manager and the database with the new encrypted password.
     *
     * <p>The caller has not verified a current password (the user has just been authenticated another way,
     * for a password the user was told to change, say). Use {@link #changePassword(String, String, String)}
     * when the current password was verified, so that two requests cannot both succeed with it.</p>
     *
     * @param username the username of the user
     * @param password the new password in plain text
     * @throws IllegalArgumentException if the password is longer than the hash algorithm accepts; nothing is changed
     * @throws FessUserNotFoundException if the user is not found
     * @throws UserConflictException if the user was changed concurrently on each of the attempts
     */
    public void changePassword(final String username, final String password) {
        changePassword(username, null, password);
    }

    /**
     * Changes the password of a user whose current password the caller has just verified, on the condition
     * that the stored password is still that one.
     *
     * <p>Two requests that verify the same current password at the same time must not both succeed: the
     * second one's current password is no longer the stored one once the first has been written. So the
     * stored password is compared with {@code currentPassword} once before the authentication manager is
     * asked to change the password (a stale request is refused before it touches LDAP), and again on every
     * attempt of the conditional write, on the user that attempt has just read. The comparison is made
     * with {@link org.codelibs.fess.helper.PasswordHashHelper#matches}, not by comparing hash strings, so
     * a retry caused by a change that leaves the password alone (the sessions of the user being ended, a
     * re-hash at login that changes only the hash format, a passkey change) still succeeds.</p>
     *
     * <p>The authentication manager (LDAP) and the user index are two writes that are not atomic. When the
     * conflict is found after the authentication manager has changed the password, that change is not
     * undone: the external password is the new one while the local hash is not.</p>
     *
     * @param username the username of the user
     * @param currentPassword the current password in plain text, which the caller has verified
     * @param password the new password in plain text
     * @throws FessUserNotFoundException if the user is not found
     * @throws UserConflictException if the stored password is no longer the current password, or the user was
     *         changed concurrently on each of the attempts
     */
    public void changePassword(final String username, final String currentPassword, final String password) {
        if (logger.isDebugEnabled()) {
            logger.debug("Password change initiated for user: username={}", username);
        }

        // Hash first: a password the algorithm refuses (IllegalArgumentException) must be found before
        // AuthenticationManager.changePassword, which may already have changed the password in LDAP.
        final String encodedPassword = ComponentUtil.getPasswordHashHelper().encode(password);
        try {
            if (currentPassword != null) {
                // refuse a stale request before the authentication manager is asked to change anything
                userBhv.selectEntity(cb -> cb.query().setName_Equal(username))
                        .ifPresent(u -> verifyCurrentPassword(username, currentPassword, u));
            }
            final boolean changed = ComponentUtil.getAuthenticationManager().changePassword(username, password);
            if (changed) {
                for (int attempt = 0; attempt < CONDITIONAL_WRITE_ATTEMPTS; attempt++) {
                    final OptionalEntity<User> found = userBhv.selectEntity(cb -> cb.query().setName_Equal(username));
                    if (!found.isPresent()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to change password - user not found: username={}", username);
                        }
                        throw new FessUserNotFoundException(username);
                    }
                    final User entity = found.get();
                    if (currentPassword != null) {
                        verifyCurrentPassword(username, currentPassword, entity);
                    }
                    entity.setPassword(encodedPassword);
                    final long passwordChangedAt = nextPasswordChangedAt(entity);
                    entity.setPasswordChangedAt(passwordChangedAt);
                    if (!updateIfUnchanged(entity)) {
                        continue;
                    }
                    acceptPasswordChange(username, passwordChangedAt);

                    if (logger.isInfoEnabled()) {
                        logger.info("Password changed successfully for user: username={}, id={}", username, entity.getId());
                    }
                    return;
                }
                throw new UserConflictException("The user was changed concurrently, so its password was not changed: username=" + username);
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
     * Refuses the change when the stored password of the user is no longer the one the caller verified.
     *
     * @param username the username of the user
     * @param currentPassword the current password in plain text
     * @param stored the user as just read
     * @throws UserConflictException if the stored password does not match {@code currentPassword}
     */
    private void verifyCurrentPassword(final String username, final String currentPassword, final User stored) {
        if (!ComponentUtil.getPasswordHashHelper().matches(currentPassword, stored.getPassword())) {
            throw new UserConflictException(
                    "The password of the user was changed concurrently, so it was not changed again: username=" + username);
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
        for (int attempt = 0; attempt < CONDITIONAL_WRITE_ATTEMPTS; attempt++) {
            final OptionalEntity<User> found = userBhv.selectEntity(cb -> cb.query().setName_Equal(username));
            if (!found.isPresent()) {
                return;
            }
            final User entity = found.get();
            final long changedAt = nextPasswordChangedAt(entity);
            entity.setPasswordChangedAt(changedAt);
            // The whole document is written back, so the write is conditional on what was read: without
            // it a password change that lands between the read and the write would be put back to the
            // old hash.
            if (!updateIfUnchanged(entity)) {
                continue;
            }
            acceptPasswordChange(username, changedAt);
            if (logger.isInfoEnabled()) {
                logger.info("Ended the login sessions of the user: username={}, id={}", username, entity.getId());
            }
            return;
        }
        throw new UserConflictException("The user was changed concurrently, so its sessions were not ended: username=" + username);
    }

    /**
     * Tells whether the user was read from the index, so that its sequence number and primary term are known.
     *
     * @param user the user
     * @return true if the user carries them
     */
    private static boolean hasVersion(final User user) {
        final Long seqNo = user.asDocMeta().seqNo();
        final Long primaryTerm = user.asDocMeta().primaryTerm();
        return seqNo != null && seqNo.longValue() >= 0L && primaryTerm != null && primaryTerm.longValue() > 0L;
    }

    /**
     * Writes a user that was read, on the condition that it has not been changed since: the sequence number
     * and primary term it was read with go into the request, which the index rejects with a version conflict
     * otherwise. A user that carries no version is written without the condition.
     *
     * @param user the user, as read and changed
     * @return false if the stored user was changed in the meantime, true if it was written
     */
    private boolean updateIfUnchanged(final User user) {
        final boolean versioned = hasVersion(user);
        final Long seqNo = user.asDocMeta().seqNo();
        final Long primaryTerm = user.asDocMeta().primaryTerm();
        try {
            userBhv.update(user, op -> {
                op.setRefreshPolicy(Constants.TRUE);
                if (versioned) {
                    op.setIfSeqNo(seqNo);
                    op.setIfPrimaryTerm(primaryTerm);
                }
            });
            return true;
        } catch (final RuntimeException e) {
            if (isVersionConflict(e)) {
                return false;
            }
            throw e;
        }
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
     * @return epoch milliseconds, 0 when the user has no recorded password change, or empty when the user does not exist
     */
    public OptionalLong getPasswordChangedAt(final String username) {
        return userBhv.selectEntity(cb -> cb.query().setName_Equal(username))
                .map(user -> OptionalLong.of(user.getPasswordChangedAt() != null ? user.getPasswordChangedAt().longValue() : 0L))
                .orElse(OptionalLong.empty());
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
