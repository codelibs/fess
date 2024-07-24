/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.documents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.web.admin.searchlist.AdminSearchlistAction;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiBulkResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.app.web.api.admin.searchlist.ApiAdminSearchlistAction;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.opensearch.action.bulk.BulkResponse;

import jakarta.annotation.Resource;

public class ApiAdminDocumentsAction extends FessApiAdminAction {
    // ===================================================================================
    // Constant
    //
    private static final Logger logger = LogManager.getLogger(ApiAdminSearchlistAction.class);

    // ===================================================================================
    // Attribute
    // =========
    @Resource
    protected SearchEngineClient searchEngineClient;

    // ===================================================================================
    // Search Execute
    //

    // POST /api/admin/documents/bulk
    @Execute
    public JsonResponse<ApiResult> post$bulk(final BulkBody body) {
        validateApi(body, messages -> {});
        if (body.documents == null) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "documents is required."));
        }
        if (body.documents.isEmpty()) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "documents is empty."));
        }
        final String indexFieldId = fessConfig.getIndexFieldId();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final List<Map<String, Object>> docList = body.documents.stream().map(doc -> {
            AdminSearchlistAction.validateFields(doc, this::throwValidationErrorApi);
            final Map<String, Object> newDoc = fessConfig.convertToStorableDoc(doc);
            final String newId = crawlingInfoHelper.generateId(newDoc);
            newDoc.put(indexFieldId, newId);
            return newDoc;
        }).toList();
        if (fessConfig.isThumbnailCrawlerEnabled()) {
            final ThumbnailManager thumbnailManager = ComponentUtil.getThumbnailManager();
            final String thumbnailField = fessConfig.getIndexFieldThumbnail();
            docList.stream().forEach(doc -> {
                if (!thumbnailManager.offer(doc)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing {}={} from doc[{}]", thumbnailField, doc.get(thumbnailField),
                                doc.get(fessConfig.getIndexFieldUrl()));
                    }
                    doc.remove(thumbnailField);
                }
            });
        }

        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final BulkResponse response = searchEngineClient.addAll(fessConfig.getIndexDocumentUpdateIndex(), docList, (doc, builder) -> {
            if (doc.get(fessConfig.getIndexFieldConfigId()) instanceof final String configId) {
                crawlingConfigHelper.getPipeline(configId).ifPresent(s -> builder.setPipeline(s));
            }
        });
        return asJson(new ApiBulkResponse().items(Arrays.stream(response.getItems()).map(item -> {
            final Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("status", item.status().name());
            if (item.isFailed()) {
                itemMap.put("message", item.getFailureMessage());
            } else {
                itemMap.put("id", item.getId());
            }
            return itemMap;
        }).toList()).status(response.hasFailures() ? Status.FAILED : Status.OK).result());

    }

}
