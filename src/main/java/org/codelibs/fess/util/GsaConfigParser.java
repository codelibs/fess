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
package org.codelibs.fess.util;

import static org.codelibs.core.stream.StreamUtil.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.GsaConfigException;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.dbflute.optional.OptionalEntity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for Google Search Appliance (GSA) configuration files.
 * This SAX-based parser reads GSA XML configuration files and converts them into
 * Fess configuration objects including web crawling configurations, file crawling
 * configurations, and label types for access control.
 *
 * <p>The parser handles the following GSA configuration elements:
 * <ul>
 * <li>Collections with good/bad URL patterns</li>
 * <li>Global parameters including start URLs and filtering rules</li>
 * <li>User agent settings</li>
 * <li>URL pattern matching with regular expressions and contains filters</li>
 * </ul>
 *
 */
public class GsaConfigParser extends DefaultHandler {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(GsaConfigParser.class);

    /** Prefix for regular expression patterns. */
    public static final String REGEXP = "regexp:";

    /** Prefix for case-sensitive regular expression patterns. */
    public static final String REGEXP_CASE = "regexpCase:";

    /** Prefix for case-insensitive regular expression patterns. */
    public static final String REGEXP_IGNORE_CASE = "regexpIgnoreCase:";

    /** Prefix for contains-based string matching patterns. */
    public static final String CONTAINS = "contains:";

    /** XML element name for collections container. */
    protected static final String COLLECTIONS = "collections";

    /** XML element name for individual collection. */
    protected static final String COLLECTION = "collection";

    /** XML element name for global parameters container. */
    protected static final String GLOBALPARAMS = "globalparams";

    /** XML element name for start URLs configuration. */
    protected static final String START_URLS = "start_urls";

    /** XML element name for good (included) URLs configuration. */
    protected static final String GOOD_URLS = "good_urls";

    /** XML element name for bad (excluded) URLs configuration. */
    protected static final String BAD_URLS = "bad_urls";

    /** Array of supported web protocols for URL classification. */
    protected String[] webProtocols = { "http:", "https:" };

    /** Array of supported file protocols for URL classification. */
    protected String[] fileProtocols = { "file:", "smb:", "smb1:", "ftp:", "storage:" };

    /** Queue to track the current XML element hierarchy during parsing. */
    protected LinkedList<String> tagQueue;

    /** List to store parsed label types for access control. */
    protected List<LabelType> labelList;

    /** Current label type being processed during parsing. */
    protected LabelType labelType;

    /** Map to store global configuration parameters. */
    protected Map<String, String> globalParams = new HashMap<>();

    /** Generated web crawling configuration from parsed GSA config. */
    protected WebConfig webConfig = null;

    /** Generated file crawling configuration from parsed GSA config. */
    protected FileConfig fileConfig = null;

    /** Buffer to accumulate character data between XML tags. */
    protected StringBuilder textBuf = new StringBuilder(1000);

    /** User agent string to be used for web crawling. */
    protected String userAgent = "gsa-crawler";

    /**
     * Default constructor for GsaConfigParser.
     */
    public GsaConfigParser() {
        super();
    }

