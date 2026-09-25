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
package org.codelibs.fess.app.web.api.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.service.BadWordService;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.admin.dict.synonym.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.badword.ApiAdminBadwordAction;
import org.codelibs.fess.app.web.api.admin.dict.kuromoji.ApiAdminDictKuromojiAction;
import org.codelibs.fess.app.web.api.admin.dict.mapping.ApiAdminDictMappingAction;
import org.codelibs.fess.app.web.api.admin.dict.protwords.ApiAdminDictProtwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.stemmeroverride.ApiAdminDictStemmeroverrideAction;
import org.codelibs.fess.app.web.api.admin.dict.stopwords.ApiAdminDictStopwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.synonym.ApiAdminDictSynonymAction;
import org.codelibs.fess.app.web.api.admin.elevateword.ApiAdminElevatewordAction;
import org.codelibs.fess.app.web.api.admin.storage.ApiAdminStorageAction;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

/**
 * Contract tests for the multipart upload endpoints of the admin API
 * ({@code /api/admin/dict/{type}/upload/{dictId}}, {@code /api/admin/badword/upload},
 * {@code /api/admin/elevateword/upload} and {@code /api/admin/storage/upload/}).
 *
 * <p>LastaFlute parses a {@code multipart/form-data} body only for POST, so a PUT upload always
 * reaches the action without its file. Each upload therefore has to be offered as POST, not only
 * as PUT.</p>
 */
public class ApiAdminUploadTest extends UnitFessTestCase {

    private static final Class<?>[] UPLOAD_ACTIONS = { ApiAdminDictKuromojiAction.class, ApiAdminDictMappingAction.class,
            ApiAdminDictProtwordsAction.class, ApiAdminDictStemmeroverrideAction.class, ApiAdminDictStopwordsAction.class,
            ApiAdminDictSynonymAction.class, ApiAdminBadwordAction.class, ApiAdminElevatewordAction.class, ApiAdminStorageAction.class };

    @Test
    public void test_everyUploadIsServedByPost() throws Exception {
        for (final Class<?> actionType : UPLOAD_ACTIONS) {
            final Method put = findMethod(actionType, "put$upload");
            assertNotNull(put, actionType.getSimpleName() + " must keep put$upload");

            final Method post = findMethod(actionType, "post$upload");
            assertNotNull(post, actionType.getSimpleName() + " must serve the upload by POST");
            assertNotNull(post.getAnnotation(Execute.class), actionType.getSimpleName() + "#post$upload must be an @Execute method");
            assertTrue(Arrays.equals(put.getParameterTypes(), post.getParameterTypes()),
                    actionType.getSimpleName() + "#post$upload must take the same parameters as put$upload");
        }
    }

