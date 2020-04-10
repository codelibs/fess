/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import java.io.File;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.ResourceNotFoundRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse.FieldMappingMetaData;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;

public final class UpgradeUtil {
    private static final Logger logger = LogManager.getLogger(UpgradeUtil.class);

    private UpgradeUtil() {
    }

    public static boolean uploadResource(final String indexConfigPath, final String indexName, final String path) {
        final String filePath = indexConfigPath + "/" + indexName + "/" + path;
        try {
            final String source = FileUtil.readUTF8(filePath);
            try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_configsync/file").param("path", path).body(source).execute()) {
                if (response.getHttpStatusCode() == 200) {
                    logger.info("Register {} to {}", path, indexName);
                    return true;
                } else {
                    logger.warn("Invalid request for {}", path);
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to register " + filePath, e);
        }
        return false;
    }

    public static boolean createAlias(final IndicesAdminClient indicesClient, final String indexConfigPath, final String indexName,
            final String aliasName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String aliasConfigPath = indexConfigPath + "/" + indexName + "/alias/" + aliasName + ".json";
        try {
            final File aliasConfigFile = org.codelibs.core.io.ResourceUtil.getResourceAsFile(aliasConfigPath);
            if (aliasConfigFile.exists()) {
                final String source = FileUtil.readUTF8(aliasConfigFile);
                final AcknowledgedResponse response =
                        indicesClient.prepareAliases().addAlias(indexName, aliasName, source).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (response.isAcknowledged()) {
                    logger.info("Created {} alias for {}", aliasName, indexName);
                    return true;
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create {} alias for {}", aliasName, indexName);
                }
            }
        } catch (final ResourceNotFoundRuntimeException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn(aliasConfigPath + " is not found.", e);
        }
        return false;
    }

    public static boolean addMapping(final IndicesAdminClient indicesClient, final String index, final String type,
            final String indexResourcePath) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetMappingsResponse getMappingsResponse =
                indicesClient.prepareGetMappings(index).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(index);
        if (indexMappings == null || !indexMappings.containsKey(type)) {
            String source = null;
            final String mappingFile = indexResourcePath + "/" + type + ".json";
            try {
                source = FileUtil.readUTF8(mappingFile);
            } catch (final Exception e) {
                logger.warn(mappingFile + " is not found.", e);
            }
            try {
                final AcknowledgedResponse putMappingResponse =
                        indicesClient.preparePutMapping(index).setSource(source, XContentType.JSON).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created {}/{} mapping.", index, type);
                    return true;
                } else {
                    logger.warn("Failed to create {}/{} mapping.", index, type);
                }
                // TODO bulk
            } catch (final Exception e) {
                logger.warn("Failed to create " + index + "/" + type + " mapping.", e);
            }
        }
        return false;
    }

    public static boolean addFieldMapping(final IndicesAdminClient indicesClient, final String index, final String type,
            final String field, final String source) {
        final GetFieldMappingsResponse gfmResponse =
                indicesClient.prepareGetFieldMappings(index).addTypes(type).setFields(field).execute().actionGet();
        final FieldMappingMetaData fieldMappings = gfmResponse.fieldMappings(index, type, field);
        if (fieldMappings == null || fieldMappings.isNull()) {
            try {
                final AcknowledgedResponse pmResponse =
                        indicesClient.preparePutMapping(index).setSource(source, XContentType.JSON).execute().actionGet();
                if (!pmResponse.isAcknowledged()) {
                    logger.warn("Failed to add {} to {}/{}", field, index, type);
                } else {
                    return true;
                }
            } catch (final Exception e) {
                logger.warn("Failed to add " + field + " to " + index + "/" + type, e);
            }
        }
        return false;
    }

    public static boolean putMapping(final IndicesAdminClient indicesClient, final String index, final String source) {
        return putMapping(indicesClient, index, null, source);
    }

    public static boolean putMapping(final IndicesAdminClient indicesClient, final String index, final String type, final String source) {
        try {
            final PutMappingRequestBuilder builder = indicesClient.preparePutMapping(index).setSource(source, XContentType.JSON);
            final AcknowledgedResponse pmResponse = builder.execute().actionGet();
            if (!pmResponse.isAcknowledged()) {
                logger.warn("Failed to update {} settings.", index);
            } else {
                return true;
            }
        } catch (final Exception e) {
            logger.warn("Failed to update " + index + " settings.", e);
        }

        return false;
    }

    public static boolean addData(final Client fessEsClient, final String index, final String id, final String source) {
        try {
            final IndexRequest indexRequest = new IndexRequest(index).id(id).source(source, XContentType.JSON);
            fessEsClient.index(indexRequest).actionGet();
            return true;
        } catch (final Exception e) {
            logger.warn("Failed to add " + id + " to " + index, e);
        }
        return false;
    }

    public static boolean existsIndex(final IndicesAdminClient indicesClient, final String index, final boolean expandWildcardsOpen,
            final boolean expandWildcardsClosed) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            final IndicesExistsResponse response =
                    indicesClient.prepareExists(index).setExpandWildcardsClosed(expandWildcardsClosed)
                            .setExpandWildcardsOpen(expandWildcardsOpen).execute().actionGet(fessConfig.getIndexSearchTimeout());
            return response.isExists();
        } catch (final Exception e) {
            // ignore
        }
        return false;
    }

    public static void deleteIndex(final IndicesAdminClient indicesClient, final String index, final Consumer<AcknowledgedResponse> comsumer) {
        indicesClient.prepareDelete(index).execute(new ActionListener<AcknowledgedResponse>() {

            @Override
            public void onResponse(final AcknowledgedResponse response) {
                logger.info("Deleted {} index.", index);
                comsumer.accept(response);
            }

            @Override
            public void onFailure(final Exception e) {
                logger.warn("Failed to delete " + index + " index.", e);
            }
        });
    }
}
