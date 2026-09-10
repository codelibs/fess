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

/**
 * Where Fess plugins are published.
 *
 * <p>A release lives in two places: the GitHub release of the plugin's own repository, which is
 * where a jar is downloaded from, and the Maven repository, which is where the version list and
 * the checksum come from and which serves the jar when GitHub does not. A snapshot lives only in
 * the Maven snapshot repository -- GitHub publishes releases, not development builds -- so a
 * development build of Fess needs both Maven trees.</p>
 *
 * @param release the release repository's group directory URL
 * @param snapshot the snapshot repository's group directory URL
 * @param github the GitHub organisation URL, blank when jars are not published there
 */
public record PluginSources(String release, String snapshot, String github) {

    /**
     * Returns the Maven repository that publishes a version.
     *
     * @param version the version
     * @return the snapshot repository for a snapshot, the release repository otherwise
     * @throws SetupException if that repository is not configured
     */
    public String repositoryOf(final String version) throws SetupException {
        return PluginRepository.isSnapshot(version) ? snapshotRepository() : releaseRepository();
    }

    /**
     * Returns the release repository.
     *
     * @return the release repository URL
     * @throws SetupException if it is not configured
     */
    public String releaseRepository() throws SetupException {
        return require(release, "release", "component.plugin.repository");
    }

    /**
     * Returns the snapshot repository.
     *
     * @return the snapshot repository URL
     * @throws SetupException if it is not configured
     */
    public String snapshotRepository() throws SetupException {
        return require(snapshot, "snapshot", "component.plugin.snapshot.repository");
    }

    /**
     * Reports whether a release repository is configured.
     *
     * @return true when one is set
     */
    public boolean hasRelease() {
        return release != null && !release.isBlank();
    }

    /**
     * Reports whether a snapshot repository is configured.
     *
     * @return true when one is set
     */
    public boolean hasSnapshot() {
        return snapshot != null && !snapshot.isBlank();
    }

    private static String require(final String url, final String kind, final String key) throws SetupException {
        if (url == null || url.isBlank()) {
            throw new SetupException("No " + kind + " repository for plugins is configured. Set " + key + " in fess-setup.properties.");
        }
        return url;
    }

    /**
     * Reports whether GitHub releases are configured as a download source.
     *
     * @return true when a GitHub organisation URL is set
     */
    public boolean hasGithub() {
        return github != null && !github.isBlank();
    }
}
