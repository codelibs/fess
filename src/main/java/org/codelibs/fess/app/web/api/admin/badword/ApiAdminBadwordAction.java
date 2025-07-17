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
package org.codelibs.fess.app.web.api.admin.badword;

import static org.codelibs.fess.app.web.admin.badword.AdminBadwordAction.getBadWord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.fess.app.pager.BadWordPager;
import org.codelibs.fess.app.service.BadWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.badword.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin bad word management.
 * Provides REST endpoints for managing bad words in the Fess search engine.
 */
public class ApiAdminBadwordAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminBadwordAction() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminBadwordAction.class);

    @Resource
    private BadWordService badWordService;

    /** Helper for managing search suggestions and bad words */
    @Resource
    protected SuggestHelper suggestHelper;

    /**
     * Retrieves bad word settings with pagination support.
     *
     * @param body the search body containing pagination and filter parameters
     * @return JSON response containing list of bad word configurations
     */
    // GET /api/admin/badword/settings
    // PUT /api/admin/badword/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final BadWordPager pager = copyBeanToNewBean(body, BadWordPager.class);
        final List<BadWord> list = badWordService.getBadWordList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific bad word setting by ID.
     *
     * @param id the ID of the bad word to retrieve
     * @return JSON response containing the bad word configuration
     */
    // GET /api/admin/badword/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {

        final BadWord entity = badWordService.getBadWord(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });

        final EditBody body = createEditBody(entity);
        return asJson(new ApiResult.ApiConfigResponse().setting(body).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/badword/setting
    /**
     * Creates a new bad word setting.
     *
     * @param body the request body containing bad word information
     * @return JSON response with result status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final BadWord entity = getBadWord(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            badWordService.store(entity);
            suggestHelper.addBadWord(entity.getSuggestWord(), false);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/user/setting
    /**
     * Updates an existing bad word setting.
     *
     * @param body the request body containing updated bad word information
     * @return JSON response with result status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final BadWord badWord = getBadWord(body).map(entity -> {
            try {
                badWordService.store(entity);
                suggestHelper.storeAllBadWords(false);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(badWord.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a bad word setting by ID.
     *
     * @param id the ID of the bad word to delete
     * @return JSON response indicating the deletion status
     */
    // DELETE /api/admin/badword/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        try {
            badWordService.getBadWord(id).ifPresent(entity -> {
                try {
                    badWordService.delete(entity);
                    suggestHelper.deleteBadWord(entity.getSuggestWord());
                    saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                } catch (final Exception e) {
                    logger.warn("Failed to process a request.", e);
                    throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
                }
            }).orElse(() -> {
                throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            });
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(false).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/badword/upload
    /**
     * Uploads bad words from a CSV file.
     *
     * @param body the upload form containing the CSV file
     * @return JSON response with result status
     */
    @Execute
    public JsonResponse<ApiResult> put$upload(final UploadForm body) {
        validateApi(body, messages -> {});
        CommonPoolUtil.execute(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(body.badWordFile.getInputStream(), getCsvEncoding()))) {
                badWordService.importCsv(reader);
                suggestHelper.storeAllBadWords(false);
            } catch (final Exception e) {
                throw new FessSystemException("Failed to import data.", e);
            }
        });
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    /**
     * Downloads bad word settings as a CSV file.
     *
     * @param body the download request body containing download parameters
     * @return stream response containing the CSV file data
     */
    // GET /api/admin/badword/download
    @Execute
    public StreamResponse get$download(final DownloadBody body) {
        validateApi(body, messages -> {});
        return asStream("badword.csv").contentTypeOctetStream().stream(out -> {
            final Path tempFile = ComponentUtil.getSystemHelper().createTempFile("fess-badword-", ".csv").toPath();
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    badWordService.exportCsv(writer);
                } catch (final Exception e) {
                    logger.warn("Failed to process a request.", e);
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadBadwordFile(GLOBAL));
                }
                try (InputStream in = Files.newInputStream(tempFile)) {
                    out.write(in);
                }
            } finally {
                Files.delete(tempFile);
            }
        });
    }

    /**
     * Creates an EditBody from a BadWord entity for API responses.
     *
     * @param entity the BadWord entity to convert
     * @return the converted EditBody object
     */
    protected EditBody createEditBody(final BadWord entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }

    private String getCsvEncoding() {
        return fessConfig.getCsvFileEncoding();
    }

}
