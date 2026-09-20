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
package org.codelibs.fess.theme;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.curl.CurlRequest;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ThemeArtifactHelperTest extends UnitFessTestCase {

    @Test
    public void test_parseIndex() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        final List<String> names = helper.parseIndex("docuforge\ncode-search\n\ndocuforge\n");
        assertEquals(2, names.size());
        assertEquals("docuforge", names.get(0));
        assertEquals("code-search", names.get(1));
    }

    @Test
    public void test_parseIndex_keepsALastLineWithNoTrailingNewline() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        final List<String> names = helper.parseIndex("docuforge\nvoicebox");
        assertEquals(2, names.size());
        assertEquals("voicebox", names.get(1));
    }

    @Test
    public void test_parseIndex_refusesABodyThatIsNotAnIndex() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        // A proxy page, a captive portal or an error document answering 200 would
        // otherwise be read as a catalogue whose theme names are fragments of HTML.
        assertTrue(helper.parseIndex("<html><body>nope</body></html>").isEmpty());
        assertTrue(helper.parseIndex("docuforge\n../escape").isEmpty());
        assertTrue(helper.parseIndex("DocuForge").isEmpty());
    }

    @Test
    public void test_parseVersions() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
                + "<metadata><groupId>org.codelibs.fess.themes</groupId><artifactId>docuforge</artifactId>" //
                + "<versioning><latest>15.8.1</latest><release>15.8.1</release>" //
                + "<versions><version>15.8.0</version><version>15.8.1</version></versions>" //
                + "<lastUpdated>20260920000000</lastUpdated></versioning></metadata>";
        final List<String> versions = helper.parseVersions(xml);
        assertEquals(2, versions.size());
        assertEquals("15.8.0", versions.get(0));
        assertEquals("15.8.1", versions.get(1));
    }

    @Test
    public void test_parseVersions_rejectsExternalEntities() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        final String xml = "<?xml version=\"1.0\"?>" //
                + "<!DOCTYPE metadata [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" //
                + "<metadata><versioning><versions><version>&xxe;</version></versions></versioning></metadata>";
        // Secure processing must refuse the entity; the helper returns no versions
        // rather than leaking file content.
        assertTrue(helper.parseVersions(xml).isEmpty());
    }

    @Test
    public void test_parseVersions_dropsAMalformedVersionButKeepsWellFormedOnes() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        // Versions come from a remote maven-metadata.xml; isTargetPluginVersion's default
        // pattern is "^<productVersion>.*", whose ".*" would happily match a path-traversal
        // payload spliced later into buildArtifactUrl. Every entry must survive a shape check.
        final String xml = "<?xml version=\"1.0\"?><metadata><versioning><versions>" //
                + "<version>15.8.0</version>" //
                + "<version>15.9.0/../../evil</version>" //
                + "<version>not-a-version</version>" //
                + "<version>15.9.1</version>" //
                + "</versions></versioning></metadata>";
        final List<String> versions = helper.parseVersions(xml);
        assertEquals(2, versions.size());
        assertEquals("15.8.0", versions.get(0));
        assertEquals("15.9.1", versions.get(1));
    }

    @Test
    public void test_getAvailableArtifacts_doesNotCacheAFailedIndexFetch() {
        // A failed fetch of theme-index.txt (a 500, a timeout, a DNS failure) must not be
        // cached as "no themes": that would make a repository that recovered a second later
        // look empty to the admin screen for the full 5-minute expireAfterWrite. Every call
        // here fails, so if the failure were cached, only the first call would reach
        // readIndexOrThrow and the counter would stop at 1.
        final AtomicInteger indexFetchAttempts = new AtomicInteger();
        final ThemeArtifactHelper helper = new ThemeArtifactHelper() {
            @Override
            protected String[] getRepositories() {
                return new String[] { "https://example.invalid/themes/" };
            }

            @Override
            protected String readIndexOrThrow(final String url) throws RepositoryUnreadableException {
                indexFetchAttempts.incrementAndGet();
                throw new RepositoryUnreadableException("simulated failure");
            }
        };
        assertTrue(helper.getAvailableArtifacts().isEmpty());
        assertTrue(helper.getAvailableArtifacts().isEmpty());
        assertEquals(2, indexFetchAttempts.get(),
                "a failed index fetch must not be cached as \"no themes\"; the second call should fetch again");
    }

    @Test
    public void test_readIndexOrThrow_carriesTheUnderlyingExceptionAsItsCause() {
        // createCurlRequest is the seam: it throws before any network I/O happens, so this
        // exercises readIndexOrThrow's exception branch without a real connection. The
        // HTTP-status branch (a non-200 response) is not covered here: producing one without
        // touching the network would mean faking CurlRequest.execute()'s HttpURLConnection,
        // which is real contortion for what the message-building line already shows is a
        // one-line string concatenation ("HTTP " + status + " for " + url).
        final RuntimeException connectionFailure = new RuntimeException("simulated DNS failure");
        final ThemeArtifactHelper helper = new ThemeArtifactHelper() {
            @Override
            protected CurlRequest createCurlRequest(final String url) {
                throw connectionFailure;
            }
        };
        final ThemeArtifactHelper.RepositoryUnreadableException thrown =
                assertThrows(ThemeArtifactHelper.RepositoryUnreadableException.class,
                        () -> helper.readIndexOrThrow("https://example.invalid/themes/theme-index.txt"));
        assertSame(connectionFailure, thrown.getCause());
        assertTrue(thrown.getMessage().contains("https://example.invalid/themes/theme-index.txt"),
                "the message should name the URL that failed");
    }

    @Test
    public void test_buildArtifactUrl() {
        final ThemeArtifactHelper helper = new ThemeArtifactHelper();
        assertEquals("https://example.invalid/themes/docuforge/15.8.0/docuforge-15.8.0.zip",
                helper.buildArtifactUrl("https://example.invalid/themes/", "docuforge", "15.8.0"));
        assertEquals("https://example.invalid/themes/docuforge/15.8.0/docuforge-15.8.0.zip",
                helper.buildArtifactUrl("https://example.invalid/themes", "docuforge", "15.8.0"));
    }

    @Test
    public void test_matchesChecksum() {
        final String sha1 = "0123456789abcdef0123456789abcdef01234567";
        assertTrue(ThemeArtifactHelper.matchesChecksum(sha1, sha1));
        // maven.codelibs.org serves bare 40-hex, but tolerate a sha1sum-style line
        assertTrue(ThemeArtifactHelper.matchesChecksum(sha1 + "  theme.zip", sha1));
        assertTrue(ThemeArtifactHelper.matchesChecksum(sha1.toUpperCase(java.util.Locale.ROOT), sha1));
        assertTrue(ThemeArtifactHelper.matchesChecksum(" " + sha1 + "\n", sha1));
        assertFalse(ThemeArtifactHelper.matchesChecksum("dead" + sha1.substring(4), sha1));
        assertFalse(ThemeArtifactHelper.matchesChecksum("", sha1));
        assertFalse(ThemeArtifactHelper.matchesChecksum(null, sha1));
    }

    @Test
    public void test_install_unpublishedArtifact() {
        // "no-such-theme" is a name that has never been published, and download() is
        // overridden -- not getAvailableArtifacts(), which install() never consults (see
        // buildArtifactUrl / getRepositories in install()) -- so the request never leaves
        // this process. The overridden body simulates exactly what a real repository
        // answers for a name/version it does not have: an HTTP 404 from download(). The
        // assertion checks that specific failure, not merely that the message happens to
        // contain the theme name.
        final ThemeArtifactHelper helper = new ThemeArtifactHelper() {
            @Override
            protected void download(final String url, final Path dest) {
                throw new ThemeArtifactException("HTTP 404 for " + url);
            }
        };
        final ThemeArtifactHelper.ThemeArtifactException ex =
                assertThrows(ThemeArtifactHelper.ThemeArtifactException.class, () -> helper.install("no-such-theme", "0.0.1"));
        assertTrue(ex.getMessage().contains("404"), "expected the 404 from the repository to reach the caller, got: " + ex.getMessage());
    }

    @Test
    public void test_install_propagatesInstallExceptionAndStopsAtTheFirstRepository() {
        // Pins two things: (1) an InstallException from installZip must reach the caller
        // unchanged, with its Code intact, rather than wrapped as a generic
        // ThemeArtifactException -- that Code is what the admin screen and the API map to
        // a specific message such as INCOMPATIBLE_FESS_VERSION; and (2) the repository
        // loop must stop there rather than trying a second repository -- the archive was
        // already fetched and verified, so moving on would install a theme the operator
        // did not name.
        final AtomicInteger downloadAttempts = new AtomicInteger();
        final StaticThemeInstaller.InstallException expected = new StaticThemeInstaller.InstallException(
                StaticThemeInstaller.InstallException.Code.INCOMPATIBLE_FESS_VERSION, "requires a newer Fess");
        final ThemeArtifactHelper helper = new ThemeArtifactHelper() {
            @Override
            protected String[] getRepositories() {
                return new String[] { "https://example.invalid/repo1/", "https://example.invalid/repo2/" };
            }

            @Override
            protected void download(final String url, final Path dest) {
                downloadAttempts.incrementAndGet();
                try {
                    Files.write(dest, "zip-bytes".getBytes(StandardCharsets.UTF_8));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void verifyChecksum(final ThemeArtifact artifact, final Path file) {
                // Checksum matching is covered by test_matchesChecksum; skipped here so
                // this test pins only the InstallException behaviour.
            }
        };
        helper.staticThemeInstaller = new StaticThemeInstaller() {
            @Override
            public void installZip(final InputStream zipStream) {
                throw expected;
            }
        };
        final StaticThemeInstaller.InstallException thrown =
                assertThrows(StaticThemeInstaller.InstallException.class, () -> helper.install("docuforge", "15.8.0"));
        assertSame(expected, thrown, "install() must not wrap or replace the InstallException");
        assertEquals(StaticThemeInstaller.InstallException.Code.INCOMPATIBLE_FESS_VERSION, thrown.code());
        assertEquals(1, downloadAttempts.get(), "the second repository must not be tried after an InstallException");
    }

    @Test
    public void test_install_triesRepositoriesInOrderUntilOneHasTheArtifact() {
        // Pins the decision that install() tries each configured repository in turn: a
        // theme published only in the second repository must still install, and the
        // first repository's failure (a 404, the ordinary "not here" case) must not stop
        // the loop the way an InstallException does.
        final List<String> attemptedUrls = new ArrayList<>();
        final ThemeArtifactHelper helper = new ThemeArtifactHelper() {
            @Override
            protected String[] getRepositories() {
                return new String[] { "https://example.invalid/repo1/", "https://example.invalid/repo2/" };
            }

            @Override
            protected void download(final String url, final Path dest) {
                attemptedUrls.add(url);
                if (url.startsWith("https://example.invalid/repo1/")) {
                    throw new ThemeArtifactException("HTTP 404 for " + url);
                }
                try {
                    Files.write(dest, "zip-bytes".getBytes(StandardCharsets.UTF_8));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void verifyChecksum(final ThemeArtifact artifact, final Path file) {
                // see test_install_propagatesInstallExceptionAndStopsAtTheFirstRepository
            }
        };
        final AtomicInteger installCount = new AtomicInteger();
        helper.staticThemeInstaller = new StaticThemeInstaller() {
            @Override
            public void installZip(final InputStream zipStream) {
                installCount.incrementAndGet();
            }
        };
        helper.install("docuforge", "15.8.0");
        assertEquals(2, attemptedUrls.size(), "both repositories must be tried");
        assertTrue(attemptedUrls.get(0).startsWith("https://example.invalid/repo1/"), "repo1 must be tried first");
        assertTrue(attemptedUrls.get(1).startsWith("https://example.invalid/repo2/"), "repo2 must be tried second");
        assertEquals(1, installCount.get(), "installZip must run exactly once, against the artifact from repo2");
    }
}
