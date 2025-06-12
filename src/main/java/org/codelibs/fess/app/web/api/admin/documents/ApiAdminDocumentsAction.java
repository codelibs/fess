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
package org.codelibs.fess.app.web.api.admin.documents;

import java.util.Arrays;
import java.util.Date;
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
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
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

    // PUT /api/admin/documents/bulk
    @Execute
    public JsonResponse<ApiResult> put$bulk(final BulkBody body) {
        validateApi(body, messages -> {});
        if (body.documents == null) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "documents is required."));
        }
        if (body.documents.isEmpty()) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "documents is empty."));
        }
        final String indexFieldId = fessConfig.getIndexFieldId();
        final String indexFieldDocId = fessConfig.getIndexFieldDocId();
        final String indexFieldContentLength = fessConfig.getIndexFieldContentLength();
        final String indexFieldTitle = fessConfig.getIndexFieldTitle();
        final String indexFieldContent = fessConfig.getIndexFieldContent();
        final String indexFieldFavoriteCount = fessConfig.getIndexFieldFavoriteCount();
        final String indexFieldClickCount = fessConfig.getIndexFieldClickCount();
        final String indexFieldBoost = fessConfig.getIndexFieldBoost();
        final String indexFieldRole = fessConfig.getIndexFieldRole();
        final String indexFieldLastModified = fessConfig.getIndexFieldLastModified();
        final String indexFieldTimestamp = fessConfig.getIndexFieldTimestamp();
        final String indexFieldLang = fessConfig.getIndexFieldLang();
        final List<String> guestRoleList = fessConfig.getSearchGuestRoleList();
        final Date now = systemHelper.getCurrentTime();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final LanguageHelper languageHelper = ComponentUtil.getLanguageHelper();
        final List<Map<String, Object>> docList = body.documents.stream().map(doc -> {
            if (!doc.containsKey(indexFieldContentLength)) {
                long contentLength = 0;
                if (doc.get(indexFieldTitle) instanceof final String title) {
                    contentLength += title.length();
                }
                if (doc.get(indexFieldContent) instanceof final String content) {
                    contentLength += content.length();
                }
                doc.put(indexFieldContentLength, contentLength);
            }
            if (!doc.containsKey(indexFieldFavoriteCount)) {
                doc.put(indexFieldFavoriteCount, 0L);
            }
            if (!doc.containsKey(indexFieldClickCount)) {
                doc.put(indexFieldClickCount, 0L);
            }
            if (!doc.containsKey(indexFieldBoost)) {
                doc.put(indexFieldBoost, 1.0f);
            }
            if (!doc.containsKey(indexFieldRole)) {
                doc.put(indexFieldRole, guestRoleList);
            }
            if (!doc.containsKey(indexFieldLastModified)) {
                doc.put(indexFieldLastModified, now);
            }
            if (!doc.containsKey(indexFieldTimestamp)) {
                doc.put(indexFieldTimestamp, now);
            }
            AdminSearchlistAction.validateFields(doc, this::throwValidationErrorApi);
            final Map<String, Object> newDoc = fessConfig.convertToStorableDoc(doc);
            newDoc.put(indexFieldId, crawlingInfoHelper.generateId(newDoc));
            newDoc.put(indexFieldDocId, systemHelper.generateDocId(newDoc));
            if (newDoc.get(indexFieldLang) instanceof final List<?> langList) {
                if (langList.contains("auto")) {
                    newDoc.remove(indexFieldLang);
                }
                languageHelper.updateDocument(newDoc);
            }
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
            itemMap.put("result", item.status().name());
            if (item.isFailed()) {
                itemMap.put("message", item.getFailureMessage());
            } else {
                itemMap.put("id", item.getId());
            }
            return itemMap;
        }).toList()).status(response.hasFailures() ? Status.FAILED : Status.OK).result());

    }

}
