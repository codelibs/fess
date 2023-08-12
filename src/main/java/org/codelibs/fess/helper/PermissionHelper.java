/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jcifs.SID;

public class PermissionHelper {
    private static final Logger logger = LogManager.getLogger(PermissionHelper.class);

    protected String rolePrefix = "{role}";

    protected String groupPrefix = "{group}";

    protected String userPrefix = "{user}";

    protected String allowPrefix = "(allow)";

    protected String denyPrefix = "(deny)";

    @Resource
    protected SystemHelper systemHelper;

    public String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        String permission = value.trim();
        String lower = permission.toLowerCase(Locale.ROOT);
        final String aclPrefix;
        if (lower.startsWith(allowPrefix)) {
            lower = lower.substring(allowPrefix.length());
            permission = permission.substring(allowPrefix.length());
            aclPrefix = StringUtil.EMPTY;
        } else if (lower.startsWith(denyPrefix)) {
            lower = lower.substring(denyPrefix.length());
            permission = permission.substring(denyPrefix.length());
            aclPrefix = ComponentUtil.getFessConfig().getRoleSearchDeniedPrefix();
        } else {
            aclPrefix = StringUtil.EMPTY;
        }
        if (StringUtil.isBlank(permission)) {
            return null;
        }
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
        return permission;
    }

    public String decode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String aclPrefix;
        final String permission;
        final String deniedPrefix = fessConfig.getRoleSearchDeniedPrefix();
        if (value.startsWith(deniedPrefix)) {
            permission = value.substring(deniedPrefix.length());
            aclPrefix = denyPrefix;
        } else {
            permission = value;
            aclPrefix = StringUtil.EMPTY;
        }
        if (StringUtil.isBlank(permission)) {
            return null;
        }
        if (permission.startsWith(fessConfig.getRoleSearchUserPrefix())
                && permission.length() > fessConfig.getRoleSearchUserPrefix().length()) {
            return aclPrefix + userPrefix + permission.substring(fessConfig.getRoleSearchUserPrefix().length());
        }
        if (permission.startsWith(fessConfig.getRoleSearchGroupPrefix())
                && permission.length() > fessConfig.getRoleSearchGroupPrefix().length()) {
            return aclPrefix + groupPrefix + permission.substring(fessConfig.getRoleSearchGroupPrefix().length());
        }
        if (permission.startsWith(fessConfig.getRoleSearchRolePrefix())
                && permission.length() > fessConfig.getRoleSearchRolePrefix().length()) {
            return aclPrefix + rolePrefix + permission.substring(fessConfig.getRoleSearchRolePrefix().length());
        }
        return permission;
    }

    public void setRolePrefix(final String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void setGroupPrefix(final String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public void setUserPrefix(final String userPrefix) {
        this.userPrefix = userPrefix;
    }

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
                    logger.debug("smbUrl:{} roleType:{}", responseData.getUrl(), roleTypeList);
                }
            } else if (responseData.getUrl().startsWith("smb1:")) {
                final jcifs.smb1.smb1.SID[] allowedSids =
                        (jcifs.smb1.smb1.SID[]) metaDataMap.get(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_ALLOWED_SID_ENTRIES);
                if (allowedSids != null) {
                    for (final jcifs.smb1.smb1.SID sid : allowedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(accountId);
                        }
                    }
                }
                final jcifs.smb1.smb1.SID[] deniedSids =
                        (jcifs.smb1.smb1.SID[]) metaDataMap.get(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_DENIED_SID_ENTRIES);
                if (deniedSids != null) {
                    for (final jcifs.smb1.smb1.SID sid : deniedSids) {
                        final String accountId = sambaHelper.getAccountId(sid);
                        if (accountId != null) {
                            roleTypeList.add(fessConfig.getRoleSearchDeniedPrefix() + accountId);
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smb1Url:{} roleType:{}", responseData.getUrl(), roleTypeList);
                }
            }
        }
        return roleTypeList;
    }

    public List<String> getFileRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isFileRoleFromFile() && responseData.getUrl().startsWith("file:")) {
            final Map<String, Object> metaDataMap = responseData.getMetaDataMap();
            final Object fileAttributeView = metaDataMap.get(FileSystemClient.FILE_ATTRIBUTE_VIEW);
            try {
                if (fileAttributeView instanceof final AclFileAttributeView aclFileAttributeView) {
                    aclFileAttributeView.getAcl().stream().forEach(acl -> {
                        final UserPrincipal principal = acl.principal();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Principal: [{}] {}", principal.getClass().getName(), principal);
                        }
                        if (principal instanceof final GroupPrincipal groupPrincipal) {
                            roleTypeList.add(systemHelper.getSearchRoleByGroup(groupPrincipal.getName()));
                        } else if (principal != null) {
                            roleTypeList.add(systemHelper.getSearchRoleByUser(principal.getName()));
                        }
                    });
                } else if (fileAttributeView instanceof final PosixFileAttributeView posixFileAttributeView) {
                    final PosixFileAttributes attributes = posixFileAttributeView.readAttributes();
                    final UserPrincipal userPrincipal = attributes.owner();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Principal: [{}] {}", userPrincipal.getClass().getName(), userPrincipal);
                    }
                    if (userPrincipal != null) {
                        roleTypeList.add(systemHelper.getSearchRoleByUser(userPrincipal.getName()));
                    }
                    final GroupPrincipal groupPrincipal = attributes.group();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Principal: [{}] {}", groupPrincipal.getClass().getName(), groupPrincipal);
                    }
                    if (groupPrincipal != null) {
                        roleTypeList.add(systemHelper.getSearchRoleByGroup(groupPrincipal.getName()));
                    }
                }
            } catch (final IOException e) {
                throw new CrawlingAccessException("Failed to access permission info", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("fileUrl:{} roleType:{}", responseData.getUrl(), roleTypeList);
            }
        }
        return roleTypeList;
    }

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
                logger.debug("ftpUrl:{} roleType:{}", responseData.getUrl(), roleTypeList);
            }
        }
        return roleTypeList;
    }

    public void setAllowPrefix(final String allowPrefix) {
        this.allowPrefix = allowPrefix;
    }

    public void setDenyPrefix(final String denyPrefix) {
        this.denyPrefix = denyPrefix;
    }
}
