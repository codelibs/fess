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

import java.util.Locale;

/**
 * Detects the running platform so artifact URLs can be built for it.
 */
public final class Platform {

    /** Operating system families this setup tool distinguishes. */
    public enum Os {
        /** Linux. */
        LINUX,
        /** Windows. */
        WINDOWS,
        /** macOS. OpenSearch publishes no official build for it. */
        MACOS,
        /** Anything else. */
        UNKNOWN
    }

    private Platform() {
    }

    /**
     * Maps an {@code os.name} value to an {@link Os}.
     *
     * @param osName the {@code os.name} system property value, may be null
     * @return the matching {@link Os}, or {@link Os#UNKNOWN}
     */
    public static Os osFrom(final String osName) {
        if (osName == null) {
            return Os.UNKNOWN;
        }
        final String name = osName.toLowerCase(Locale.ROOT);
        if (name.contains("win")) {
            return Os.WINDOWS;
        }
        if (name.contains("mac") || name.contains("darwin")) {
            return Os.MACOS;
        }
        if (name.contains("linux")) {
            return Os.LINUX;
        }
        return Os.UNKNOWN;
    }

    /**
     * Returns the {@link Os} of the running JVM.
     *
     * @return the current {@link Os}
     */
    public static Os current() {
        return osFrom(System.getProperty("os.name"));
    }

    /**
     * Maps an {@code os.arch} value to the token OpenSearch uses in its artifact names.
     *
     * @param osArch the {@code os.arch} system property value, may be null
     * @return {@code "x64"}, {@code "arm64"}, or {@code null} when unsupported
     */
    public static String archFrom(final String osArch) {
        if (osArch == null) {
            return null;
        }
        final String arch = osArch.toLowerCase(Locale.ROOT);
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            return "x64";
        }
        if ("aarch64".equals(arch) || "arm64".equals(arch)) {
            return "arm64";
        }
        return null;
    }

    /**
     * Returns the archive extension publishers use for the given OS. Windows gets zip; everything
     * else gets a gzipped tar. This holds for both OpenSearch and Node.js.
     *
     * @param os the operating system
     * @return {@code "zip"} on Windows, {@code "tar.gz"} otherwise
     */
    public static String archiveExt(final Os os) {
        return os == Os.WINDOWS ? "zip" : "tar.gz";
    }
}
