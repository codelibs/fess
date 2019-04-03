/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.SID;

public class PermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);

    protected String rolePrefix = "{role}";

    protected String groupPrefix = "{group}";

    protected String userPrefix = "{user}";

    @Resource
    protected SystemHelper systemHelper;

    public String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final String permission = value.trim();
        final String lower = permission.toLowerCase(Locale.ROOT);
        if (lower.startsWith(userPrefix)) {
            if (permission.length() > userPrefix.length()) {
                return systemHelper.getSearchRoleByUser(permission.substring(userPrefix.length()));
            } else {
                return null;
            }
        } else if (lower.startsWith(groupPrefix)) {
            if (permission.length() > groupPrefix.length()) {
                return systemHelper.getSearchRoleByGroup(permission.substring(groupPrefix.length()));
            } else {
                return null;
            }
        } else if (lower.startsWith(rolePrefix)) {
            if (permission.length() > rolePrefix.length()) {
                return systemHelper.getSearchRoleByRole(permission.substring(rolePrefix.length()));
            } else {
                return null;
            }
        }
        return permission;
    }

    public String decode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (value.startsWith(fessConfig.getRoleSearchUserPrefix()) && value.length() > fessConfig.getRoleSearchUserPrefix().length()) {
            return userPrefix + value.substring(fessConfig.getRoleSearchUserPrefix().length());
        } else if (value.startsWith(fessConfig.getRoleSearchGroupPrefix())
                && value.length() > fessConfig.getRoleSearchGroupPrefix().length()) {
            return groupPrefix + value.substring(fessConfig.getRoleSearchGroupPrefix().length());
        } else if (value.startsWith(fessConfig.getRoleSearchRolePrefix()) && value.length() > fessConfig.getRoleSearchRolePrefix().length()) {
            return rolePrefix + value.substring(fessConfig.getRoleSearchRolePrefix().length());
        }
        return value;
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
        if (fessConfig.isSmbRoleFromFile() && responseData.getUrl().startsWith("smb:")) {
            final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
            final SID[] sids = (SID[]) responseData.getMetaDataMap().get(SmbClient.SMB_ALLOWED_SID_ENTRIES);
            if (sids != null) {
                for (final SID sid : sids) {
                    final String accountId = sambaHelper.getAccountId(sid);
                    if (accountId != null) {
                        roleTypeList.add(accountId);
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smbUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
                }
            }
        }
        return roleTypeList;
    }

    public List<String> getFileRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isFileRoleFromFile() && responseData.getUrl().startsWith("file:")) {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final String owner = (String) responseData.getMetaDataMap().get(FileSystemClient.FS_FILE_USER);
            if (owner != null) {
                roleTypeList.add(systemHelper.getSearchRoleByUser(owner));
            }
            final String[] groups = (String[]) responseData.getMetaDataMap().get(FileSystemClient.FS_FILE_GROUPS);
            roleTypeList.addAll(stream(groups).get(stream -> stream.map(systemHelper::getSearchRoleByGroup).collect(Collectors.toList())));
            if (logger.isDebugEnabled()) {
                logger.debug("fileUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
            }
        }
        return roleTypeList;
    }

    public List<String> getFtpRoleTypeList(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isFtpRoleFromFile() && responseData.getUrl().startsWith("ftp:")) {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final String owner = (String) responseData.getMetaDataMap().get(FtpClient.FTP_FILE_USER);
            if (owner != null) {
                roleTypeList.add(systemHelper.getSearchRoleByUser(owner));
            }
            final String group = (String) responseData.getMetaDataMap().get(FtpClient.FTP_FILE_GROUP);
            if (group != null) {
                roleTypeList.add(systemHelper.getSearchRoleByGroup(group));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ftpUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
            }
        }
        return roleTypeList;
    }
}
