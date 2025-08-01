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
package org.codelibs.fess.helper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.opensearch.script.Script;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for language detection.
 */
public class LanguageHelper {
    private static final Logger logger = LogManager.getLogger(LanguageHelper.class);

    /** An array of language fields. */
    protected String[] langFields;

    /** An array of supported languages. */
    protected String[] supportedLanguages;

    /** The language detector. */
    protected LanguageDetector detector;

    /** The maximum text length for language detection. */
    protected int maxTextLength;

    /**
     * Default constructor.
     */
    public LanguageHelper() {
        // do nothing
    }

    /**
     * Initializes the helper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        langFields = fessConfig.getIndexerLanguageFieldsAsArray();
        supportedLanguages = fessConfig.getSupportedLanguagesAsArray();
        maxTextLength = fessConfig.getIndexerLanguageDetectLengthAsInteger();
    }

    /**
     * Updates a document with language information.
     *
     * @param doc The document to update.
     */
    public void updateDocument(final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String language = getSupportedLanguage(DocumentUtil.getValue(doc, fessConfig.getIndexFieldLang(), String.class));
        if (language == null) {
            for (final String f : langFields) {
                if (doc.containsKey(f)) {
                    language = detectLanguage(DocumentUtil.getValue(doc, f, String.class));
                    if (language != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("set {} to lang field", language);
                        }
                        doc.put(fessConfig.getIndexFieldLang(), language);
                        break;
                    }
                }
            }
            if (language == null) {
                return;
            }
        }

        for (final String f : langFields) {
            final String lf = f + "_" + language;
            if (doc.containsKey(f) && !doc.containsKey(lf)) {
                doc.put(lf, doc.get(f));
                if (logger.isDebugEnabled()) {
                    logger.debug("add {} field", lf);
                }
            }
        }
    }

    /**
     * Detects the language of a text.
     *
     * @param text The text to detect the language from.
     * @return The detected language.
     */
    protected String detectLanguage(final String text) {
        if (StringUtil.isBlank(text)) {
            return null;
        }
        final String target = getDetectText(text);
        final LanguageResult result = detector.detect(target);
        if (logger.isDebugEnabled()) {
            logger.debug("detected lang:{}({}) from {}", result, result.getRawScore(), target);
        }
        return getSupportedLanguage(result.getLanguage());
    }

    /**
     * Returns the text to be used for language detection.
     *
     * @param text The original text.
     * @return The text for language detection.
     */
    protected String getDetectText(final String text) {
        final String result;
        if (text.length() <= maxTextLength) {
            result = text;
        } else {
            result = text.substring(0, maxTextLength);
        }
        return result.replaceAll("\\s+", " ");
    }

    /**
     * Returns the supported language for a given language.
     *
     * @param lang The language to check.
     * @return The supported language, or null if not supported.
     */
    protected String getSupportedLanguage(final String lang) {
        if (StringUtil.isBlank(lang)) {
            return null;
        }
        for (final String l : supportedLanguages) {
            if (l.equals(lang)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Sets the language detector.
     *
     * @param detector The language detector.
     */
    public void setDetector(final LanguageDetector detector) {
        this.detector = detector;
    }

    /**
     * Creates a script for updating a document with language information.
     *
     * @param doc The document.
     * @param code The script code.
     * @return The script.
     */
    public Script createScript(final Map<String, Object> doc, final String code) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(code);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String language = DocumentUtil.getValue(doc, fessConfig.getIndexFieldLang(), String.class);
        if (StringUtil.isNotBlank(language)) {
            for (final String f : langFields) {
                buf.append(";ctx._source.").append(f).append('_').append(language).append("=ctx._source.").append(f);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("update script: {}", buf);
        }
        return new Script(buf.toString());
    }

    /**
     * Returns the reindex script source.
     *
     * @return The reindex script source.
     */
    public String getReindexScriptSource() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String langField = fessConfig.getIndexFieldLang();
        final String code = Arrays.stream(langFields).map(s -> "ctx._source['" + s + "_'+ctx._source." + langField + "]=ctx._source." + s)
                .collect(Collectors.joining(";"));
        if (logger.isDebugEnabled()) {
            logger.debug("reindex script: {}", code);
        }
        return "if(ctx._source." + langField + "!=null){" + code + "}";
    }

}
