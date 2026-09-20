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
package org.codelibs.fess.app.web.go;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.system.DBFluteSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter.RequestClientErrorException;
import org.lastaflute.web.servlet.request.ResponseManager;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.VaMore;
import org.lastaflute.web.validation.ValidationSuccess;

/**
 * Test class for GoAction.
 * Tests the isFileSystemPath method for various protocol types.
 */
public class GoActionTest extends UnitFessTestCase {

    private TestableGoAction goAction;

    /** Shared by {@code ComponentUtil.setFessConfig} and the action's own {@code fessConfig} field. */
    private FessConfig fessConfig;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // Setup protocolHelper with test configuration
        fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,smb1,ftp";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldConfigId() {
                return "configId";
            }

            @Override
            public boolean isSearchLog() {
                // Keeps index() off the click-log path, which needs userInfoHelper/SearchLogHelper
                // that these tests do not stub -- unrelated to the status/detail-key behavior
                // under test.
                return false;
            }

            @Override
            public boolean isSearchFileProxyEnabled() {
                return true;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        ComponentUtil.register(protocolHelper, "protocolHelper");

        goAction = new TestableGoAction();
        goAction.setSystemHelper(new FixedSystemHelper());
        goAction.setFessConfig(fessConfig);
        goAction.setResponseManager(ComponentUtil.getComponent(ResponseManager.class));
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        // Also unregisters everything ComponentUtil.register(...) added during this test
        // (protocolHelper, and the notFoundOnFileSystem/loadFailure tests' stub viewHelper):
        // ComponentUtil.setFessConfig(null) clears its whole componentMap as a side effect (see
        // ComponentUtil#setFessConfig), so a stub registered by one test cannot leak into a
        // later test in the same JVM. This is the same cleanup other Fess tests that call
        // ComponentUtil.register(...) rely on (e.g. AdminWizardActionTest).
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // Test class to expose protected methods for testing
    private static class TestableGoAction extends GoAction {
        @Override
        public boolean isFileSystemPath(final String url) {
            return super.isFileSystemPath(url);
        }

        @Override
        public LocalDateTime parseQueryRequestedAt(final String rt) {
            return super.parseQueryRequestedAt(rt);
        }

        @Override
        public String decodeHash(final String hash) {
            return super.decodeHash(hash);
        }

        // systemHelper is injected via @Resource in production; set it directly for unit tests.
        void setSystemHelper(final SystemHelper systemHelper) {
            this.systemHelper = systemHelper;
        }

        // fessConfig, searchHelper, pathMappingHelper and responseManager are injected via
        // @Resource in production; set them directly for unit tests that drive index().
        void setFessConfig(final FessConfig fessConfig) {
            this.fessConfig = fessConfig;
        }

        void setSearchHelper(final SearchHelper searchHelper) {
            this.searchHelper = searchHelper;
        }

        void setResponseManager(final ResponseManager responseManager) {
            this.responseManager = responseManager;
        }

        {
            // A plain pass-through: index()'s own branching is what these tests exercise, not
            // path-mapping rewriting, which would need a live PathMappingBhv/OpenSearch.
            this.pathMappingHelper = new PathMappingHelper() {
                @Override
                public String replaceUrl(final String url) {
                    return url;
                }
            };
        }

        // Login handling is orthogonal to the status/detail-key decisions under test here.
        @Override
        protected boolean isLoginRequired() {
            return false;
        }

        // getUserBean() normally reads fessLoginAssist, unset here; index() passes its result
        // straight through to searchHelper (ignored by the stub below), so an anonymous visitor
        // is enough and avoids a NullPointerException that index()'s own try/catch would
        // otherwise swallow as "doc not found", masking every branch below it.
        @Override
        protected OptionalThing<FessUserBean> getUserBean() {
            return OptionalThing.empty();
        }

        /**
         * When true, {@link #validate} invokes the production error hook instead of running real
         * Bean Validation -- exercising exactly the {@code validate(form, messages -> {}, () -> {
         * throw responseManager.new400(...); })} hook in {@link GoAction#index}, the same way
         * {@code SearchActionTest} stubs {@code validate()} rather than driving Hibernate
         * Validator, which real validation would need {@code requestManager} wired up to reach.
         */
        boolean failValidation;

        @Override
        public ValidationSuccess validate(final Object form, final VaMore<FessMessages> moreValidationLambda,
                final VaErrorHook validationErrorLambda) {
            if (failValidation) {
                // The production hook's body always throws; it never returns a value.
                validationErrorLambda.hook();
                throw new IllegalStateException("validationErrorLambda should have thrown");
            }
            return new ValidationSuccess(new FessMessages());
        }
    }

    /** Fixed instant used as "now" so the fallback is deterministic. */
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2020, 1, 2, 3, 4, 5);

    /** SystemHelper returning a fixed "current time" so the fallback branch is assertable. */
    private static class FixedSystemHelper extends SystemHelper {
        @Override
        public LocalDateTime getCurrentTimeAsLocalDateTime() {
            return FIXED_NOW;
        }
    }

    // ==================================================================================
    //                                                            parseQueryRequestedAt Tests
    //                                                            ===========================

    /**
     * Regression test: {@code rt} is an unconstrained request parameter, so a non-numeric
     * value used to reach {@code Long.parseLong} unguarded and raise NumberFormatException,
     * failing the user's navigation with an HTTP 500. A malformed value must instead be
     * treated as absent and fall back to the current time.
     */
    @Test
    public void test_parseQueryRequestedAt_malformed_fallsBackToCurrentTime() {
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("not-a-number"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(""));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(" "));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("123abc"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("1.5"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("1,000"));
        // Numeric but outside long range: parseLong also rejects these.
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("99999999999999999999999"));
    }

    /**
     * A missing {@code rt} must not throw either. {@code @Required} normally rejects this before
     * the action body runs, so this guards the method's own contract rather than the request flow.
     */
    @Test
    public void test_parseQueryRequestedAt_null_fallsBackToCurrentTime() {
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(null));
    }

    /**
     * A well-formed {@code rt} must still be honoured, not silently replaced by the fallback.
     *
     * <p>DfTypeUtil converts via {@code DBFluteSystem.getFinalTimeZone()} (Fess registers a
     * provider for it in FessCurtainBeforeHook), not via TimeZone.getDefault() at call time,
     * so the expectation is read back through that same zone to keep this test independent of
     * the host's time zone. That conversion zone differs from the v2 ClickHandler's UTC
     * conversion; aligning the two would change stored timestamps and is out of scope here.
     */
    @Test
    public void test_parseQueryRequestedAt_valid_usesRtValue() {
        final long rtMs = 1718454896789L; // 2024-06-15T12:34:56.789Z (mid-year: no DST-overlap ambiguity)
        final LocalDateTime actual = goAction.parseQueryRequestedAt(Long.toString(rtMs));
        assertFalse(FIXED_NOW.equals(actual));
        final ZoneId zone = DBFluteSystem.getFinalTimeZone().toZoneId();
        assertEquals(rtMs, ZonedDateTime.of(actual, zone).toInstant().toEpochMilli());
    }

    /**
     * Epoch 0 is a valid timestamp and must be parsed, not treated as absent.
     *
     * <p>The exact epoch is asserted rather than merely "not the fallback": the latter alone also
     * holds for any wrong-but-different instant, so it would not detect the value being mangled.
     * As above, the expectation is read back through {@code DBFluteSystem.getFinalTimeZone()} to
     * stay independent of the host's time zone.</p>
     */
    @Test
    public void test_parseQueryRequestedAt_zero_isParsed() {
        final LocalDateTime actual = goAction.parseQueryRequestedAt("0");
        assertFalse(FIXED_NOW.equals(actual));
        final ZoneId zone = DBFluteSystem.getFinalTimeZone().toZoneId();
        assertEquals(0L, ZonedDateTime.of(actual, zone).toInstant().toEpochMilli());
    }

    // ==================================================================================
    //                                                                       decodeHash Tests
    //                                                                       ================

    /**
     * Regression test: {@code hash} is an unconstrained request parameter, so a value with a
     * malformed escape used to reach {@code URLDecoder} unguarded and raise
     * IllegalArgumentException, failing the user's navigation with an HTTP 500. A malformed value
     * must instead be treated as absent, which drops the fragment and leaves the redirect intact.
     *
     * <p>These are the values the container hands to {@code GoForm.hash} after decoding the query
     * string of {@code /go?...&hash=%25}, {@code %25zz}, {@code %252} and {@code abc%25}: each
     * {@code %25} is a well-formed escape, so the container decodes it to a bare {@code %}, which
     * this action then decodes a second time. That double decode is what raised the error.</p>
     */
    @Test
    public void test_decodeHash_malformed_treatedAsAbsent() {
        assertNull(goAction.decodeHash("%")); // request: hash=%25
        assertNull(goAction.decodeHash("%zz")); // request: hash=%25zz
        assertNull(goAction.decodeHash("%2")); // request: hash=%252
        assertNull(goAction.decodeHash("abc%")); // request: hash=abc%25
    }

    /**
     * A well-formed {@code hash} must still be decoded, not dropped by the new guard.
     */
    @Test
    public void test_decodeHash_valid_isDecoded() {
        assertEquals("#", goAction.decodeHash("%23"));
        assertEquals("#section", goAction.decodeHash("#section"));
        assertEquals("a b", goAction.decodeHash("a%20b"));
    }

    /**
     * An absent {@code hash} is optional and must be reported as absent rather than throwing.
     *
     * <p>The blank check is load-bearing for whitespace alone: {@code URLUtil.decode(" ")} returns
     * {@code " "} rather than failing, so without it a blank fragment would be appended. A null or
     * empty argument instead raises EmptyArgumentException, which is an IllegalArgumentException
     * and so is already absorbed by the same catch that handles a malformed escape.</p>
     */
    @Test
    public void test_decodeHash_blank_treatedAsAbsent() {
        assertNull(goAction.decodeHash(null));
        assertNull(goAction.decodeHash(""));
        assertNull(goAction.decodeHash(" "));
    }

    // ==================================================================================
    //                                                                 isFileSystemPath Tests
    //                                                                 ====================

    @Test
    public void test_isFileSystemPath_file_protocol() {
        assertTrue(goAction.isFileSystemPath("file:///path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file://localhost/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file:/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file:C:/Users/test/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_smb_protocol() {
        assertTrue(goAction.isFileSystemPath("smb://server/share/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb://192.168.1.1/share/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb://server/"));
    }

    @Test
    public void test_isFileSystemPath_smb1_protocol() {
        assertTrue(goAction.isFileSystemPath("smb1://server/share/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb1://192.168.1.1/share/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb1://server/"));
    }

    @Test
    public void test_isFileSystemPath_ftp_protocol() {
        assertTrue(goAction.isFileSystemPath("ftp://ftp.example.com/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("ftp://user:pass@ftp.example.com/file.txt"));
        assertTrue(goAction.isFileSystemPath("ftp://192.168.1.1/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_s3_protocol() {
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("s3://my-bucket/folder/document.pdf"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/"));
        assertTrue(goAction.isFileSystemPath("s3://my-bucket-name/deep/nested/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket-with-dashes/file"));
    }

    @Test
    public void test_isFileSystemPath_gcs_protocol() {
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket/folder/document.pdf"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/"));
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket-name/deep/nested/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket_with_underscores/file"));
    }

    @Test
    public void test_isFileSystemPath_http_protocol_not_file_system() {
        assertFalse(goAction.isFileSystemPath("http://example.com/path/file.txt"));
        assertFalse(goAction.isFileSystemPath("http://localhost:8080/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_https_protocol_not_file_system() {
        assertFalse(goAction.isFileSystemPath("https://example.com/path/file.txt"));
        assertFalse(goAction.isFileSystemPath("https://secure.example.com/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_other_protocols_not_file_system() {
        assertFalse(goAction.isFileSystemPath("mailto:test@example.com"));
        assertFalse(goAction.isFileSystemPath("ldap://server/path"));
        assertFalse(goAction.isFileSystemPath("ssh://server/path"));
        assertFalse(goAction.isFileSystemPath("data:text/plain;base64,SGVsbG8="));
    }

    @Test
    public void test_isFileSystemPath_empty_and_invalid() {
        assertFalse(goAction.isFileSystemPath(""));
        assertFalse(goAction.isFileSystemPath("not-a-url"));
        assertFalse(goAction.isFileSystemPath("/local/path"));
        assertFalse(goAction.isFileSystemPath("C:\\Windows\\System32"));
    }

    @Test
    public void test_isFileSystemPath_case_sensitivity() {
        // URLs are case-sensitive for protocol
        assertFalse(goAction.isFileSystemPath("FILE://path"));
        assertFalse(goAction.isFileSystemPath("S3://bucket/path"));
        assertFalse(goAction.isFileSystemPath("GCS://bucket/path"));
        assertFalse(goAction.isFileSystemPath("FTP://server/path"));
        assertFalse(goAction.isFileSystemPath("SMB://server/share"));
    }

    @Test
    public void test_isFileSystemPath_s3_various_bucket_names() {
        // S3 bucket names can contain lowercase letters, numbers, hyphens, and periods
        assertTrue(goAction.isFileSystemPath("s3://my-bucket/file"));
        assertTrue(goAction.isFileSystemPath("s3://my.bucket/file"));
        assertTrue(goAction.isFileSystemPath("s3://mybucket123/file"));
        assertTrue(goAction.isFileSystemPath("s3://123bucket/file"));
    }

    @Test
    public void test_isFileSystemPath_gcs_various_bucket_names() {
        // GCS bucket names can contain lowercase letters, numbers, hyphens, underscores, and periods
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://my.bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://my_bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://mybucket123/file"));
    }

    @Test
    public void test_isFileSystemPath_s3_with_special_characters_in_path() {
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/file%20with%20spaces.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/ファイル.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/file+name.txt"));
    }

    @Test
    public void test_isFileSystemPath_gcs_with_special_characters_in_path() {
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/file%20with%20spaces.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/ファイル.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/file+name.txt"));
    }

    // ==================================================================================
    //                                                                index() status Tests
    //                                                                ====================
    // Spec table (task 7): each branch reports the real status at the real URL instead of
    // redirecting to ErrorAction, and records the detail key ErrorPageServlet reads off the
    // request. validate()'s own error hook is exercised via TestableGoAction.failValidation
    // rather than real Bean Validation, which would need requestManager wired up to reach --
    // the same seam SearchActionTest uses. The 400/404 branches throw a
    // RequestClientErrorException that never reaches the container's RequestLoggingFilter in
    // this unit test, so only the exception's type and its getErrorStatus() (via
    // assertClientError below) and the detail-key attribute are assertable here; the eventual
    // sendError(<status>) conversion is that filter's job, not index()'s, and is out of reach of
    // a plain action-method call. The 500 branch is different: index() itself calls sendError,
    // so that status is asserted directly on the mock response.

    private GoForm newGoForm() {
        final GoForm form = new GoForm();
        form.docId = "doc1";
        form.queryId = "query1";
        return form;
    }

    /**
     * Asserts {@code e} is the framework's client-error exception carrying exactly
     * {@code expectedStatus} -- not merely that its class name happens to contain the status
     * digits, which an unrelated class could also satisfy.
     */
    private void assertClientError(final int expectedStatus, final Exception e) {
        assertTrue(e instanceof RequestClientErrorException,
                "expected a " + expectedStatus + " RequestClientErrorException, got " + e.getClass());
        assertEquals(expectedStatus, ((RequestClientErrorException) e).getErrorStatus());
    }

    /** Stub returning a fixed document (or none) without touching the search engine. */
    private static class StubSearchHelper extends SearchHelper {
        private final Map<String, Object> doc;

        StubSearchHelper(final Map<String, Object> doc) {
            this.doc = doc;
        }

        @Override
        public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
                final OptionalThing<FessUserBean> userBean) {
            // GoAction.index() only ever calls .orElse(null) on this, never .get(), so the
            // not-found thrower below is never invoked.
            return OptionalEntity.ofNullable(doc, () -> {});
        }
    }

    @Test
    public void test_go_validationFailure_is400() {
        goAction.failValidation = true;
        try {
            goAction.index(newGoForm());
            fail("expected a 400");
        } catch (final Exception e) {
            assertClientError(400, e);
        }
    }

    @Test
    public void test_go_docIdNotFound_is404WithDetailKey() throws Exception {
        goAction.setSearchHelper(new StubSearchHelper(null));
        try {
            goAction.index(newGoForm());
            fail("expected a 404");
        } catch (final Exception e) {
            assertClientError(404, e);
        }
        assertEquals("errors.docid_not_found", getMockRequest().getAttribute(Constants.ERROR_DETAIL_KEY));
    }

    @Test
    public void test_go_documentUrlMissing_is404WithDetailKey() throws Exception {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("id", "doc1");
        goAction.setSearchHelper(new StubSearchHelper(doc));
        try {
            goAction.index(newGoForm());
            fail("expected a 404");
        } catch (final Exception e) {
            assertClientError(404, e);
        }
        assertEquals("errors.document_not_found", getMockRequest().getAttribute(Constants.ERROR_DETAIL_KEY));
    }

    @Test
    public void test_go_unsafeRedirectUrl_is404WithDetailKey() throws Exception {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "javascript:alert(1)");
        goAction.setSearchHelper(new StubSearchHelper(doc));
        try {
            goAction.index(newGoForm());
            fail("expected a 404");
        } catch (final Exception e) {
            assertClientError(404, e);
        }
        assertEquals("errors.document_not_found", getMockRequest().getAttribute(Constants.ERROR_DETAIL_KEY));
    }

    @Test
    public void test_go_notFoundOnFileSystem_is404WithDetailKey() throws Exception {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "file:///tmp/does-not-exist.txt");
        goAction.setSearchHelper(new StubSearchHelper(doc));
        ComponentUtil.register(new ViewHelper() {
            @Override
            public StreamResponse asContentResponse(final Map<String, Object> requestedDoc) {
                return new StreamResponse("does-not-exist.txt").httpStatus(404);
            }
        }, "viewHelper");
        try {
            goAction.index(newGoForm());
            fail("expected a 404");
        } catch (final Exception e) {
            assertClientError(404, e);
        }
        assertEquals("errors.not_found_on_file_system", getMockRequest().getAttribute(Constants.ERROR_DETAIL_KEY));
    }

    @Test
    public void test_go_loadFailure_is500WithDetailKey() throws Exception {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "file:///tmp/unreachable.txt");
        goAction.setSearchHelper(new StubSearchHelper(doc));
        ComponentUtil.register(new ViewHelper() {
            @Override
            public StreamResponse asContentResponse(final Map<String, Object> requestedDoc) {
                throw new RuntimeException("simulated file server failure");
            }
        }, "viewHelper");

        final ActionResponse response = goAction.index(newGoForm());

        assertEquals(500, getMockResponse().getStatus());
        assertEquals("errors.not_load_from_server", getMockRequest().getAttribute(Constants.ERROR_DETAIL_KEY));
        assertTrue(response.isReturnAsEmptyBody(),
                "must return asEmptyBody() -- undefined() is refused from an"
                        + " @Execute method by RedCardableAssist#assertExecuteMethodResponseDefined, and asEmptyBody() is"
                        + " what pins \"no body, no forward\": a plain HtmlResponse.fromRedirectPathAsIs(...) is also"
                        + " !isUndefined() but is not an empty body");
    }
}
