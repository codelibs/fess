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
package org.codelibs.fess.app.web.api.admin.dict.synonym;

import static org.codelibs.fess.app.web.admin.dict.synonym.AdminDictSynonymAction.createSynonymItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.synonym.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.dict.synonym.SynonymItem;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

public class ApiAdminDictSynonymAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminDictSynonymAction.class);

    @Resource
    private SynonymService synonymService;

    // GET /api/admin/dict/synonym/settings/{dictId}
    @Execute
    public JsonResponse<ApiResult> get$settings(final String dictId, final SearchBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        final SynonymPager pager = copyBeanToNewBean(body, SynonymPager.class);
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(synonymService.getSynonymList(body.dictId, pager).stream()
                        .map(protwordsItem -> createEditBody(protwordsItem, dictId)).collect(Collectors.toList()))
                .status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/dict/synonym/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String dictId, final long id) {
        return asJson(new ApiResult.ApiConfigResponse()
                .setting(synonymService.getSynonymItem(dictId, id).map(entity -> createEditBody(entity, dictId)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/dict/synonym/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$setting(final String dictId, final CreateBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final SynonymItem entity = createSynonymItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        synonymService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(true).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/dict/synonym/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$setting(final String dictId, final EditBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final SynonymItem entity = createSynonymItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        });
        synonymService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(false).status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/dict/synonym/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String dictId, final long id) {
        synonymService.getSynonymItem(dictId, id).ifPresent(entity -> {
            synonymService.delete(dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
        });
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(id)).created(false).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/dict/synonym/upload/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$upload(final String dictId, final UploadForm form) {
        form.dictId = dictId;
        validateApi(form, messages -> {});
        final SynonymFile file = synonymService.getSynonymFile(form.dictId).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
            return null;
        });
        try (InputStream inputStream = form.synonymFile.getInputStream()) {
            file.update(inputStream);
        } catch (final IOException e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/dict/synonym/download/{dictId}
    @Execute
    public StreamResponse get$download(final String dictId, final DownloadBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        return synonymService.getSynonymFile(body.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL));
                    return null;
                });
    }

    protected EditBody createEditBody(final SynonymItem entity, final String dictId) {
        final EditBody body = new EditBody();
        body.id = entity.getId();
        body.dictId = dictId;
        body.inputs = entity.getInputsValue();
        body.outputs = entity.getOutputsValue();
        return body;
    }
}
