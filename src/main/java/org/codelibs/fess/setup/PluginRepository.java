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
package org.codelibs.fess.setup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Resolves Fess plugins in a Maven repository.
 *
 * <p>Mirrors what {@code org.codelibs.fess.helper.PluginHelper} does for the admin UI, because
 * that class lives in the webapp and cannot be reached from this jar: a plugin listing comes
 * from the repository's directory index, and a version comes from the artifact's
 * {@code maven-metadata.xml}.</p>
 */
public final class PluginRepository {

    /**
     * The artifact name prefixes Fess treats as plugins. Anything else in the group directory
     * -- {@code fess} itself, {@code fess-parent} -- is not installable.
     */
    private static final String[] PLUGIN_PREFIXES = { "fess-ds", "fess-theme", "fess-ingest", "fess-script", "fess-webapp",
            "fess-thumbnail", "fess-crawler", "fess-llm", "fess-storage", "fess-sso" };

    /**
     * Crawler artifacts that are libraries rather than plugins. PluginHelper hides these from
     * the admin list, and offering them here would let someone install a jar that cannot work.
     */
    private static final Set<String> EXCLUDED = Set.of("fess-crawler", "fess-crawler-db", "fess-crawler-db-h2", "fess-crawler-db-mysql",
            "fess-crawler-es", "fess-crawler-opensearch", "fess-crawler-lasta", "fess-crawler-parent", "fess-crawler-webdriver");

    private static final Pattern HREF = Pattern.compile("href=\"[^\"]*?([a-zA-Z0-9\\-]+)/?\"");

    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";

    private PluginRepository() {
    }

    /**
     * Returns the {@code maven-metadata.xml} URL for an artifact.
     *
     * @param repository the group directory URL
     * @param artifactId the artifact name
     * @return the metadata URL
     */
    public static String metadataUrl(final String repository, final String artifactId) {
        return base(repository) + artifactId + "/maven-metadata.xml";
    }

    /**
     * Returns the jar URL for an artifact version.
     *
     * <p>The file name is given separately from the version because a Maven repository stores a
     * snapshot under a directory named for the version and a file named for the build.</p>
     *
     * @param repository the group directory URL
     * @param artifactId the artifact name
     * @param version the version, which names the directory
     * @param fileVersion the version the file name carries
     * @return the jar URL
     */
    public static String jarUrl(final String repository, final String artifactId, final String version, final String fileVersion) {
        return base(repository) + artifactId + "/" + version + "/" + artifactId + "-" + fileVersion + ".jar";
    }

    /**
     * Returns the {@code maven-metadata.xml} URL of one version of an artifact, which is where a
     * snapshot repository records its builds.
     *
     * @param repository the group directory URL
     * @param artifactId the artifact name
     * @param version the version
     * @return the metadata URL
     */
    public static String versionMetadataUrl(final String repository, final String artifactId, final String version) {
        return base(repository) + artifactId + "/" + version + "/maven-metadata.xml";
    }

    /**
     * Returns the GitHub release asset URL for a plugin jar.
     *
     * <p>The tag is the one {@code maven-release-plugin} creates, {@code <artifact>-<version>},
     * and the asset is the jar under its Maven name.</p>
     *
     * @param github the GitHub organisation URL, for example {@code https://github.com/codelibs}
     * @param artifactId the plugin name, which is also its repository name
     * @param version the release version
     * @return the asset URL
     */
    public static String githubJarUrl(final String github, final String artifactId, final String version) {
        final String tag = artifactId + "-" + version;
        return base(github) + artifactId + "/releases/download/" + tag + "/" + tag + ".jar";
    }

    /**
     * Returns the Maven repository URL of a plugin jar, which is also where its checksum sits.
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @param version the version, which names the directory
     * @param fileVersion the version the file name carries
     * @return the jar URL in the Maven repository that publishes that version
     * @throws SetupException if that repository is not configured
     */
    public static String repositoryJarUrl(final PluginSources sources, final String artifactId, final String version,
            final String fileVersion) throws SetupException {
        return jarUrl(sources.repositoryOf(version), artifactId, version, fileVersion);
    }

