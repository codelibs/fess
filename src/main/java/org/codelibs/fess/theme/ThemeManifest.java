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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Parsed representation of a static theme's {@code theme.yml} manifest.
 *
 * <p>Use {@link #parse(InputStream)} to build an instance; the parser performs
 * structural and value validation and throws {@link ThemeManifestException} on
 * any failure.</p>
 */
public final class ThemeManifest {

    public static final String CURRENT_API_VERSION = "fess.codelibs.org/v1";
    public static final String KIND = "StaticTheme";
    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9_-]{0,63}$");
    public static final Pattern SEMVER_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[A-Za-z0-9.-]+)?$");

    private final String apiVersion;
    private final String kind;
    private final String name;
    private final String displayName;
    private final String version;
    private final String author;
    private final String description;
    private final String license;
    private final String homepage;
    private final String minFessVersion;
    private final List<String> supportedLocales;
    private final String entry;
    private final boolean spaFallback;

    private ThemeManifest(final Builder b) {
        this.apiVersion = b.apiVersion;
        this.kind = b.kind;
        this.name = b.name;
        this.displayName = b.displayName;
        this.version = b.version;
        this.author = b.author;
        this.description = b.description;
        this.license = b.license;
        this.homepage = b.homepage;
        this.minFessVersion = b.minFessVersion;
        this.supportedLocales = b.supportedLocales == null ? List.of() : List.copyOf(b.supportedLocales);
        this.entry = b.entry == null ? "index.html" : b.entry;
        this.spaFallback = b.spaFallback == null ? true : b.spaFallback;
    }

    private static final int MAX_FIELD_LENGTH = 4096;

    @SuppressWarnings("unchecked")
    public static ThemeManifest parse(final InputStream in) {
        final Object rawObj;
        try {
            final LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setCodePointLimit(1_000_000);
            loaderOptions.setMaxAliasesForCollections(50);
            // theme.yml is a flat document; a nesting depth of 20 is generous while
            // defending against deeply-nested bomb payloads (SnakeYAML 2.0+).
            loaderOptions.setNestingDepthLimit(20);
            rawObj = new Yaml(new SafeConstructor(loaderOptions)).load(in);
        } catch (final RuntimeException e) {
            throw new ThemeManifestException("Failed to parse theme.yml", e);
        }
        if (rawObj == null) {
            throw new ThemeManifestException("theme.yml is empty");
        }
        if (!(rawObj instanceof Map)) {
            throw new ThemeManifestException("theme.yml root must be a mapping");
        }
        final Map<String, Object> raw = (Map<String, Object>) rawObj;
        final Builder b = new Builder();
        b.apiVersion = str(raw, "apiVersion");
        b.kind = str(raw, "kind");
        b.name = str(raw, "name");
        b.displayName = checkFieldLength("displayName", str(raw, "displayName"));
        b.version = str(raw, "version");
        b.author = checkFieldLength("author", str(raw, "author"));
        b.description = checkFieldLength("description", str(raw, "description"));
        b.license = checkFieldLength("license", str(raw, "license"));
        b.homepage = checkFieldLength("homepage", str(raw, "homepage"));
        b.minFessVersion = checkFieldLength("minFessVersion", str(raw, "minFessVersion"));
        final Object loc = raw.get("supportedLocales");
        if (loc instanceof List) {
            final List<String> locales = ((List<Object>) loc).stream().map(String::valueOf).toList();
            for (int i = 0; i < locales.size(); i++) {
                checkFieldLength("supportedLocales[" + i + "]", locales.get(i));
            }
            b.supportedLocales = locales;
        } else if (loc instanceof String s) {
            b.supportedLocales = List.of(checkFieldLength("supportedLocales", s));
        }
        b.entry = checkFieldLength("entry", str(raw, "entry"));
        final Object spa = raw.get("spaFallback");
        if (spa instanceof Boolean) {
            b.spaFallback = (Boolean) spa;
        }
        final ThemeManifest m = new ThemeManifest(b);
        m.validate();
        return m;
    }

    private static String checkFieldLength(final String fieldName, final String value) {
        if (value != null && value.length() > MAX_FIELD_LENGTH) {
            throw new ThemeManifestException("Field '" + fieldName + "' exceeds maximum length of " + MAX_FIELD_LENGTH + " characters");
        }
        return value;
    }

    private static String str(final Map<String, Object> raw, final String key) {
        final Object v = raw.get(key);
        return v == null ? null : v.toString();
    }

    private void validate() {
        if (!CURRENT_API_VERSION.equals(apiVersion)) {
            throw new ThemeManifestException("Unsupported apiVersion: " + apiVersion);
        }
        if (!KIND.equals(kind)) {
            throw new ThemeManifestException("Unsupported kind: " + kind);
        }
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new ThemeManifestException("Invalid theme name: " + name);
        }
        if (displayName == null || displayName.isBlank()) {
            throw new ThemeManifestException("displayName is required");
        }
        if (version == null || !SEMVER_PATTERN.matcher(version).matches()) {
            throw new ThemeManifestException("Invalid semver version: " + version);
        }
        if (isUnsafeEntry(entry)) {
            throw new ThemeManifestException("entry must be a relative path inside the theme: " + entry);
        }
    }

    /**
     * Returns {@code true} when the {@code entry} value is considered unsafe.
     * Rejects: path traversal ({@code ..}), absolute paths, backslashes,
     * null bytes, colon (Windows drive separator), and Windows drive letter patterns.
     */
    private static boolean isUnsafeEntry(final String e) {
        if (e == null) {
            return false; // null entry will default to index.html
        }
        if (e.startsWith("/")) {
            return true;
        }
        if (e.contains("..")) {
            return true;
        }
        if (e.contains("\\")) {
            return true;
        }
        if (e.contains("\0")) {
            return true;
        }
        if (e.contains(":")) {
            return true;
        }
        // Windows drive letter: e.g. "C:..." but already caught by colon check above
        if (e.matches("^[A-Za-z]:.*")) {
            return true;
        }
        return false;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getMinFessVersion() {
        return minFessVersion;
    }

    public List<String> getSupportedLocales() {
        return supportedLocales;
    }

    public String getEntry() {
        return entry;
    }

    public boolean isSpaFallback() {
        return spaFallback;
    }

    private static final class Builder {
        String apiVersion;
        String kind;
        String name;
        String displayName;
        String version;
        String author;
        String description;
        String license;
        String homepage;
        String minFessVersion;
        List<String> supportedLocales;
        String entry;
        Boolean spaFallback;
    }
}
