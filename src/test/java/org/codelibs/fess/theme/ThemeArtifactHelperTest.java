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
}
