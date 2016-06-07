/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.upgrade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.exception.ResourceNotFoundRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.DataConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigToRoleBhv;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.es.user.exentity.Role;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUpgradeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminUpgradeAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigToRoleBhv webConfigToRoleBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigToRoleBhv fileConfigToRoleBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigToRoleBhv dataConfigToRoleBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected FessEsClient fessEsClient;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUpgrade()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminUpgrade_AdminUpgradeJsp).useForm(UpgradeForm.class);
    }

    @Execute
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, () -> {
            return asIndexHtml();
        });
        verifyToken(() -> asIndexHtml());

        if ("10.0".equals(form.targetVersion)) {
            upgradeFrom10_0();
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom10_0() {

        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String indexConfigPath = "fess_indices";
        final String configIndex = ".fess_config";
        final String userIndex = ".fess_user";
        final String docIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String docType = fessConfig.getIndexDocumentType();

        try {
            // file
            uploadResource(indexConfigPath, docIndex, "ko/seunjeon.txt");

            // alias
            createAlias(indicesClient, indexConfigPath, configIndex, ".fess_basic_config");

            // update mapping
            addFieldMapping(indicesClient, configIndex, "label_type", "permissions",
                    "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, configIndex, "web_config", "permissions",
                    "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, configIndex, "file_config", "permissions",
                    "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, configIndex, "data_config", "permissions",
                    "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "group", "gidNumber", "{\"properties\":{\"gidNumber\":{\"type\":\"long\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "employeeNumber",
                    "{\"properties\":{\"employeeNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "mail",
                    "{\"properties\":{\"mail\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "telephoneNumber",
                    "{\"properties\":{\"telephoneNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "homePhone",
                    "{\"properties\":{\"homePhone\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "homePostalAddress",
                    "{\"properties\":{\"homePostalAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "labeledURI",
                    "{\"properties\":{\"labeledURI\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "roomNumber",
                    "{\"properties\":{\"roomNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "description",
                    "{\"properties\":{\"description\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "title",
                    "{\"properties\":{\"title\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "pager",
                    "{\"properties\":{\"pager\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "street",
                    "{\"properties\":{\"street\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "postalCode",
                    "{\"properties\":{\"postalCode\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "physicalDeliveryOfficeName",
                    "{\"properties\":{\"physicalDeliveryOfficeName\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "destinationIndicator",
                    "{\"properties\":{\"destinationIndicator\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "internationaliSDNNumber",
                    "{\"properties\":{\"internationaliSDNNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "state",
                    "{\"properties\":{\"state\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "employeeType",
                    "{\"properties\":{\"employeeType\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "facsimileTelephoneNumber",
                    "{\"properties\":{\"facsimileTelephoneNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "postOfficeBox",
                    "{\"properties\":{\"postOfficeBox\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "initials",
                    "{\"properties\":{\"initials\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "carLicense",
                    "{\"properties\":{\"carLicense\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "mobile",
                    "{\"properties\":{\"mobile\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "postalAddress",
                    "{\"properties\":{\"postalAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "city",
                    "{\"properties\":{\"city\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "teletexTerminalIdentifier",
                    "{\"properties\":{\"teletexTerminalIdentifier\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "x121Address",
                    "{\"properties\":{\"x121Address\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "businessCategory",
                    "{\"properties\":{\"businessCategory\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "registeredAddress",
                    "{\"properties\":{\"registeredAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "displayName",
                    "{\"properties\":{\"displayName\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "preferredLanguage",
                    "{\"properties\":{\"preferredLanguage\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "departmentNumber",
                    "{\"properties\":{\"departmentNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "uidNumber", "{\"properties\":{\"uidNumber\":{\"type\":\"long\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "gidNumber", "{\"properties\":{\"gidNumber\":{\"type\":\"long\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "homeDirectory",
                    "{\"properties\":{\"homeDirectory\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, userIndex, "user", "groups",
                    "{\"properties\":{\"groups\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
            addFieldMapping(indicesClient, docIndex, docType, "location", "{\"properties\":{\"location\":{\"type\":\"geo_point\"}}}");

            // data migration
            final Map<String, List<String>> mapping = new HashMap<>();
            labelToRoleBhv.selectList(cb -> cb.query().addOrderBy_LabelTypeId_Asc()).forEach(e -> {
                List<String> list = mapping.get(e.getLabelTypeId());
                if (list == null) {
                    list = new ArrayList<>();
                    mapping.put(e.getLabelTypeId(), list);
                }
                list.add(e.getRoleTypeId());
            });
            mapping.entrySet().forEach(
                    e -> {
                        final String labelTypeId = e.getKey();
                        final List<String> idList = e.getValue();
                        labelTypeBhv.selectEntity(cb -> cb.acceptPK(labelTypeId)).ifPresent(
                                entity -> {
                                    final String[] permissions =
                                            roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                    .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue())
                                                    .toArray(n -> new String[n]);
                                    entity.setPermissions(permissions);
                                    labelTypeBhv.insertOrUpdate(entity);
                                    labelToRoleBhv.queryDelete(cb -> cb.query().setLabelTypeId_Equal(labelTypeId));
                                });
                    });

            mapping.clear();
            webConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_WebConfigId_Asc()).forEach(e -> {
                final String webConfigId = e.getWebConfigId();
                List<String> list = mapping.get(webConfigId);
                if (list == null) {
                    list = new ArrayList<>();
                    mapping.put(webConfigId, list);
                }
                list.add(e.getRoleTypeId());
            });
            mapping.entrySet().forEach(
                    e -> {
                        final String webConfigTypeId = e.getKey();
                        final List<String> idList = e.getValue();
                        webConfigBhv.selectEntity(cb -> cb.acceptPK(webConfigTypeId)).ifPresent(
                                entity -> {
                                    final String[] permissions =
                                            roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                    .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue())
                                                    .toArray(n -> new String[n]);
                                    entity.setPermissions(permissions);
                                    webConfigBhv.insertOrUpdate(entity);
                                    webConfigToRoleBhv.queryDelete(cb -> cb.query().setWebConfigId_Equal(webConfigTypeId));
                                });
                    });

            mapping.clear();
            fileConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_FileConfigId_Asc()).forEach(e -> {
                final String fileConfigId = e.getFileConfigId();
                List<String> list = mapping.get(fileConfigId);
                if (list == null) {
                    list = new ArrayList<>();
                    mapping.put(fileConfigId, list);
                }
                list.add(e.getRoleTypeId());
            });
            mapping.entrySet().forEach(
                    e -> {
                        final String fileConfigTypeId = e.getKey();
                        final List<String> idList = e.getValue();
                        fileConfigBhv.selectEntity(cb -> cb.acceptPK(fileConfigTypeId)).ifPresent(
                                entity -> {
                                    final String[] permissions =
                                            roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                    .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue())
                                                    .toArray(n -> new String[n]);
                                    entity.setPermissions(permissions);
                                    fileConfigBhv.insertOrUpdate(entity);
                                    fileConfigToRoleBhv.queryDelete(cb -> cb.query().setFileConfigId_Equal(fileConfigTypeId));
                                });
                    });

            mapping.clear();
            dataConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_DataConfigId_Asc()).forEach(e -> {
                final String dataConfigId = e.getDataConfigId();
                List<String> list = mapping.get(dataConfigId);
                if (list == null) {
                    list = new ArrayList<>();
                    mapping.put(dataConfigId, list);
                }
                list.add(e.getRoleTypeId());
            });
            mapping.entrySet().forEach(
                    e -> {
                        final String dataConfigTypeId = e.getKey();
                        final List<String> idList = e.getValue();
                        dataConfigBhv.selectEntity(cb -> cb.acceptPK(dataConfigTypeId)).ifPresent(
                                entity -> {
                                    final String[] permissions =
                                            roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                    .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue())
                                                    .toArray(n -> new String[n]);
                                    entity.setPermissions(permissions);
                                    dataConfigBhv.insertOrUpdate(entity);
                                    dataConfigToRoleBhv.queryDelete(cb -> cb.query().setDataConfigId_Equal(dataConfigTypeId));
                                });
                    });

            roleTypeBhv.queryDelete(cb -> {});

            roleBhv.selectEntity(cb -> cb.query().setName_Equal("guest")).orElseGet(() -> {
                Role entity = new Role();
                entity.setName("guest");
                roleBhv.insert(entity);
                return entity;
            });

            saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to upgrade data.", e);
            saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, "10.0", e.getLocalizedMessage()));
        }
    }

    private void uploadResource(final String indexConfigPath, final String indexName, final String path) {
        final String filePath = indexConfigPath + "/" + indexName + "/" + path;
        try {
            final String source = FileUtil.readUTF8(filePath);
            try (CurlResponse response =
                    Curl.post(org.codelibs.fess.util.ResourceUtil.getElasticsearchHttpUrl() + "/_configsync/file").param("path", path)
                            .body(source).execute()) {
                if (response.getHttpStatusCode() == 200) {
                    logger.info("Register " + path + " to " + indexName);
                } else {
                    logger.warn("Invalid request for " + path);
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to register " + filePath, e);
        }
    }

    private void createAlias(final IndicesAdminClient indicesClient, final String indexConfigPath, final String indexName,
            final String aliasName) {
        final String aliasConfigPath = indexConfigPath + "/" + indexName + "/alias/" + aliasName + ".json";
        try {
            final File aliasConfigFile = ResourceUtil.getResourceAsFile(aliasConfigPath);
            if (aliasConfigFile.exists()) {
                final String source = FileUtil.readUTF8(aliasConfigFile);
                final IndicesAliasesResponse response =
                        indicesClient.prepareAliases().addAlias(indexName, aliasName, source).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (response.isAcknowledged()) {
                    logger.info("Created " + aliasName + " alias for " + indexName);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create " + aliasName + " alias for " + indexName);
                }
            }
        } catch (final ResourceNotFoundRuntimeException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn(aliasConfigPath + " is not found.", e);
        }
    }

    private void addFieldMapping(final IndicesAdminClient indicesClient, final String index, final String type, final String field,
            final String source) {
        final GetFieldMappingsResponse gfmResponse =
                indicesClient.prepareGetFieldMappings(index).addTypes(type).setFields(field).execute().actionGet();
        if (gfmResponse.fieldMappings(index, type, field).isNull()) {
            try {
                final PutMappingResponse pmResponse =
                        indicesClient.preparePutMapping(index).setType(type).setSource(source).execute().actionGet();
                if (!pmResponse.isAcknowledged()) {
                    logger.warn("Failed to add " + field + " to " + index + "/" + type);
                }
            } catch (Exception e) {
                logger.warn("Failed to add " + field + " to " + index + "/" + type, e);
            }
        }
    }

}