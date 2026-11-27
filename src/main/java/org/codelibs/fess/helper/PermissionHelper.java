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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.IOException;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.CredentialUrlUtil;
import org.codelibs.jcifs.smb.SID;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.Resource;

/**
 * Helper class for handling permission-related operations in Fess.
 * Provides functionality to encode/decode permission strings and extract
 * role type information from various file system protocols (SMB, file, FTP).
 */
public class PermissionHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PermissionHelper.class);

    /** Prefix used to identify role-based permissions */
    protected String rolePrefix = "{role}";

    /** Prefix used to identify group-based permissions */
    protected String groupPrefix = "{group}";

    /** Prefix used to identify user-based permissions */
    protected String userPrefix = "{user}";

    /** Prefix used to identify allow permissions */
    protected String allowPrefix = "(allow)";

    /** Prefix used to identify deny permissions */
    protected String denyPrefix = "(deny)";

    /** System helper for user/group/role search operations */
    @Resource
    protected SystemHelper systemHelper;

    /**
     * Default constructor for PermissionHelper.
     * Initializes the permission helper with default configuration.
     */
    public PermissionHelper() {
        // Default constructor
    }

    /**
     * Encodes a permission string into a search role format.
     * Processes user, group, and role prefixes along with allow/deny prefixes.
     * Any number of stacked {@code (allow)} / {@code (deny)} prefixes, in any order and case, collapse to
     * one effect: the entry is denied when at least one of them is {@code (deny)}, so that
     * {@code (allow)(deny){role}x} and {@code (deny)(allow){role}x} are both stored as {@code (deny){role}x}.
     *
     * @param value the permission string to encode
     * @return the encoded permission string, or null if the input is blank or invalid
     */
    public String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        String permission = stripSpaces(value);
        boolean denied = false;
        // stacked prefixes collapse to one effect: a single (deny) among them denies the entry
        for (String prefix = aclPrefixOf(permission); prefix != null; prefix = aclPrefixOf(permission)) {
            denied |= prefix.equals(denyPrefix);
            permission = stripSpaces(permission.substring(prefix.length()));
        }
        final String aclPrefix = denied ? ComponentUtil.getFessConfig().getRoleSearchDeniedPrefix() : StringUtil.EMPTY;
        if (permission.isEmpty()) {
            return null;
        }
        final String lower = permission.toLowerCase(Locale.ROOT);
        if (lower.startsWith(userPrefix)) {
            if (permission.length() > userPrefix.length()) {
                return aclPrefix + systemHelper.getSearchRoleByUser(permission.substring(userPrefix.length()));
            }
            return null;
        }
        if (lower.startsWith(groupPrefix)) {
            if (permission.length() > groupPrefix.length()) {
                return aclPrefix + systemHelper.getSearchRoleByGroup(permission.substring(groupPrefix.length()));
            }
            return null;
        }
        if (lower.startsWith(rolePrefix)) {
            if (permission.length() > rolePrefix.length()) {
                return aclPrefix + systemHelper.getSearchRoleByRole(permission.substring(rolePrefix.length()));
            }
            return null;
        }
        if (denied) {
            // a deny entry must name a user, group or role; never store it as a raw value
            return null;
        }
        return permission;
    }

    /**
     * Returns the {@code (allow)} / {@code (deny)} prefix the value starts with, compared without regard to case.
     *
     * @param value the permission entry
     * @return the configured prefix, or null if the value starts with neither
     */
    private String aclPrefixOf(final String value) {
        final String lower = value.toLowerCase(Locale.ROOT);
        if (!allowPrefix.isEmpty() && lower.startsWith(allowPrefix)) {
            return allowPrefix;
        }
        if (!denyPrefix.isEmpty() && lower.startsWith(denyPrefix)) {
            return denyPrefix;
        }
        return null;
    }

    /**
     * Checks the permission entries typed into a permission field of the admin screens and the admin
     * API, one entry per line, and adds an error for the {@code permissions} field when an entry is
     * written with whitespace where none is expected (see {@link #isSpacedEntry(String)}).
     * <p>
     * {@link #encode(String)} stays lenient because it also parses configuration values that are
     * read while searching, where an exception would break the request. An entry entered by a
     * person is checked here instead of being converted without a word.
     * </p>
     *
     * @param permissionsText the permission entries, separated by line breaks (may be null)
     * @param messages the messages to add the error to
     */
    public void validateEntries(final String permissionsText, final FessMessages messages) {
        if (hasSpacedEntry(typedPermissions(permissionsText))) {
            messages.addErrorsInvalidPermissionSpacing(Constants.PERMISSIONS, denyPrefix + userPrefix + "name",
                    allowPrefix + userPrefix + "name");
        } else if (hasFullWidthSyntaxEntry(typedPermissions(permissionsText))) {
            messages.addErrorsInvalidPermissionFullwidth(Constants.PERMISSIONS, denyPrefix + userPrefix + "name",
                    allowPrefix + userPrefix + "name");
        } else if (hasInvisibleLeadEntry(typedPermissions(permissionsText))) {
            messages.addErrorsInvalidPermissionInvisible(Constants.PERMISSIONS, denyPrefix + userPrefix + "name",
                    allowPrefix + userPrefix + "name");
        }
    }

    /**
     * Returns whether any line of the permission entries starts with an invisible character (see
     * {@link #isInvisibleLeadEntry(String)}).
     *
     * @param permissionsText the permission entries, separated by line breaks (may be null)
     * @return true if a line is such an entry
     */
    public boolean hasInvisibleLeadEntry(final String permissionsText) {
        return StringUtil.isNotBlank(permissionsText)
                && split(permissionsText, "\n").get(stream -> stream.anyMatch(this::isInvisibleLeadEntry));
    }

    /**
     * Returns whether a permission entry starts with an invisible character, such as a zero-width space, a
     * byte order mark or a control character, at the start of the entry, right after an {@code (allow)} /
     * {@code (deny)} prefix, or right after the {@code {user}} / {@code {group}} / {@code {role}} marker. Such a character is not whitespace to Java and does not change under Unicode normalisation, so the
     * entry passes the other checks, yet {@link #encode(String)} no longer finds the {@code {user}},
     * {@code {group}} or {@code {role}} marker: a deny is dropped without a word, and a plain value is stored
     * that matches nobody. The invisible character is typically pasted in with the text.
     * <p>
     * Only the lead of the entry and of its name are judged. A character of the same kind inside or at the end of
     * a name stays valid, because the zero-width joiner is part of emoji sequences and a direction mark can belong
     * to a name written right to left.
     * </p>
     *
     * @param entry a single permission entry (may be null)
     * @return true if the entry has an invisible character where its prefix or marker should start
     */
    public boolean isInvisibleLeadEntry(final String entry) {
        if (StringUtil.isBlank(entry)) {
            return false;
        }
        String rest = stripSpaces(entry);
        if (startsWithInvisible(rest)) {
            return true;
        }
        for (String prefix = aclPrefixOf(rest); prefix != null; prefix = aclPrefixOf(rest)) {
            rest = rest.substring(prefix.length());
            if (startsWithInvisible(rest)) {
                return true;
            }
            rest = stripSpaces(rest);
        }
        final String lower = rest.toLowerCase(Locale.ROOT);
        for (final String marker : new String[] { userPrefix, groupPrefix, rolePrefix }) {
            if (lower.startsWith(marker)) {
                return startsWithInvisible(rest.substring(marker.length()));
            }
        }
        return false;
    }

    private static boolean startsWithInvisible(final String value) {
        if (value.isEmpty()) {
            return false;
        }
        final int first = value.codePointAt(0);
        return Character.getType(first) == Character.FORMAT || Character.isISOControl(first);
    }

    /**
     * Returns whether any line of the permission entries writes the {@code (allow)} / {@code (deny)} prefix or
     * the {@code {user}} / {@code {group}} / {@code {role}} marker with full-width characters (see
     * {@link #isFullWidthSyntaxEntry(String)}).
     *
     * @param permissionsText the permission entries, separated by line breaks (may be null)
     * @return true if a line is such an entry
     */
    public boolean hasFullWidthSyntaxEntry(final String permissionsText) {
        return StringUtil.isNotBlank(permissionsText)
                && split(permissionsText, "\n").get(stream -> stream.anyMatch(this::isFullWidthSyntaxEntry));
    }

    /**
     * Returns whether a permission entry spells its {@code (allow)} / {@code (deny)} prefix or its
     * {@code {user}} / {@code {group}} / {@code {role}} marker with full-width characters, as a Japanese
     * keyboard produces them ({@code （deny）｛user｝bob}). {@link #encode(String)} does not know such a
     * spelling: it would store the entry as a plain value, or drop a deny, so the document would stay
     * visible to the person it was meant to be hidden from.
     * <p>
     * Only the leading syntax is judged. The name that follows it is taken as typed, so a role named with
     * full-width or other non-ASCII letters stays valid.
     * </p>
     *
     * @param entry a single permission entry (may be null)
     * @return true if the syntax of the entry is not the half-width one that {@link #encode(String)} reads
     */
    public boolean isFullWidthSyntaxEntry(final String entry) {
        if (StringUtil.isBlank(entry)) {
            return false;
        }
        final String stripped = stripSpaces(entry);
        final String normalized = Normalizer.normalize(stripped, Normalizer.Form.NFKC);
        if (normalized.equals(stripped)) {
            return false;
        }
        final Pattern syntax = entrySyntax();
        final Matcher typed = syntax.matcher(stripped);
        final Matcher intended = syntax.matcher(normalized);
        typed.find();
        intended.find();
        return !sameSyntax(typed.group(1), intended.group(1)) || !sameSyntax(typed.group(2), intended.group(2));
    }

    /**
     * The pattern of what {@link #encode(String)} reads at the start of an entry: the allow / deny prefix
     * and the user / group / role marker, as configured in this helper.
     */
    private Pattern entrySyntax() {
        return Pattern.compile("^(?:(" + Pattern.quote(allowPrefix) + "|" + Pattern.quote(denyPrefix) + ")\\s*)?("
                + Pattern.quote(userPrefix) + "|" + Pattern.quote(groupPrefix) + "|" + Pattern.quote(rolePrefix) + ")?",
                Pattern.CASE_INSENSITIVE);
    }

    private static boolean sameSyntax(final String a, final String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    /**
     * Returns the permission entries as they were typed. The form mapping of the admin screens trims every
     * parameter, which removes the space in front of the first entry, so a screen that shows such an entry
     * (' {user}bob', saved by an earlier version) would pass the check with it. The request parameter keeps
     * the space; it is used when it is the same text apart from that trimming.
     *
     * @param mapped the permission entries as the form holds them (may be null)
     * @return the typed text, or the mapped text when the request carries no such parameter
     */
    protected String typedPermissions(final String mapped) {
        if (mapped == null) {
            return null;
        }
        return LaRequestUtil.getOptionalRequest()
                .map(request -> request.getParameter(Constants.PERMISSIONS))
                .filter(raw -> raw.trim().replace("\r\n", "\n").replace('\r', '\n').equals(mapped))
                .orElse(mapped);
    }

    /**
     * Returns whether any line of the permission entries is a spaced entry.
     *
     * @param permissionsText the permission entries, separated by line breaks (may be null)
     * @return true if a line is a spaced entry
     */
    public boolean hasSpacedEntry(final String permissionsText) {
        return StringUtil.isNotBlank(permissionsText) && split(permissionsText, "\n").get(stream -> stream.anyMatch(this::isSpacedEntry));
    }

    /**
     * Returns whether a permission entry is written with whitespace where none is expected. That is
     * the case when whitespace sits between the {@code (allow)} / {@code (deny)} prefix and what
     * follows it, such as {@code (deny) {user}bob}, and when the entry starts with whitespace
     * followed by a {@code {user}}, {@code {group}} or {@code {role}} name, which is how an
     * entry such as {@code (deny) {user}bob} was stored by Fess 15.9. Whitespace before an
     * {@code (allow)} / {@code (deny)} prefix, whitespace around a plain value and trailing
     * whitespace are not spaced entries.
     *
     * @param entry a single permission entry (may be null)
     * @return true if the entry is a spaced entry
     */
    public boolean isSpacedEntry(final String entry) {
        if (StringUtil.isBlank(entry)) {
            return false;
        }
        final String stripped = stripSpaces(entry);
        final String lower = stripped.toLowerCase(Locale.ROOT);
        String rest = stripped;
        for (String prefix = aclPrefixOf(rest); prefix != null; prefix = aclPrefixOf(rest)) {
            rest = rest.substring(prefix.length());
            if (startsWithWhitespace(rest)) {
                return true;
            }
        }
        if (!rest.equals(stripped)) {
            return false;
        }
        return startsWithWhitespace(entry)
                && (lower.startsWith(userPrefix) || lower.startsWith(groupPrefix) || lower.startsWith(rolePrefix));
    }

    /**
     * Returns whether the permission entries hold nothing but spaces and line breaks, so that no entry is left once
     * they are stripped. Unlike {@link StringUtil#isBlank(String)}, a no-break space (U+00A0, U+2007, U+202F) counts
     * as a space, as it does when an entry is encoded or checked.
     *
     * @param permissionsText the permission entries, separated by line breaks (may be null)
     * @return true if the text is null or has only spaces
     */
    public boolean isBlank(final String permissionsText) {
        return permissionsText == null || stripSpaces(permissionsText).isEmpty();
    }

    private boolean startsWithWhitespace(final String value) {
        return !value.isEmpty() && isSpace(value.codePointAt(0));
    }

    /**
     * Returns whether a character is a space in a permission entry: whitespace to Java, or a space separator that
     * {@link Character#isWhitespace(int)} leaves out because it does not break a line (U+00A0, U+2007, U+202F).
     * Such a no-break space is typically pasted in with the text, and has to be treated like the space it looks like.
     */
    private static boolean isSpace(final int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
    }

    /**
     * Removes the spaces (see {@link #isSpace(int)}) at the start and the end of a value, as {@link String#strip()}
     * does for whitespace.
     */
    private static String stripSpaces(final String value) {
        int start = 0;
        int end = value.length();
        while (start < end && isSpace(value.codePointAt(start))) {
            start += Character.charCount(value.codePointAt(start));
        }
        while (end > start && isSpace(value.codePointBefore(end))) {
            end -= Character.charCount(value.codePointBefore(end));
        }
        return value.substring(start, end);
    }

    /**
     * Decodes a search role format string back to a permission string.
     * Reverses the encoding process to restore original permission format.
     *
     * @param value the encoded permission string to decode
     * @return the decoded permission string, or null if the input is blank or invalid
     */
    public String decode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String aclPrefix = StringUtil.EMPTY;
        String permission = value;
        final String deniedPrefix = fessConfig.getRoleSearchDeniedPrefix();
        if (value.startsWith(deniedPrefix)) {
            // the denied prefix only counts when a user/group/role entry follows it (e.g. "Developers" is not denied)
            final String remainder = value.substring(deniedPrefix.length());
            if (hasTypedName(remainder, fessConfig.getRoleSearchUserPrefix())
                    || hasTypedName(remainder, fessConfig.getRoleSearchGroupPrefix())
                    || hasTypedName(remainder, fessConfig.getRoleSearchRolePrefix())) {
                permission = remainder;
                aclPrefix = denyPrefix;
            }
        }
        if (hasTypedName(permission, fessConfig.getRoleSearchUserPrefix())) {
            return aclPrefix + userPrefix + permission.substring(fessConfig.getRoleSearchUserPrefix().length());
        }
        if (hasTypedName(permission, fessConfig.getRoleSearchGroupPrefix())) {
            return aclPrefix + groupPrefix + permission.substring(fessConfig.getRoleSearchGroupPrefix().length());
        }
        if (hasTypedName(permission, fessConfig.getRoleSearchRolePrefix())) {
            return aclPrefix + rolePrefix + permission.substring(fessConfig.getRoleSearchRolePrefix().length());
        }
        return permission;
    }

    private boolean hasTypedName(final String permission, final String typePrefix) {
        return permission.startsWith(typePrefix) && permission.length() > typePrefix.length();
    }

    /**
     * Sets the prefix used to identify role-based permissions.
     *
     * @param rolePrefix the role prefix to set
     */
    public void setRolePrefix(final String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    /**
     * Sets the prefix used to identify group-based permissions.
     *
     * @param groupPrefix the group prefix to set
     */
    public void setGroupPrefix(final String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    /**
     * Sets the prefix used to identify user-based permissions.
     *
     * @param userPrefix the user prefix to set
     */
    public void setUserPrefix(final String userPrefix) {
        this.userPrefix = userPrefix;
    }

    /**
     * Extracts role type information from SMB (Server Message Block) response data.
     * Processes both SMB and SMB1 protocols to extract allowed and denied SIDs.
     *
     * @param responseData the response data containing SMB metadata
     * @return a list of role type strings extracted from the SMB permissions
     */
    public List<String> getSmbRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isSmbRoleFromFile()) {
            final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
            final Map<String, Object> metaDataMap = responseData.getMetaDataMap();
            if (responseData.getUrl().startsWith("smb:")) {
                final SID[] allowedSids = (SID[]) metaDataMap.get(SmbClient.SMB_ALLOWED_SID_ENTRIES);
                if (allowedSids != null) {
                    for (final SID sid : allowedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(accountId);
                        }
                    }
                }
                final SID[] deniedSids = (SID[]) metaDataMap.get(SmbClient.SMB_DENIED_SID_ENTRIES);
                if (deniedSids != null) {
                    for (final SID sid : deniedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(fessConfig.getRoleSearchDeniedPrefix() + accountId);
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smbUrl:{} roleType:{}", CredentialUrlUtil.removeUserInfo(responseData.getUrl()), roleTypeList);
                }
            } else if (responseData.getUrl().startsWith("smb1:")) {
                final org.codelibs.jcifs.smb1.SID[] allowedSids = (org.codelibs.jcifs.smb1.SID[]) metaDataMap
                        .get(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_ALLOWED_SID_ENTRIES);
                if (allowedSids != null) {
                    for (final org.codelibs.jcifs.smb1.SID sid : allowedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(accountId);
                        }
                    }
                }
                final org.codelibs.jcifs.smb1.SID[] deniedSids = (org.codelibs.jcifs.smb1.SID[]) metaDataMap
                        .get(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_DENIED_SID_ENTRIES);
                if (deniedSids != null) {
                    for (final org.codelibs.jcifs.smb1.SID sid : deniedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(fessConfig.getRoleSearchDeniedPrefix() + accountId);
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smb1Url:{} roleType:{}", CredentialUrlUtil.removeUserInfo(responseData.getUrl()), roleTypeList);
                }
            }
        }
        return roleTypeList;
    }

    /**
     * Extracts role type information from file system response data.
     * Processes ACL (Access Control List) or POSIX file attributes to extract user and group information.
     *
     * @param responseData the response data containing file system metadata
     * @return a list of role type strings extracted from the file permissions
     */
    public List<String> getFileRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isFileRoleFromFile() && responseData.getUrl().startsWith("file:")) {
            final Map<String, Object> metaDataMap = responseData.getMetaDataMap();
            final Object fileAttributeView = metaDataMap.get(FileSystemClient.FILE_ATTRIBUTE_VIEW);
            try {
                if (fileAttributeView instanceof final AclFileAttributeView aclFileAttributeView) {
                    aclFileAttributeView.getAcl().stream().forEach(acl -> {
                        // only entries that grant or deny reading the content matter (AUDIT/ALARM and other masks are ignored)
                        final AclEntryType type = acl.type();
                        if (type != AclEntryType.ALLOW && type != AclEntryType.DENY
                                || !acl.permissions().contains(AclEntryPermission.READ_DATA)) {
                            return;
                        }
                        final boolean allowed = type == AclEntryType.ALLOW;
                        final UserPrincipal principal = acl.principal();
                        if (principal == null) {
                            return;
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Principal: [{}] {}, type:{}", principal.getClass().getName(), principal, type);
                        }
                        final String role = principal instanceof GroupPrincipal ? systemHelper.getSearchRoleByGroup(principal.getName())
                                : systemHelper.getSearchRoleByUser(principal.getName());
                        roleTypeList.add(allowed ? role : fessConfig.getRoleSearchDeniedPrefix() + role);
                    });
                } else if (fileAttributeView instanceof final PosixFileAttributeView posixFileAttributeView) {
                    final PosixFileAttributes attributes = posixFileAttributeView.readAttributes();
                    final UserPrincipal userPrincipal = attributes.owner();
                    if (userPrincipal != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Principal: [{}] {}", userPrincipal.getClass().getName(), userPrincipal);
                        }
                        roleTypeList.add(systemHelper.getSearchRoleByUser(userPrincipal.getName()));
                    }
                    final GroupPrincipal groupPrincipal = attributes.group();
                    if (groupPrincipal != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Principal: [{}] {}", groupPrincipal.getClass().getName(), groupPrincipal);
                        }
                        roleTypeList.add(systemHelper.getSearchRoleByGroup(groupPrincipal.getName()));
                    }
                }
            } catch (final IOException e) {
                throw new CrawlingAccessException("Failed to access permission info", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("fileUrl:{} roleType:{}", CredentialUrlUtil.removeUserInfo(responseData.getUrl()), roleTypeList);
            }
        }
        return roleTypeList;
    }

    /**
     * Extracts role type information from FTP (File Transfer Protocol) response data.
     * Processes FTP metadata to extract file owner and group information.
     *
     * @param responseData the response data containing FTP metadata
     * @return a list of role type strings extracted from the FTP file permissions
     */
    public List<String> getFtpRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isFtpRoleFromFile() && responseData.getUrl().startsWith("ftp:")) {
            final String owner = (String) responseData.getMetaDataMap().get(FtpClient.FTP_FILE_USER);
            if (owner != null) {
                roleTypeList.add(systemHelper.getSearchRoleByUser(owner));
            }
            final String group = (String) responseData.getMetaDataMap().get(FtpClient.FTP_FILE_GROUP);
            if (group != null) {
                roleTypeList.add(systemHelper.getSearchRoleByGroup(group));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ftpUrl:{} roleType:{}", CredentialUrlUtil.removeUserInfo(responseData.getUrl()), roleTypeList);
            }
        }
        return roleTypeList;
    }

    /**
     * Sets the prefix used to identify allow permissions.
     *
     * @param allowPrefix the allow prefix to set
     */
    public void setAllowPrefix(final String allowPrefix) {
        this.allowPrefix = allowPrefix;
    }

    /**
     * Sets the prefix used to identify deny permissions.
     *
     * @param denyPrefix the deny prefix to set
     */
    public void setDenyPrefix(final String denyPrefix) {
        this.denyPrefix = denyPrefix;
    }
}
