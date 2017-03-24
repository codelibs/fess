/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.BadWordPager;
import org.codelibs.fess.app.service.BadWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.badword.CreateForm;
import org.codelibs.fess.app.web.admin.badword.EditForm;
import org.codelibs.fess.app.web.admin.badword.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.BadWord;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SuggestHelper;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ApiAdminBadwordAction extends FessApiAdminAction {

    @Resource
    private BadWordService badWordService;

    @Resource
    protected SuggestHelper suggestHelper;

    // GET /api/admin/badword
    // POST /api/admin/badword
    @Execute
    public JsonResponse<ApiResult> settings(SearchBody body) {
        validateApi(body, messages -> {});
        final BadWordPager pager = new BadWordPager();
        pager.setPageSize(body.size);
        pager.setCurrentPageNumber(body.page);
        final List<BadWord> list = badWordService.getBadWordList(pager);
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(list.stream().map(entity -> createEditBody(entity)).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/badword/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(EditBody body) {
        validateApi(body, messages -> {});
        final BadWord entity = getBadWord(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            badWordService.store(entity);
            suggestHelper.addBadWord(entity.getSuggestWord());
        } catch (final Exception e) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/badword/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(EditBody body) {
        validateApi(body, messages -> {});
        final BadWord entity = getBadWord(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            badWordService.store(entity);
            suggestHelper.storeAllBadWords();
        } catch (final Exception e) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/badword/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(String id) {
        try {
            badWordService.getBadWord(id).ifPresent(entity -> {
                try {
                    badWordService.delete(entity);
                    suggestHelper.deleteBadWord(entity.getSuggestWord());
                    saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                } catch (final Exception e) {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
                }
            }).orElse(() -> {
                throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            });
        } catch (final Exception e) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(true).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/badword/upload
    @Execute
    public JsonResponse<ApiResult> post$upload(UploadForm form) {
        validateApi(form, messages -> {});
        new Thread(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(form.badWordFile.getInputStream(), getCsvEncoding()))) {
                badWordService.importCsv(reader);
                suggestHelper.storeAllBadWords();
            } catch (final Exception e) {
                throw new FessSystemException("Failed to import data.", e);
            }
        }).start();
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/badword/download
    @Execute
    public StreamResponse get$download(DownloadBody body) {
        validateApi(body, messages -> {});
        return asStream("badword.csv").contentTypeOctetStream().stream(out -> {
            final Path tempFile = Files.createTempFile(null, null);
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    badWordService.exportCsv(writer);
                } catch (final Exception e) {
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

    protected EditBody createEditBody(BadWord entity) {
        EditBody body = new EditBody();
        body.id = entity.getId();
        body.versionNo = entity.getVersionNo();
        body.createdBy = entity.getCreatedBy();
        body.createdTime = entity.getCreatedTime();
        body.suggestWord = entity.getSuggestWord();
        body.updatedBy = entity.getUpdatedBy();
        body.updatedTime = entity.getUpdatedTime();
        return body;
    }

    protected OptionalEntity<BadWord> getBadWord(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    private OptionalEntity<BadWord> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new BadWord()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return badWordService.getBadWord(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    private String getCsvEncoding() {
        return fessConfig.getCsvFileEncoding();
    }

}
