package org.codelibs.fess.app.web.api.admin.dict;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;
import org.codelibs.fess.dict.DictionaryManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import javax.annotation.Resource;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiAdminDictAction extends FessApiAdminAction {
    @Resource
    protected DictionaryManager dictionaryManager;

    // GET /api/admin/dict
    // POST /api/admin/dict
    @Execute
    public JsonResponse<ApiResult> settings(final ListBody body) {
        validateApi(body, messages -> {});
        final DictionaryFile<? extends DictionaryItem>[] dictFiles = dictionaryManager.getDictionaryFiles();
        return asJson(new ApiResult.ApiConfigsResponse<ListBody>()
                .settings(Stream.of(dictFiles).map(dictionaryFile -> createListBody(dictionaryFile)).collect(Collectors.toList()))
                .status(ApiResult.Status.OK).result());
    }

    protected ListBody createListBody(DictionaryFile<? extends DictionaryItem> dictionaryFile) {
        final ListBody body = new ListBody();
        body.id = dictionaryFile.getId();
        body.type = dictionaryFile.getType();
        body.path = dictionaryFile.getPath();
        body.timestamp = dictionaryFile.getTimestamp();
        return body;
    }
}