    /**
     * Returns the URLs to try for a plugin jar, in order.
     *
     * <p>A release is taken from its GitHub release first and from the Maven repository when
     * that fails, which is the fallback the distribution relies on until every plugin publishes
     * its jars on GitHub. A snapshot is only ever in the Maven snapshot repository, so there is
     * nothing to fall back from.</p>
     *
     * @param sources where plugins are published
     * @param artifactId the plugin name
     * @param version the version, which names the directory
     * @param fileVersion the version the file name carries
     * @return the candidate URLs, most preferred first
     * @throws SetupException if the repository that publishes the version is not configured
     */
    public static List<String> jarUrls(final PluginSources sources, final String artifactId, final String version, final String fileVersion)
            throws SetupException {
        final String repositoryUrl = repositoryJarUrl(sources, artifactId, version, fileVersion);
        if (isSnapshot(version) || !sources.hasGithub()) {
            return List.of(repositoryUrl);
        }
        return List.of(githubJarUrl(sources.github(), artifactId, version), repositoryUrl);
    }

    /**
     * Returns the URL of the SHA-1 a Maven repository publishes beside an artifact.
     *
     * <p>SHA-1 rather than something longer because that, with MD5, is what these repositories
     * carry: {@code .sha256} and {@code .sha512} answer 404.</p>
     *
     * @param jarUrl the artifact URL in a Maven repository
     * @return the checksum URL
     */
    public static String checksumUrl(final String jarUrl) {
        return jarUrl + ".sha1";
    }

    /**
     * Merges two plugin listings into one, without duplicates, in name order.
     *
     * @param first the first listing
     * @param second the second listing
     * @return the merged names
     */
    public static List<String> mergeNames(final List<String> first, final List<String> second) {
        final Set<String> names = new TreeSet<>(first);
        names.addAll(second);
        return new ArrayList<>(names);
    }

    private static String base(final String repository) {
        return repository.endsWith("/") ? repository : repository + "/";
    }

