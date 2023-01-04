/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.LabelType;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.exception.GsaConfigException;
import org.dbflute.optional.OptionalEntity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GsaConfigParser extends DefaultHandler {

    private static final Logger logger = LogManager.getLogger(GsaConfigParser.class);

    public static final String REGEXP = "regexp:";

    public static final String REGEXP_CASE = "regexpCase:";

    public static final String REGEXP_IGNORE_CASE = "regexpIgnoreCase:";

    public static final String CONTAINS = "contains:";

    protected static final String COLLECTIONS = "collections";

    protected static final String COLLECTION = "collection";

    protected static final String GLOBALPARAMS = "globalparams";

    protected static final String START_URLS = "start_urls";

    protected static final String GOOD_URLS = "good_urls";

    protected static final String BAD_URLS = "bad_urls";

    protected String[] webProtocols = { "http:", "https:" };

    protected String[] fileProtocols = { "file:", "smb:", "smb1:", "ftp:", "storage:" };

    protected LinkedList<String> tagQueue;

    protected List<LabelType> labelList;

    protected LabelType labelType;

    protected Map<String, String> globalParams = new HashMap<>();

    protected WebConfig webConfig = null;

    protected FileConfig fileConfig = null;

    protected StringBuilder textBuf = new StringBuilder(1000);

    protected String userAgent = "gsa-crawler";

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

    @Override
    public void startDocument() throws SAXException {
        tagQueue = new LinkedList<>();
        labelList = new ArrayList<>();
        labelType = null;
    }

    @Override
    public void endDocument() throws SAXException {
        globalParams.clear();
        tagQueue.clear();
    }

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
            final long now = System.currentTimeMillis();
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
                final long now = System.currentTimeMillis();
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

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String text = new String(ch, start, length);
        if (logger.isDebugEnabled()) {
            logger.debug("Text: {}", text);
        }
        textBuf.append(text);
    }

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

    protected String unescape(final String s) {
        return s.replace("\\\\", "\\");
    }

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

    public void setWebProtocols(final String[] webProtocols) {
        this.webProtocols = webProtocols;
    }

    public void setFileProtocols(final String[] fileProtocols) {
        this.fileProtocols = fileProtocols;
    }

    @Override
    public String toString() {
        return "GsaConfigParser [labelList=" + labelList + ", webConfig=" + webConfig + ", fileConfig=" + fileConfig + "]";
    }

    public OptionalEntity<WebConfig> getWebConfig() {
        return OptionalUtil.ofNullable(webConfig);
    }

    public OptionalEntity<FileConfig> getFileConfig() {
        return OptionalUtil.ofNullable(fileConfig);
    }

    public LabelType[] getLabelTypes() {
        return labelList.toArray(new LabelType[labelList.size()]);
    }

}