    @Test
    public void test_dictSynonym_post$upload_writesTheUploadedFileIntoTheDictionary() throws Exception {
        final AtomicReference<String> written = new AtomicReference<>();
        final SynonymFile synonymFile = new SynonymFile("synonym-id", "synonym.txt", new Date()) {
            @Override
            public synchronized void update(final InputStream in) throws IOException {
                written.set(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
        };
        final ApiAdminDictSynonymAction action = createSynonymAction(synonymFile);

        final UploadForm form = new UploadForm();
        form.synonymFile = new BytesFormFile("a,b,c\n".getBytes(StandardCharsets.UTF_8));

        assertOkResponse(action.post$upload("synonym-id", form));
        assertEquals("synonym-id", form.dictId);
        assertEquals("a,b,c\n", written.get());
    }

    @Test
    public void test_dictSynonym_post$upload_withoutAFile_isRejected() throws Exception {
        final ApiAdminDictSynonymAction action = createSynonymAction(null);

        final UploadForm form = new UploadForm();

        assertValidationError(() -> action.post$upload("synonym-id", form)).handle(data -> {
            data.requiredMessageOf("synonymFile", Required.class);
        });
    }

    @Test
    public void test_dictSynonym_post$upload_unknownDictionary_isRejected() throws Exception {
        final ApiAdminDictSynonymAction action = createSynonymAction(null);

        final UploadForm form = new UploadForm();
        form.synonymFile = new BytesFormFile("a,b\n".getBytes(StandardCharsets.UTF_8));

        assertValidationError(() -> action.post$upload("missing-id", form)).handle(data -> {
            data.requiredMessageOf("_global", "errors.failed_to_upload_protwords_file");
        });
    }

    @Test
    public void test_badword_post$upload_importsTheUploadedCsv() throws Exception {
        final AtomicReference<String> imported = new AtomicReference<>();
        final CountDownLatch done = new CountDownLatch(1);
        final ApiAdminBadwordAction action = createBadwordAction(new BadWordService() {
            @Override
            public void importCsv(final Reader reader) {
                try {
                    final StringBuilder buf = new StringBuilder();
                    for (int c = reader.read(); c != -1; c = reader.read()) {
                        buf.append((char) c);
                    }
                    imported.set(buf.toString());
                } catch (final IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }, done);

        final org.codelibs.fess.app.web.admin.badword.UploadForm form = new org.codelibs.fess.app.web.admin.badword.UploadForm();
        form.badWordFile = new BytesFormFile("\"SuggestWord\"\n\"foo\"\n".getBytes(StandardCharsets.UTF_8));

        assertOkResponse(action.post$upload(form));
        // the import runs on the common pool; it finishes with storeAllBadWords()
        assertTrue(done.await(10, TimeUnit.SECONDS), "the CSV import must run");
        assertEquals("\"SuggestWord\"\n\"foo\"\n", imported.get());
    }

    @Test
    public void test_badword_post$upload_withoutAFile_isRejected() throws Exception {
        final ApiAdminBadwordAction action = createBadwordAction(new BadWordService() {
            @Override
            public void importCsv(final Reader reader) {
                fail("importCsv() must not be reached when the request has no file");
            }
        }, new CountDownLatch(1));

        final org.codelibs.fess.app.web.admin.badword.UploadForm form = new org.codelibs.fess.app.web.admin.badword.UploadForm();

        assertValidationError(() -> action.post$upload(form)).handle(data -> {
            data.requiredMessageOf("badWordFile", Required.class);
        });
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                             =======

    private static Method findMethod(final Class<?> type, final String name) {
        return Arrays.stream(type.getMethods()).filter(m -> m.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Wires an {@link ApiAdminDictSynonymAction} whose {@link SynonymService} knows only
     * {@code synonymFile} (or no dictionary at all when it is null).
     */
    private ApiAdminDictSynonymAction createSynonymAction(final SynonymFile synonymFile) throws Exception {
        suppressBindingOf(SynonymService.class);
        final ApiAdminDictSynonymAction action = injectAction(new ApiAdminDictSynonymAction());
        setField(ApiAdminDictSynonymAction.class, action, "synonymService", new SynonymService() {
            @Override
            public OptionalEntity<SynonymFile> getSynonymFile(final String dictId) {
                if (synonymFile != null && synonymFile.getId().equals(dictId)) {
                    return OptionalEntity.of(synonymFile);
                }
                return OptionalEntity.empty();
            }
        });
        return action;
    }

    /**
     * Wires an {@link ApiAdminBadwordAction} with the given {@link BadWordService} and a
     * {@link SuggestHelper} that counts {@code done} down when the import stores the bad words.
     */
    private ApiAdminBadwordAction createBadwordAction(final BadWordService badWordService, final CountDownLatch done) throws Exception {
        suppressBindingOf(BadWordService.class);
        suppressBindingOf(SuggestHelper.class);
        final ApiAdminBadwordAction action = injectAction(new ApiAdminBadwordAction());
        setField(ApiAdminBadwordAction.class, action, "badWordService", badWordService);
        setField(ApiAdminBadwordAction.class, action, "suggestHelper", new SuggestHelper() {
            @Override
            public void storeAllBadWords(final boolean apply) {
                done.countDown();
            }
        });
        return action;
    }

    /**
     * Injects the framework fields that {@code validateApi()}/{@code asJson()} need, as
     * {@code ApiAdminThemeActionTest#createInjectedAction} does.
     */
    private <T extends org.codelibs.fess.app.web.base.FessBaseAction> T injectAction(final T action) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        inject(action);

        final org.codelibs.fess.helper.SystemHelper systemHelperInstance = new org.codelibs.fess.helper.SystemHelper();
        final Field systemHelperField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        if (systemHelperField.get(action) == null) {
            systemHelperField.set(action, systemHelperInstance);
        }
        ComponentUtil.register(systemHelperInstance, "systemHelper");

        final Field fessConfigField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        if (fessConfigField.get(action) == null) {
            fessConfigField.set(action, ComponentUtil.getFessConfig());
        }
        return action;
    }

    private static void setField(final Class<?> type, final Object target, final String name, final Object value) throws Exception {
        final Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void assertOkResponse(final JsonResponse<ApiResult> response) throws Exception {
        assertNotNull(response);
        final ApiResult result = response.getJsonResult();
        assertNotNull(result);

        final Field responseField = ApiResult.class.getDeclaredField("response");
        responseField.setAccessible(true);
        final Object apiResponse = responseField.get(result);
        assertNotNull(apiResponse);

        final Field statusField = ApiResult.ApiResponse.class.getDeclaredField("status");
        statusField.setAccessible(true);
        assertEquals(ApiResult.Status.OK.getId(), ((Integer) statusField.get(apiResponse)).intValue());
    }

    private static class BytesFormFile implements MultipartFormFile {
        private final byte[] data;

        BytesFormFile(final byte[] data) {
            this.data = data;
        }

        @Override
        public byte[] getFileData() {
            return data;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public int getFileSize() {
            return data.length;
        }

        @Override
        public String getFileName() {
            return "upload.txt";
        }

        @Override
        public void destroy() {
        }
    }
}