    /**
     * Extracts the version list from a {@code maven-metadata.xml} document.
     *
     * @param xml the metadata document
     * @return the versions, in the order the document lists them
     * @throws SetupException if the document cannot be parsed
     */
    public static List<String> versionsFromMetadata(final String xml) throws SetupException {
        final NodeList nodes = parse(xml).getElementsByTagName("version");
        final List<String> versions = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            final String v = nodes.item(i).getTextContent().trim();
            if (!v.isEmpty()) {
                versions.add(v);
            }
        }
        return versions;
    }

    /**
     * Returns the file-name version of a snapshot build, read from the version's
     * {@code maven-metadata.xml}.
     *
     * <p>A deployed snapshot is published under a timestamped file name rather than the
     * {@code -SNAPSHOT} one, so {@code 15.9.0-SNAPSHOT} is really
     * {@code 15.9.0-20260903.015513-6} on disk. This reads the {@code <snapshot>} element the
     * way {@code PluginHelper} does for the admin UI. A repository that instead keeps the
     * {@code -SNAPSHOT} file -- a locally installed one, whose metadata says
     * {@code <localCopy>true</localCopy>} and carries no build number -- is reported as having
     * no build rather than guessed at.</p>
     *
     * @param xml the version's metadata document
     * @param version the snapshot version, for example {@code 15.9.0-SNAPSHOT}
     * @return the version the jar's file name carries
     * @throws SetupException if the document cannot be parsed or names no build
     */
    public static String snapshotFileVersion(final String xml, final String version) throws SetupException {
        final NodeList snapshots = parse(xml).getElementsByTagName("snapshot");
        String timestamp = null;
        String buildNumber = null;
        if (snapshots.getLength() > 0) {
            final NodeList children = snapshots.item(0).getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if ("timestamp".equalsIgnoreCase(child.getNodeName())) {
                    timestamp = child.getTextContent().trim();
                } else if ("buildNumber".equalsIgnoreCase(child.getNodeName())) {
                    buildNumber = child.getTextContent().trim();
                }
            }
        }
        if (timestamp == null || timestamp.isEmpty() || buildNumber == null || buildNumber.isEmpty()) {
            throw new SetupException("No snapshot build is published for " + version
                    + ". Name a version with --version <version>, or install a release instead.");
        }
        return version.replace("SNAPSHOT", timestamp + "-" + buildNumber);
    }

    private static Document parse(final String xml) throws SetupException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new SetupException("Failed to read maven-metadata.xml", e);
        }
    }

    /**
     * Reports whether a version is a Maven snapshot.
     *
     * @param version the version
     * @return true when the version ends with {@code -SNAPSHOT}
     */
    public static boolean isSnapshot(final String version) {
        return version != null && version.endsWith(SNAPSHOT_SUFFIX);
    }

    /**
     * Picks the highest version matching the product version, comparing numerically so that
     * 15.9.10 sorts above 15.9.9.
     *
     * <p>Releases and snapshots are ranked separately rather than in one order, because a
     * development build wants the snapshot of its own line even once that line has a release,
     * and a released build must never be offered a snapshot that happens to sort higher. A
     * development build falls back to a release when the plugin has no snapshot yet, which is
     * what makes {@code install plugin} work on a 15.9.0-SNAPSHOT Fess before any 15.9 snapshot
     * of that plugin has been deployed. A released build has no fallback: a snapshot is
     * installed there only when {@code --version} names it.</p>
     *
     * @param versions the available versions
     * @param productVersion the major.minor this Fess accepts, for example {@code 15.9}
     * @param preferSnapshot whether a snapshot outranks a release
     * @return the selected version
     * @throws SetupException when no version matches
     */
    public static String selectVersion(final List<String> versions, final String productVersion, final boolean preferSnapshot)
            throws SetupException {
        final String prefix = productVersion + ".";
        final List<String> snapshots = new ArrayList<>();
        final List<String> releases = new ArrayList<>();
        for (final String v : versions) {
            if (v.equals(productVersion) || v.startsWith(prefix)) {
                (isSnapshot(v) ? snapshots : releases).add(v);
            }
        }
        // Asymmetric on purpose. A development build falls back to a release, because a plugin
        // whose line has no snapshot yet still has to be installable; a released build has no
        // fallback at all, because it must never install a snapshot it was not asked for by
        // name. A --version names one explicitly and does not come through here.
        final List<String> matching = preferSnapshot && !snapshots.isEmpty() ? snapshots : releases;
        if (matching.isEmpty()) {
            throw new SetupException("No release matches Fess " + productVersion + ". Available versions: " + String.join(", ", versions));
        }
        matching.sort(Comparator.comparing(PluginRepository::sortKey));
        return matching.get(matching.size() - 1);
    }

    private static String sortKey(final String version) {
        final StringBuilder key = new StringBuilder();
        for (final String part : version.split("[.\\-]")) {
            if (part.chars().allMatch(Character::isDigit) && !part.isEmpty()) {
                key.append(String.format("%08d", Integer.parseInt(part)));
            } else {
                key.append(part);
            }
            key.append('.');
        }
        return key.toString();
    }

    /**
     * Extracts installable plugin names from a repository directory listing.
     *
     * @param html the directory index
     * @return the plugin names, in listing order, without duplicates
     */
    public static List<String> namesFromListing(final String html) {
        final Set<String> names = new LinkedHashSet<>();
        final Matcher matcher = HREF.matcher(html);
        while (matcher.find()) {
            final String name = matcher.group(1);
            if (EXCLUDED.contains(name)) {
                continue;
            }
            for (final String prefix : PLUGIN_PREFIXES) {
                if (name.startsWith(prefix + "-")) {
                    names.add(name);
                    break;
                }
            }
        }
        return new ArrayList<>(names);
    }
}
