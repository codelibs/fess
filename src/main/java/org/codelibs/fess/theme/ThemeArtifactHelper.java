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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Discovers static themes published in a Maven-style repository tree.
 *
 * <p>A theme is published as
 * {@code <repository>/<name>/<version>/<name>-<version>.zip} with a
 * {@code maven-metadata.xml} next to the version directories. Theme names come
 * from {@code theme-index.txt} at the root of the repository, and versions from
 * each theme's own metadata file. Neither is a directory listing: those are
 * generated on a schedule, so the themes tree answers 403 until that runs and
 * the listing for the parent group already lags what is published. Both files
 * are served directly and reflect a publish immediately.</p>
 *
 * <p>This helper deliberately does not reuse {@link org.codelibs.fess.helper.PluginHelper}:
 * themes live in their own namespace, carry no {@code fess-theme} prefix, and the
 * plugin installer's THEME branch handles JAR themes, which discard a static
 * theme's files.</p>
 */
public class ThemeArtifactHelper {

    private static final Logger logger = LogManager.getLogger(ThemeArtifactHelper.class);

    /** Metadata file name published next to a theme's version directories. */
    protected static final String METADATA_FILE = "maven-metadata.xml";

    /** Theme directory names obey the same pattern the manifest enforces. */
    protected static final Pattern NAME_PATTERN = ThemeManifest.NAME_PATTERN;

    /** Theme version strings obey the same pattern the manifest enforces. */
    protected static final Pattern VERSION_PATTERN = ThemeManifest.SEMVER_PATTERN;

    /**
     * The file naming every published theme, one per line, at the root of a theme
     * repository.
     *
     * <p>Not a directory listing. The server generates those on a schedule, so the themes
     * tree answers 403 until it has and the listing for the parent group is already
     * behind; a catalogue built on it reports that no themes exist. The deploy writes this
     * file, so it is there as soon as anything is published.</p>
     */
    protected static final String INDEX_FILE = "theme-index.txt";

    /**
     * Thrown when a repository's index cannot be read, so that Guava never caches that
     * outcome as "no themes here" -- see {@link #artifactCache}. Carries the reason (the
     * HTTP status, or the underlying exception as its cause) so the single warning logged
     * where this is caught can say why, not just that it failed.
     */
    protected static final class RepositoryUnreadableException extends Exception {

        private static final long serialVersionUID = 1L;

        /**
         * @param message the reason, e.g. an HTTP status
         */
        protected RepositoryUnreadableException(final String message) {
            super(message);
        }

