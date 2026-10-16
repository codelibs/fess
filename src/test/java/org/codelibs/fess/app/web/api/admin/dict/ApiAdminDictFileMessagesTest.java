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
package org.codelibs.fess.app.web.api.admin.dict;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.codelibs.fess.app.service.CharMappingService;
import org.codelibs.fess.app.service.KuromojiService;
import org.codelibs.fess.app.service.ProtwordsService;
import org.codelibs.fess.app.service.StemmerOverrideService;
import org.codelibs.fess.app.service.StopwordsService;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.api.admin.dict.kuromoji.ApiAdminDictKuromojiAction;
import org.codelibs.fess.app.web.api.admin.dict.mapping.ApiAdminDictMappingAction;
import org.codelibs.fess.app.web.api.admin.dict.protwords.ApiAdminDictProtwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.stemmeroverride.ApiAdminDictStemmeroverrideAction;
import org.codelibs.fess.app.web.api.admin.dict.stopwords.ApiAdminDictStopwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.synonym.ApiAdminDictSynonymAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.mapping.CharMappingFile;
import org.codelibs.fess.dict.protwords.ProtwordsFile;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideFile;
import org.codelibs.fess.dict.stopwords.StopwordsFile;
import org.codelibs.fess.dict.synonym.SynonymFile;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.exception.ValidationErrorException;

/**
 * When the dictionary file does not exist, the upload and the download of each dictionary API are
 * refused with the error message of their own dictionary type, not with the one of another type.
 */
public class ApiAdminDictFileMessagesTest extends UnitFessTestCase {

    // ===================================================================================
    //                                                                            Download
    //                                                                            ========

    @Test
    public void test_kuromoji_download() throws Exception {
        suppressBindingOf(KuromojiService.class);
        final ApiAdminDictKuromojiAction action = injectAction(new ApiAdminDictKuromojiAction());
        setField(action, "kuromojiService", new KuromojiService() {
            @Override
            public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_kuromoji_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.kuromoji.DownloadBody()));
    }

    @Test
    public void test_mapping_download() throws Exception {
        suppressBindingOf(CharMappingService.class);
        final ApiAdminDictMappingAction action = injectAction(new ApiAdminDictMappingAction());
        setField(action, "charMappingService", new CharMappingService() {
            @Override
            public OptionalEntity<CharMappingFile> getCharMappingFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_mapping_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.mapping.DownloadBody()));
    }

    @Test
    public void test_protwords_download() throws Exception {
        suppressBindingOf(ProtwordsService.class);
        final ApiAdminDictProtwordsAction action = injectAction(new ApiAdminDictProtwordsAction());
        setField(action, "protwordsService", new ProtwordsService() {
            @Override
            public OptionalEntity<ProtwordsFile> getProtwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_protwords_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.protwords.DownloadBody()));
    }

    @Test
    public void test_stemmeroverride_download() throws Exception {
        suppressBindingOf(StemmerOverrideService.class);
        final ApiAdminDictStemmeroverrideAction action = injectAction(new ApiAdminDictStemmeroverrideAction());
        setField(action, "stemmerOverrideService", new StemmerOverrideService() {
            @Override
            public OptionalEntity<StemmerOverrideFile> getStemmerOverrideFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_stemmeroverride_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.stemmeroverride.DownloadBody()));
    }

    @Test
    public void test_stopwords_download() throws Exception {
        suppressBindingOf(StopwordsService.class);
        final ApiAdminDictStopwordsAction action = injectAction(new ApiAdminDictStopwordsAction());
        setField(action, "stopwordsService", new StopwordsService() {
            @Override
            public OptionalEntity<StopwordsFile> getStopwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_stopwords_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.stopwords.DownloadBody()));
    }

