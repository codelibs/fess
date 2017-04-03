package org.codelibs.fess.app.web.api.admin.dict.seunjeon;

import org.codelibs.fess.app.pager.SeunjeonPager;
import org.codelibs.fess.app.service.SeunjeonService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.seunjeon.UploadForm;
import org.codelibs.fess.app.web.admin.dict.seunjeon.AdminDictSeunjeonAction;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.seunjeon.SeunjeonFile;
import org.codelibs.fess.dict.seunjeon.SeunjeonItem;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import javax.annotation.Resource;

import java.io.*;
import java.util.stream.Collectors;

public class ApiAdminDictSeunjeonAction extends FessApiAdminAction {

    @Resource
    private SeunjeonService seunjeonService;

    // GET /api/admin/dict/seunjeon/settings/{dictId}
    @Execute
    public JsonResponse<ApiResult> get$settings(final String dictId, final SearchBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        SeunjeonPager pager = new SeunjeonPager();
        if (body.pageNumber != null) {
            pager.setCurrentPageNumber(body.pageNumber);
        }
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(
                        seunjeonService.getSeunjeonList(body.dictId, pager).stream()
                                .map(protwordsItem -> createEditBody(protwordsItem, dictId)).collect(Collectors.toList()))
                .status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/dict/seunjeon/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String dictId, final long id) {
        return asJson(new ApiResult.ApiConfigResponse()
                .setting(seunjeonService.getSeunjeonItem(dictId, id).map(entity -> createEditBody(entity, dictId)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/dict/seunjeon/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$setting(final String dictId, final CreateBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final SeunjeonItem entity = new AdminDictSeunjeonAction().createSeunjeonItem(body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        seunjeonService.store(body.dictId, entity);
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(true).status(ApiResult.Status.OK)
                .result());
    }

    // POST /api/admin/dict/seunjeon/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$setting(final String dictId, final EditBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final SeunjeonItem entity = new AdminDictSeunjeonAction().createSeunjeonItem(body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        });
        seunjeonService.store(body.dictId, entity);
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(false).status(ApiResult.Status.OK)
                .result());
    }

    // DELETE /api/admin/dict/seunjeon/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String dictId, final long id) {
        seunjeonService.getSeunjeonItem(dictId, id).ifPresent(entity -> {
            seunjeonService.delete(dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
        });
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(id)).created(false).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/dict/seunjeon/upload/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$upload(final String dictId, final UploadForm form) {
        form.dictId = dictId;
        validateApi(form, messages -> {});
        final SeunjeonFile file = seunjeonService.getSeunjeonFile(form.dictId).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
            return null;
        });
        try (InputStream inputStream = form.seunjeonFile.getInputStream()) {
            file.update(inputStream);
        } catch (final Throwable e) {
            e.printStackTrace();
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/dict/seunjeon/download/{dictId}
    @Execute
    public StreamResponse get$download(final String dictId, final DownloadBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        return seunjeonService.getSeunjeonFile(body.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                try (InputStream inputStream = file.getInputStream()) {
                    out.write(inputStream);
                }
            });
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL));
            return null;
        });
    }

    protected EditBody createEditBody(final SeunjeonItem entity, final String dictId) {
        final EditBody body = new EditBody();
        body.id = entity.getId();
        body.dictId = dictId;
        body.inputs = entity.getInputsValue();
        return body;
    }
}