        /**
         * @param message the reason
         * @param cause the underlying exception
         */
        protected RepositoryUnreadableException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Per-repository cache of discovered artifacts.
     *
     * <p>The loader must throw rather than return an empty list when the repository itself
     * could not be read (a 500, a timeout, a DNS failure): {@link CacheLoader#load} declares
     * {@code throws Exception} precisely so a failed load is not cached, and {@link #getAvailableArtifacts()}
     * already catches per repository and logs a warning. Returning an empty list instead would
     * be cached for the full {@code expireAfterWrite} below, making a repository that recovered
     * seconds after a single failed request look empty to the admin screen for five minutes --
     * the exact failure mode publishing {@link #INDEX_FILE} exists to avoid.</p>
     */
    protected final LoadingCache<String, List<ThemeArtifact>> artifactCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<ThemeArtifact>>() {
                @Override
                public List<ThemeArtifact> load(final String repositoryUrl) throws RepositoryUnreadableException {
                    return loadFromRepository(repositoryUrl);
                }
            });

    /**
     * Default constructor.
     */
    public ThemeArtifactHelper() {
        // for DI
    }

    /**
     * A theme artifact published in a repository.
     */
    public static class ThemeArtifact {
        private final String name;
        private final String version;
        private final String url;

        /**
         * Creates an artifact.
         *
         * @param name the theme name
         * @param version the theme version
         * @param url the absolute URL of the ZIP
         */
        public ThemeArtifact(final String name, final String version, final String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        /**
         * Returns the theme name.
         *
         * @return the theme name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the theme version.
         *
         * @return the theme version
         */
        public String getVersion() {
            return version;
        }

        /**
         * Returns the absolute URL of the theme ZIP.
         *
         * @return the artifact URL
         */
        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "ThemeArtifact [name=" + name + ", version=" + version + ", url=" + url + "]";
        }
    }

    /**
     * Returns every theme artifact whose version matches the running Fess line,
     * across all configured repositories.
     *
     * @return the available artifacts, never null
     */
    public List<ThemeArtifact> getAvailableArtifacts() {
        final List<ThemeArtifact> list = new ArrayList<>();
        for (final String repositoryUrl : getRepositories()) {
            try {
                list.addAll(artifactCache.get(repositoryUrl));
            } catch (final Exception e) {
                logger.warn("Failed to read the theme repository: {}", repositoryUrl, e);
            }
        }
        return list;
    }

    /**
     * Returns the configured repository URLs.
     *
     * @return the repository URLs
     */
    protected String[] getRepositories() {
        final String value = ComponentUtil.getFessConfig().getThemeRepositories();
        if (StringUtil.isBlank(value)) {
            return new String[0];
        }
        final String[] urls = value.split(",");
        for (int i = 0; i < urls.length; i++) {
            urls[i] = urls[i].trim();
        }
        return urls;
    }

    /**
     * Reads one repository and returns the artifacts it publishes for the
     * running Fess line.
     *
     * @param repositoryUrl the repository URL
     * @return the artifacts found there
     * @throws RepositoryUnreadableException if the index itself could not be read; a single
     *         theme's metadata being unreadable does not throw, it is skipped (see below)
     */
    protected List<ThemeArtifact> loadFromRepository(final String repositoryUrl) throws RepositoryUnreadableException {
        final List<ThemeArtifact> list = new ArrayList<>();
        final String indexUrl = join(repositoryUrl, INDEX_FILE);
        final String index = readIndexOrThrow(indexUrl);
        for (final String name : parseIndex(index)) {
            final String metadataUrl = join(repositoryUrl, name) + "/" + METADATA_FILE;
            final String metadata = getContent(metadataUrl);
            if (metadata == null) {
                // Unlike the index above, one theme's metadata being momentarily unreadable
                // should not fail the whole catalogue -- skip it and keep the rest.
                logger.warn("No metadata for theme: {}", name);
                continue;
            }
            for (final String version : parseVersions(metadata)) {
                if (ComponentUtil.getFessConfig().isTargetPluginVersion(version)) {
                    list.add(new ThemeArtifact(name, version, buildArtifactUrl(repositoryUrl, name, version)));
                }
            }
        }
        return list;
    }

    /**
     * Extracts the theme names from the published index.
     *
     * <p>Every line has to be a theme name. That is what tells an index from a proxy page
     * or an error document that answers 200, which would otherwise be read as a catalogue;
     * a body with any other line yields nothing rather than a partial list, so a broken
     * repository is reported as unreadable instead of as nearly empty.</p>
     *
     * <p>The same parsing exists in {@code org.codelibs.fess.setup.ThemeInstaller} for
     * {@code bin/fess-setup}. It cannot be shared: {@code packagingExcludes} in pom.xml
     * keeps {@code WEB-INF/classes/org/codelibs/fess/setup/**} out of the war, so that
     * class is not on the runtime class path. What both sides follow is
     * {@link ThemeManifest#NAME_PATTERN}, which lives here in core.</p>
     *
     * @param text the index
     * @return the theme names in file order without duplicates, empty when the body is
     *         not an index
     */
    protected List<String> parseIndex(final String text) {
        if (StringUtil.isBlank(text)) {
            return Collections.emptyList();
        }
        final List<String> names = new ArrayList<>();
        for (final String raw : text.split("\n")) {
            final String name = raw.strip();
            if (name.isEmpty()) {
                continue;
            }
            if (!NAME_PATTERN.matcher(name).matches()) {
                logger.warn("Not a theme index: it has a line that is not a theme name.");
                return Collections.emptyList();
            }
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Extracts the versions listed in a {@code maven-metadata.xml} document.
     *
     * <p>Each {@code <version>} is checked against {@link ThemeManifest#SEMVER_PATTERN} before
     * it is trusted: the metadata is remote, untrusted content, and an unchecked value reaches
     * {@link #buildArtifactUrl} unescaped. Unlike {@link #parseIndex}, one malformed version does
     * not fail the whole document -- it is dropped and the well-formed versions are kept, because
     * a repository may legitimately publish a version this Fess does not understand.</p>
     *
     * @param xml the metadata document
     * @return the well-formed versions in document order, never null
     */
    protected List<String> parseVersions(final String xml) {
        if (StringUtil.isBlank(xml)) {
            return Collections.emptyList();
        }
        final List<String> versions = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(xml.getBytes(Constants.UTF_8_CHARSET))) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(is);
            final NodeList nodeList = document.getElementsByTagName("version");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final String version = nodeList.item(i).getTextContent();
                if (StringUtil.isBlank(version)) {
                    continue;
                }
                final String trimmed = version.trim();
                if (!VERSION_PATTERN.matcher(trimmed).matches()) {
                    logger.warn("Not a theme version, dropped: {}", trimmed);
                    continue;
                }
                versions.add(trimmed);
            }
        } catch (final Exception e) {
            logger.warn("Failed to parse theme metadata", e);
            return Collections.emptyList();
        }
        return versions;
    }

    /**
     * Builds the absolute URL of a theme ZIP.
     *
     * @param repositoryUrl the repository URL
     * @param name the theme name
     * @param version the theme version
     * @return the artifact URL
     */
    protected String buildArtifactUrl(final String repositoryUrl, final String name, final String version) {
        return join(repositoryUrl, name) + "/" + version + "/" + name + "-" + version + ".zip";
    }

    private static String join(final String base, final String segment) {
        return base.endsWith("/") ? base + segment : base + "/" + segment;
    }

    /**
     * Fetches a text resource, returning {@code null} when it is not available.
     *
     * <p>Logs at {@code debug}: for the per-theme metadata path this is called from, a miss
     * is reported one level up at {@code warn} ("No metadata for theme" in
     * {@link #loadFromRepository}), a condition bounded by the theme count. {@link #readIndexOrThrow}
     * exists separately for the index, which needs to say why it failed, not just that it did.</p>
     *
     * @param url the URL to read
     * @return the body, or null
     */
    protected String getContent(final String url) {
        try (CurlResponse response = createCurlRequest(url).execute()) {
            if (response.getHttpStatusCode() != 200) {
                logger.debug("HTTP {} for {}", response.getHttpStatusCode(), url);
                return null;
            }
            return response.getContentAsString();
        } catch (final Exception e) {
            logger.debug("Failed to read {}", url, e);
            return null;
        }
    }

    /**
     * Fetches the repository's index, or throws with the reason it could not be read.
     *
     * <p>Used only for the index. Unlike {@link #getContent}, which swallows a failure to
     * {@code null} for the per-theme metadata path, {@link #artifactCache} deliberately keeps
     * a failed index load out of the cache (see its javadoc), so this runs again on every call
     * while a repository is down. The single warning logged where this is caught needs to name
     * the reason -- the HTTP status, or the underlying exception as its cause -- not just that
     * something failed, and logging it again here at this level would only repeat it.</p>
     *
     * @param url the index URL
     * @return the index body
     * @throws RepositoryUnreadableException if it could not be read
     */
    protected String readIndexOrThrow(final String url) throws RepositoryUnreadableException {
        try (CurlResponse response = createCurlRequest(url).execute()) {
            if (response.getHttpStatusCode() != 200) {
                throw new RepositoryUnreadableException("HTTP " + response.getHttpStatusCode() + " for " + url);
            }
            return response.getContentAsString();
        } catch (final RepositoryUnreadableException e) {
            throw e;
        } catch (final Exception e) {
            throw new RepositoryUnreadableException("Failed to read " + url, e);
        }
    }

    /**
     * Creates a GET request honouring the configured HTTP proxy.
     *
     * @param url the URL to request
     * @return the request
     */
    protected CurlRequest createCurlRequest(final String url) {
        final CurlRequest request = Curl.get(url);
        final Proxy proxy = ComponentUtil.getFessConfig().getHttpProxy();
        if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
            request.proxy(proxy);
        }
        return request;
    }
}