    /**
     * Parses a GSA configuration XML file from the given input source.
     * This method configures a secure SAX parser and processes the XML content
     * to extract configuration information for web and file crawling.
     *
     * @param is the input source containing the GSA configuration XML
     * @throws GsaConfigException if parsing fails due to XML format issues or other errors
     */
    public void parse(final InputSource is) {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            final SAXParser parser = factory.newSAXParser();
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);
            parser.parse(is, this);
        } catch (final Exception e) {
            throw new GsaConfigException("Failed to parse XML file.", e);
        }
    }

    /**
     * SAX event handler called at the beginning of document parsing.
     * Initializes internal data structures for processing the GSA configuration.
     *
     * @throws SAXException if a SAX error occurs during initialization
     */
    @Override
    public void startDocument() throws SAXException {
        tagQueue = new LinkedList<>();
        labelList = new ArrayList<>();
        labelType = null;
    }

    /**
     * SAX event handler called at the end of document parsing.
     * Cleans up internal data structures used during parsing.
     *
     * @throws SAXException if a SAX error occurs during cleanup
     */
    @Override
    public void endDocument() throws SAXException {
        globalParams.clear();
        tagQueue.clear();
    }

    /**
     * SAX event handler called when an XML start element is encountered.
     * Processes collection definitions and tracks the element hierarchy.
     *
     * @param uri the namespace URI, or empty string if none
     * @param localName the local name without prefix, or empty string if namespace processing is not performed
     * @param qName the qualified name with prefix, or empty string if qualified names are not available
     * @param attributes the attributes attached to the element
     * @throws SAXException if a SAX error occurs or if the XML format is invalid
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        if (logger.isDebugEnabled()) {
            logger.debug("Start Element: {}", qName);
        }
        if (tagQueue.isEmpty() && !"eef".equalsIgnoreCase(qName)) {
            throw new GsaConfigException("Invalid format.");
        }
        if (COLLECTION.equalsIgnoreCase(qName) && COLLECTIONS.equalsIgnoreCase(tagQueue.peekLast())) {
            final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
            final String name = attributes.getValue("Name");
            labelType = new LabelType();
            labelType.setName(name);
            labelType.setValue(name);
            labelType.setPermissions(new String[] { "Rguest" });
            labelType.setCreatedBy(Constants.SYSTEM_USER);
            labelType.setCreatedTime(now);
            labelType.setUpdatedBy(Constants.SYSTEM_USER);
            labelType.setUpdatedTime(now);
        }
        tagQueue.offer(qName);
    }

    /**
     * SAX event handler called when an XML end element is encountered.
     * Processes the accumulated text content and creates appropriate configuration objects
     * based on the element type (good_urls, bad_urls, start_urls, etc.).
     *
     * @param uri the namespace URI, or empty string if none
     * @param localName the local name without prefix, or empty string if namespace processing is not performed
     * @param qName the qualified name with prefix, or empty string if qualified names are not available
     * @throws SAXException if a SAX error occurs during processing
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (logger.isDebugEnabled()) {
            logger.debug("End Element: {}", qName);
        }
        if (GOOD_URLS.equalsIgnoreCase(qName)) {
            if (labelType != null) {
                labelType.setIncludedPaths(parseFilterPaths(textBuf.toString(), true, true));
            } else if (GLOBALPARAMS.equalsIgnoreCase(tagQueue.get(tagQueue.size() - 2))) {
                globalParams.put(GOOD_URLS, textBuf.toString());
            }
        } else if (BAD_URLS.equalsIgnoreCase(qName)) {
            if (labelType != null) {
                labelType.setExcludedPaths(parseFilterPaths(textBuf.toString(), true, true));
            } else if (GLOBALPARAMS.equalsIgnoreCase(tagQueue.get(tagQueue.size() - 2))) {
                globalParams.put(BAD_URLS, textBuf.toString());
            }
        } else if (START_URLS.equalsIgnoreCase(qName) && GLOBALPARAMS.equalsIgnoreCase(tagQueue.get(tagQueue.size() - 2))) {
            globalParams.put(START_URLS, textBuf.toString());
        } else if (labelType != null && COLLECTION.equalsIgnoreCase(qName)) {
            labelList.add(labelType);
            labelType = null;
        } else if (GLOBALPARAMS.equalsIgnoreCase(qName)) {
            final Object startUrls = globalParams.get(START_URLS);
            if (startUrls != null) {
                final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
                final List<String> urlList = split(startUrls.toString(), "\n")
                        .get(stream -> stream.map(String::trim).filter(StringUtil::isNotBlank).collect(Collectors.toList()));

                final String webUrls = urlList.stream().filter(s -> Arrays.stream(webProtocols).anyMatch(p -> s.startsWith(p)))
                        .collect(Collectors.joining("\n"));
                if (StringUtil.isNotBlank(webUrls)) {
                    webConfig = new WebConfig();
                    webConfig.setName("Default");
                    webConfig.setAvailable(true);
                    webConfig.setBoost(1.0f);
                    webConfig.setConfigParameter(StringUtil.EMPTY);
                    webConfig.setIntervalTime(1000);
                    webConfig.setNumOfThread(3);
                    webConfig.setSortOrder(1);
                    webConfig.setUrls(webUrls);
                    webConfig.setIncludedUrls(parseFilterPaths(globalParams.get(GOOD_URLS), true, false));
                    webConfig.setIncludedDocUrls(StringUtil.EMPTY);
                    webConfig.setExcludedUrls(parseFilterPaths(globalParams.get(BAD_URLS), true, false));
                    webConfig.setExcludedDocUrls(StringUtil.EMPTY);
                    webConfig.setUserAgent(userAgent);
                    webConfig.setPermissions(new String[] { "Rguest" });
                    webConfig.setCreatedBy(Constants.SYSTEM_USER);
                    webConfig.setCreatedTime(now);
                    webConfig.setUpdatedBy(Constants.SYSTEM_USER);
                    webConfig.setUpdatedTime(now);
                }

                final String fileUrls = urlList.stream().filter(s -> Arrays.stream(fileProtocols).anyMatch(p -> s.startsWith(p)))
                        .collect(Collectors.joining("\n"));
                if (StringUtil.isNotBlank(fileUrls)) {
                    fileConfig = new FileConfig();
                    fileConfig.setName("Default");
                    fileConfig.setAvailable(true);
                    fileConfig.setBoost(1.0f);
                    fileConfig.setConfigParameter(StringUtil.EMPTY);
                    fileConfig.setIntervalTime(0);
                    fileConfig.setNumOfThread(5);
                    fileConfig.setSortOrder(2);
                    fileConfig.setPaths(fileUrls);
                    fileConfig.setIncludedPaths(parseFilterPaths(globalParams.get(GOOD_URLS), false, true));
                    fileConfig.setIncludedDocPaths(StringUtil.EMPTY);
                    fileConfig.setExcludedPaths(parseFilterPaths(globalParams.get(BAD_URLS), false, true));
                    fileConfig.setExcludedDocPaths(StringUtil.EMPTY);
                    fileConfig.setPermissions(new String[] { "Rguest" });
                    fileConfig.setCreatedBy(Constants.SYSTEM_USER);
                    fileConfig.setCreatedTime(now);
                    fileConfig.setUpdatedBy(Constants.SYSTEM_USER);
                    fileConfig.setUpdatedTime(now);
                }
            }
        } else if ("user_agent".equalsIgnoreCase(qName) && GLOBALPARAMS.equalsIgnoreCase(tagQueue.get(tagQueue.size() - 2))) {
            userAgent = textBuf.toString().trim();
        }
        tagQueue.pollLast();
        textBuf.setLength(0);
    }

    /**
     * SAX event handler called to process character data between XML elements.
     * Accumulates text content in a buffer for later processing when the element ends.
     *
     * @param ch the characters from the XML document
     * @param start the start position in the character array
     * @param length the number of characters to use from the character array
     * @throws SAXException if a SAX error occurs during character processing
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String text = new String(ch, start, length);
        if (logger.isDebugEnabled()) {
            logger.debug("Text: {}", text);
        }
        textBuf.append(text);
    }

    /**
     * Parses and filters URL patterns from text based on protocol types.
     * Processes each line of the input text, filtering URLs based on web and file protocol support.
     *
     * @param text the raw text containing URL patterns, one per line
     * @param web true if web protocol URLs should be included
     * @param file true if file protocol URLs should be included
     * @return a newline-separated string of filtered URL patterns
     */
    protected String parseFilterPaths(final String text, final boolean web, final boolean file) {
        return split(text, "\n")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotBlank).map(this::getFilterPath).filter(s -> {
                    if (StringUtil.isBlank(s)) {
                        return false;
                    }
                    if (Arrays.stream(webProtocols).anyMatch(p -> s.startsWith(p))) {
                        return web;
                    }
                    if (Arrays.stream(fileProtocols).anyMatch(p -> s.startsWith(p))) {
                        return file;
                    }
                    return true;
                }).collect(Collectors.joining("\n")));
    }

    /**
     * Converts a GSA URL pattern into a regular expression pattern suitable for Fess.
     * Handles various GSA pattern formats including regexp, contains, and URL-based patterns.
     *
     * @param s the input GSA pattern string
     * @return a regular expression pattern string, or empty string for comments/invalid patterns
     */
    protected String getFilterPath(final String s) {
        if (s.startsWith("#")) {
            return StringUtil.EMPTY;
        }
        if (s.startsWith(CONTAINS)) {
            final String v = s.substring(CONTAINS.length());
            final StringBuilder buf = new StringBuilder(100);
            return ".*" + appendFileterPath(buf, escape(v)) + ".*";
        }
        if (s.startsWith(REGEXP_IGNORE_CASE)) {
            final String v = s.substring(REGEXP_IGNORE_CASE.length());
            final StringBuilder buf = new StringBuilder(100);
            buf.append("(?i)");
            return appendFileterPath(buf, unescape(v));
        }
        if (s.startsWith(REGEXP_CASE)) {
            final String v = s.substring(REGEXP_CASE.length());
            final StringBuilder buf = new StringBuilder(100);
            return appendFileterPath(buf, unescape(v));
        }
        if (s.startsWith(REGEXP)) {
            final String v = s.substring(REGEXP.length());
            final StringBuilder buf = new StringBuilder(100);
            return appendFileterPath(buf, unescape(v));
        }
        if (Arrays.stream(webProtocols).anyMatch(p -> s.startsWith(p)) || Arrays.stream(fileProtocols).anyMatch(p -> s.startsWith(p))) {
            return escape(s) + ".*";
        }
        final StringBuilder buf = new StringBuilder(100);
        return appendFileterPath(buf, escape(s));
    }

    /**
     * Escapes special regex characters in a string to create a literal pattern.
     * Handles anchor characters (^ and $) specially to preserve their regex meaning.
     *
     * @param s the string to escape
     * @return an escaped regex pattern, or empty string for comments
     */
    protected String escape(final String s) {
        if (s.startsWith("#")) {
            return StringUtil.EMPTY;
        }
        if (s.startsWith("^") && s.endsWith("$")) {
            return "^" + Pattern.quote(s.substring(1, s.length() - 1)) + "$";
        }
        if (s.startsWith("^")) {
            return "^" + Pattern.quote(s.substring(1));
        }
        if (s.endsWith("$")) {
            return Pattern.quote(s.substring(0, s.length() - 1)) + "$";
        }
        return Pattern.quote(s);
    }

    /**
     * Unescapes double backslashes in regex patterns.
     * Converts escaped backslashes (\\) back to single backslashes (\).
     *
     * @param s the string to unescape
     * @return the unescaped string
     */
    protected String unescape(final String s) {
        return s.replace("\\\\", "\\");
    }

    /**
     * Appends a filter path pattern to a string buffer with appropriate wildcards.
     * Handles various pattern formats including anchored patterns and quoted patterns.
     *
     * @param buf the string buffer to append to
     * @param v the pattern value to append
     * @return the complete pattern string from the buffer
     */
    protected String appendFileterPath(final StringBuilder buf, final String v) {
        if (StringUtil.isBlank(v)) {
            return StringUtil.EMPTY;
        }

        if (v.startsWith("^")) {
            buf.append(v);
            if (!v.endsWith("$")) {
                buf.append(".*");
            }
        } else if (v.endsWith("$")) {
            buf.append(".*");
            buf.append(v);
        } else if (v.endsWith("/\\E")) {
            buf.append(".*");
            buf.append(v);
            buf.append(".*");
        } else {
            buf.append(v);
        }
        return buf.toString();
    }

    /**
     * Sets the array of web protocols to recognize for URL classification.
     *
     * @param webProtocols array of protocol prefixes (e.g., "http:", "https:")
     */
    public void setWebProtocols(final String[] webProtocols) {
        this.webProtocols = webProtocols;
    }

    /**
     * Sets the array of file protocols to recognize for URL classification.
     *
     * @param fileProtocols array of protocol prefixes (e.g., "file:", "smb:", "ftp:")
     */
    public void setFileProtocols(final String[] fileProtocols) {
        this.fileProtocols = fileProtocols;
    }

    /**
     * Returns a string representation of this parser's current state.
     * Includes information about parsed label types and configuration objects.
     *
     * @return a string representation of the parser state
     */
    @Override
    public String toString() {
        return "GsaConfigParser [labelList=" + labelList + ", webConfig=" + webConfig + ", fileConfig=" + fileConfig + "]";
    }

    /**
     * Gets the web crawling configuration generated from the parsed GSA config.
     *
     * @return an optional containing the web configuration, or empty if no web URLs were found
     */
    public OptionalEntity<WebConfig> getWebConfig() {
        return OptionalUtil.ofNullable(webConfig);
    }

    /**
     * Gets the file crawling configuration generated from the parsed GSA config.
     *
     * @return an optional containing the file configuration, or empty if no file URLs were found
     */
    public OptionalEntity<FileConfig> getFileConfig() {
        return OptionalUtil.ofNullable(fileConfig);
    }

    /**
     * Gets all label types (collections) parsed from the GSA configuration.
     * Each label type represents a collection with its own URL filtering rules.
     *
     * @return an array of label types representing the parsed collections
     */
    public LabelType[] getLabelTypes() {
        return labelList.toArray(new LabelType[labelList.size()]);
    }

}
