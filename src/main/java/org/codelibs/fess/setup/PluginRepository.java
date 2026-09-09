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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
    private static final String[] PLUGIN_PREFIXES =
            { "fess-ds", "fess-theme", "fess-ingest", "fess-script", "fess-webapp", "fess-thumbnail", "fess-crawler", "fess-llm" };

    /**
     * Crawler artifacts that are libraries rather than plugins. PluginHelper hides these from
     * the admin list, and offering them here would let someone install a jar that cannot work.
     */
    private static final Set<String> EXCLUDED = Set.of("fess-crawler", "fess-crawler-db", "fess-crawler-db-h2", "fess-crawler-db-mysql",
            "fess-crawler-es", "fess-crawler-opensearch", "fess-crawler-lasta", "fess-crawler-parent", "fess-crawler-playwright",
            "fess-crawler-webdriver");

    private static final Pattern HREF = Pattern.compile("href=\"[^\"]*?([a-zA-Z0-9\\-]+)/?\"");

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
     * @param repository the group directory URL
     * @param artifactId the artifact name
     * @param version the version
     * @return the jar URL
     */
    public static String jarUrl(final String repository, final String artifactId, final String version) {
        return base(repository) + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
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
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            final NodeList nodes = document.getElementsByTagName("version");
            final List<String> versions = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                final String v = nodes.item(i).getTextContent().trim();
                if (!v.isEmpty()) {
                    versions.add(v);
                }
            }
            return versions;
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new SetupException("Failed to read maven-metadata.xml", e);
        }
    }

    /**
     * Picks the highest version matching the product version, comparing numerically so that
     * 15.9.10 sorts above 15.9.9.
     *
     * @param versions the available versions
     * @param productVersion the major.minor this Fess accepts, for example {@code 15.9}
     * @return the selected version
     * @throws SetupException when no version matches
     */
    public static String selectVersion(final List<String> versions, final String productVersion) throws SetupException {
        final String prefix = productVersion + ".";
        final List<String> matching = new ArrayList<>();
        for (final String v : versions) {
            if (v.equals(productVersion) || v.startsWith(prefix)) {
                matching.add(v);
            }
        }
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
