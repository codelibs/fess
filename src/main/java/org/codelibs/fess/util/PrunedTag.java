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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.exception.FessSystemException;
import org.w3c.dom.Node;

/**
 * Represents a tag configuration for pruning HTML content during document processing.
 * This class defines tag patterns that match HTML elements based on tag name, CSS class, ID, or custom attributes.
 * It is used to identify and remove unwanted HTML elements from crawled documents.
 */
public class PrunedTag {
    /** The HTML tag name to match (e.g., "div", "span", "p") */
    private final String tag;
    /** The ID attribute value to match */
    private String id;
    /** The CSS class name to match */
    private String css;
    /** The custom attribute name to match */
    private String attrName;
    /** The custom attribute value to match */
    private String attrValue;

    /**
     * Creates a new PrunedTag instance with the specified tag name.
     *
     * @param tag the HTML tag name to match (e.g., "div", "span", "p")
     */
    public PrunedTag(final String tag) {
        this.tag = tag;
    }

    /**
     * Checks if this pruned tag configuration matches the given DOM node.
     * The matching is based on tag name, and optionally ID, CSS class, or custom attributes.
     *
     * @param node the DOM node to check against this pruned tag configuration
     * @return true if the node matches this pruned tag configuration, false otherwise
     */
    public boolean matches(final Node node) {
        if (tag.equalsIgnoreCase(node.getNodeName())) {
            if (attrName != null) {
                final Node attr = node.getAttributes().getNamedItem(attrName);
                if (attr == null || !attrValue.equals(attr.getNodeValue())) {
                    return false;
                }
            }
            if (id == null) {
                if (css == null) {
                    return true;
                }
                final Node classAttr = node.getAttributes().getNamedItem("class");
                if (classAttr != null) {
                    final String value = classAttr.getNodeValue();
                    if (StringUtil.isNotBlank(value)) {
                        return StreamUtil.split(value, " ").get(stream -> stream.anyMatch(s -> css.equals(s)));
                    }
                }
            } else {
                final Node idAttr = node.getAttributes().getNamedItem("id");
                if (idAttr != null) {
                    final String value = idAttr.getNodeValue();
                    return id.equals(value);
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(css, id, tag);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PrunedTag other = (PrunedTag) obj;
        return StringUtils.compare(tag, other.tag) == 0 //
                && StringUtils.compare(css, other.css) == 0 //
                && StringUtils.compare(id, other.id) == 0 //
                && StringUtils.compare(attrName, other.attrName) == 0 //
                && StringUtils.compare(attrValue, other.attrValue) == 0;
    }

    /**
     * Sets the ID attribute value that this pruned tag should match.
     *
     * @param id the ID attribute value to match
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Sets the CSS class name that this pruned tag should match.
     *
     * @param css the CSS class name to match
     */
    public void setCss(final String css) {
        this.css = css;
    }

    /**
     * Sets a custom attribute name-value pair that this pruned tag should match.
     *
     * @param name the attribute name to match
     * @param value the attribute value to match
     */
    public void setAttr(final String name, final String value) {
        attrName = name;
        attrValue = value;
    }

    @Override
    public String toString() {
        return "PrunedTag [tag=" + tag + ", id=" + id + ", css=" + css + ", attrName=" + attrName + ", attrValue=" + attrValue + "]";
    }

    /**
     * Parses a comma-separated string of pruned tag configurations into an array of PrunedTag objects.
     * Each tag configuration follows the pattern: tagname[attr=value].classname#id
     *
     * Examples:
     * - "div.content" matches div elements with class "content"
     * - "span#header" matches span elements with ID "header"
     * - "p[data-type=ad]" matches p elements with data-type attribute equal to "ad"
     *
     * @param value the comma-separated string of pruned tag configurations
     * @return an array of PrunedTag objects parsed from the input string
     * @throws FessSystemException if the input string contains invalid tag patterns
     */
    public static PrunedTag[] parse(final String value) {
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(v -> {
            final Pattern pattern = Pattern.compile("(\\w+)(\\[[^\\]]+\\])?(\\.[\\w\\-]+)?(#[\\w\\-]+)?");
            final Matcher matcher = pattern.matcher(v.trim());
            if (matcher.matches()) {
                final PrunedTag tag = new PrunedTag(matcher.group(1));
                if (matcher.group(2) != null) {
                    final String attrPair = matcher.group(2).substring(1, matcher.group(2).length() - 1);
                    final Matcher equalMatcher = Pattern.compile("([\\w\\-]+)=(\\S+)").matcher(attrPair);
                    if (equalMatcher.matches()) {
                        tag.setAttr(equalMatcher.group(1), equalMatcher.group(2));
                    }
                }
                if (matcher.group(3) != null) {
                    tag.setCss(matcher.group(3).substring(1));
                }
                if (matcher.group(4) != null) {
                    tag.setId(matcher.group(4).substring(1));
                }
                return tag;
            }
            throw new FessSystemException("Invalid pruned tag: " + v);
        }).toArray(n -> new PrunedTag[n]));
    }
}
