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
package org.codelibs.fess.app.web.api.admin.elevateword;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.elevateword.AdminElevatewordAction.getElevateWord;

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
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ElevateWordPager;
import org.codelibs.fess.app.service.ElevateWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.elevateword.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.opensearch.config.exentity.ElevateWord;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin elevate word management.
 * Provides RESTful API endpoints for managing elevate word settings in the Fess search engine.
 * Elevate words boost specific search terms to appear higher in search results.
 */
public class ApiAdminElevatewordAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminElevatewordAction.class);

    // ===================================================================================
    //                                                                           Constructor
    //                                                                           ===========

    /**
     * Default constructor.
     */
    public ApiAdminElevatewordAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** Service for managing elevate word configurations */
    @Resource
    private ElevateWordService elevateWordService;

    /** Helper for managing search suggestions and elevate words */
    @Resource
    protected SuggestHelper suggestHelper;

    // GET /api/admin/elevateword
    // PUT /api/admin/elevateword
    /**
     * Returns list of elevate word settings.
     * Supports both GET and PUT requests for retrieving paginated elevate word configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing elevate word settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final ElevateWordPager pager = copyBeanToNewBean(body, ElevateWordPager.class);
        final List<ElevateWord> list = elevateWordService.getElevateWordList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/elevateword/{id}
    /**
     * Retrieves a specific elevate word setting by ID.
     *
     * @param id the ID of the elevate word to retrieve
     * @return JSON response containing the elevate word configuration
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {

        final ElevateWord entity = elevateWordService.getElevateWord(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });

        final EditBody body = createEditBody(entity);
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        body.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
        return asJson(new ApiResult.ApiConfigResponse().setting(body).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/elevateword/setting
    /**
     * Creates a new elevate word setting.
     * Also adds the elevate word to the suggest helper for search enhancement.
     *
     * @param body elevate word setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final ElevateWord entity = getElevateWord(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            elevateWordService.store(entity);
            suggestHelper.addElevateWord(entity.getSuggestWord(), entity.getReading(), entity.getLabelTypeValues(), entity.getPermissions(),
                    entity.getBoost(), false);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/elevateword/setting
    /**
     * Updates an existing elevate word setting.
     * Refreshes all elevate words in the suggest helper to maintain consistency.
     *
     * @param body elevate word setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final ElevateWord elevateWord = getElevateWord(body).map(entity -> {
            try {
                elevateWordService.store(entity);
                suggestHelper.deleteAllElevateWord(false);
                suggestHelper.storeAllElevateWords(false);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(elevateWord.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/elevateword/setting/{id}
    /**
     * Deletes a specific elevate word setting.
     * Also removes the elevate word from the suggest helper.
     *
     * @param id the elevate word setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        try {
            elevateWordService.getElevateWord(id).ifPresent(entity -> {
                try {
                    elevateWordService.delete(entity);
                    suggestHelper.deleteElevateWord(entity.getSuggestWord(), false);
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

    // PUT /api/admin/elevateword/upload
    /**
     * Uploads and imports elevate words from a CSV file.
     * Processes the file asynchronously and updates the suggest helper.
     *
     * @param body upload form containing the CSV file
     * @return JSON response with upload status
     */
    @Execute
    public JsonResponse<ApiResult> put$upload(final UploadForm body) {
        validateApi(body, messages -> {});
        CommonPoolUtil.execute(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(body.elevateWordFile.getInputStream(), getCsvEncoding()))) {
                elevateWordService.importCsv(reader);
                suggestHelper.storeAllElevateWords(false);
            } catch (final Exception e) {
                throw new FessSystemException("Failed to import data.", e);
            }
        });
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/elevateword/download
    /**
     * Downloads all elevate words as a CSV file.
     * Creates a temporary file with the exported data for download.
     *
     * @param body download parameters
     * @return stream response containing the CSV file
     */
    @Execute
    public StreamResponse get$download(final DownloadBody body) {
        validateApi(body, messages -> {});
        return asStream("elevate.csv").contentTypeOctetStream().stream(out -> {
            final Path tempFile = ComponentUtil.getSystemHelper().createTempFile("fess-elevate-", ".csv").toPath();
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    elevateWordService.exportCsv(writer);
                } catch (final Exception e) {
                    logger.warn("Failed to process a request.", e);
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadElevateFile(GLOBAL));
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
     * Creates an edit body from an elevate word entity for API responses.
     * Processes permissions and converts them to a readable format.
     *
     * @param entity the elevate word entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final ElevateWord entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
            copyOp.exclude(Constants.PERMISSIONS);
        });
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        body.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
        return body;
    }

    /**
     * Gets the CSV file encoding from configuration.
     *
     * @return the CSV file encoding string
     */
    private String getCsvEncoding() {
        return fessConfig.getCsvFileEncoding();
    }

}