    @Test
    public void test_synonym_download() throws Exception {
        suppressBindingOf(SynonymService.class);
        final ApiAdminDictSynonymAction action = injectAction(new ApiAdminDictSynonymAction());
        setField(action, "synonymService", new SynonymService() {
            @Override
            public OptionalEntity<SynonymFile> getSynonymFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        assertGlobalError(FessMessages.ERRORS_failed_to_download_synonym_file,
                () -> action.get$download("id", new org.codelibs.fess.app.web.api.admin.dict.synonym.DownloadBody()));
    }

    // ===================================================================================
    //                                                                              Upload
    //                                                                              ======

    @Test
    public void test_kuromoji_upload() throws Exception {
        suppressBindingOf(KuromojiService.class);
        final ApiAdminDictKuromojiAction action = injectAction(new ApiAdminDictKuromojiAction());
        setField(action, "kuromojiService", new KuromojiService() {
            @Override
            public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.kuromoji.UploadForm form =
                new org.codelibs.fess.app.web.admin.dict.kuromoji.UploadForm();
        form.kuromojiFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_kuromoji_file, () -> action.put$upload("id", form));
    }

    @Test
    public void test_mapping_upload() throws Exception {
        suppressBindingOf(CharMappingService.class);
        final ApiAdminDictMappingAction action = injectAction(new ApiAdminDictMappingAction());
        setField(action, "charMappingService", new CharMappingService() {
            @Override
            public OptionalEntity<CharMappingFile> getCharMappingFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.mapping.UploadForm form = new org.codelibs.fess.app.web.admin.dict.mapping.UploadForm();
        form.charMappingFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_mapping_file, () -> action.put$upload("id", form));
    }

    @Test
    public void test_protwords_upload() throws Exception {
        suppressBindingOf(ProtwordsService.class);
        final ApiAdminDictProtwordsAction action = injectAction(new ApiAdminDictProtwordsAction());
        setField(action, "protwordsService", new ProtwordsService() {
            @Override
            public OptionalEntity<ProtwordsFile> getProtwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.protwords.UploadForm form =
                new org.codelibs.fess.app.web.admin.dict.protwords.UploadForm();
        form.protwordsFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_protwords_file, () -> action.put$upload("id", form));
    }

    @Test
    public void test_stemmeroverride_upload() throws Exception {
        suppressBindingOf(StemmerOverrideService.class);
        final ApiAdminDictStemmeroverrideAction action = injectAction(new ApiAdminDictStemmeroverrideAction());
        setField(action, "stemmerOverrideService", new StemmerOverrideService() {
            @Override
            public OptionalEntity<StemmerOverrideFile> getStemmerOverrideFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.stemmeroverride.UploadForm form =
                new org.codelibs.fess.app.web.admin.dict.stemmeroverride.UploadForm();
        form.stemmerOverrideFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_stemmeroverride_file, () -> action.put$upload("id", form));
    }

    @Test
    public void test_stopwords_upload() throws Exception {
        suppressBindingOf(StopwordsService.class);
        final ApiAdminDictStopwordsAction action = injectAction(new ApiAdminDictStopwordsAction());
        setField(action, "stopwordsService", new StopwordsService() {
            @Override
            public OptionalEntity<StopwordsFile> getStopwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.stopwords.UploadForm form =
                new org.codelibs.fess.app.web.admin.dict.stopwords.UploadForm();
        form.stopwordsFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_stopwords_file, () -> action.put$upload("id", form));
    }

    @Test
    public void test_synonym_upload() throws Exception {
        suppressBindingOf(SynonymService.class);
        final ApiAdminDictSynonymAction action = injectAction(new ApiAdminDictSynonymAction());
        setField(action, "synonymService", new SynonymService() {
            @Override
            public OptionalEntity<SynonymFile> getSynonymFile(final String dictId) {
                return OptionalEntity.empty();
            }
        });
        final org.codelibs.fess.app.web.admin.dict.synonym.UploadForm form = new org.codelibs.fess.app.web.admin.dict.synonym.UploadForm();
        form.synonymFile = uploadedFile();
        assertGlobalError(FessMessages.ERRORS_failed_to_upload_synonym_file, () -> action.put$upload("id", form));
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                             =======

    private interface Call {
        void run() throws Exception;
    }

    /** The action refuses with the given message on the global property, and with no message of another kind. */
    private void assertGlobalError(final String expectedMessage, final Call call) throws Exception {
        try {
            call.run();
        } catch (final ValidationErrorException e) {
            assertTrue(e.getMessages().hasMessageOf("_global", expectedMessage), "expected " + expectedMessage);
            return;
        }
        fail("a validation error is expected");
    }

    /** The service answers before the content of the file is read, so only its presence matters. */
    private static MultipartFormFile uploadedFile() {
        return (MultipartFormFile) Proxy.newProxyInstance(MultipartFormFile.class.getClassLoader(),
                new Class<?>[] { MultipartFormFile.class }, (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    /**
     * Injects the framework fields that {@code validateApi()} and {@code throwValidationErrorApi()} need.
     */
    private <T extends FessBaseAction> T injectAction(final T action) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        inject(action);

        final SystemHelper systemHelper = new SystemHelper();
        final Field systemHelperField = FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        if (systemHelperField.get(action) == null) {
            systemHelperField.set(action, systemHelper);
        }
        ComponentUtil.register(systemHelper, "systemHelper");

        final Field fessConfigField = FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        if (fessConfigField.get(action) == null) {
            fessConfigField.set(action, ComponentUtil.getFessConfig());
        }
        return action;
    }

    private static void setField(final Object target, final String name, final Object value) throws Exception {
        final Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
