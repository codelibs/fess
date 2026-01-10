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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.junit.jupiter.api.Test;

public class PrunedTagTest extends UnitFessTestCase {

    @Test
    public void test_hashCode() {
        PrunedTag prunedtag = new PrunedTag("tag");

        assertTrue(prunedtag.hashCode() >= 0);

    }

    @Test
    public void test_equals() {

        PrunedTag prunedtag = new PrunedTag("tag");

        assertTrue(prunedtag.equals(prunedtag));
        assertEquals(false, prunedtag.equals(null));
        assertEquals(false, prunedtag.equals(""));

    }

    @Test
    public void test_toString() {
        String tag = "tag", id = "id", css = "css", attrName = "attrName", attrValue = "attrValue";
        PrunedTag prunedtag = new PrunedTag(tag);
        prunedtag.setAttr(attrName, attrValue);
        prunedtag.setId(id);
        prunedtag.setCss(css);
        assertEquals("PrunedTag [tag=" + tag + ", id=" + id + ", css=" + css + ", attrName=" + attrName + ", attrValue=" + attrValue + "]",
                prunedtag.toString());
    }

    @Test
    public void test_parse() {
        PrunedTag[] tags = PrunedTag.parse("");
        assertEquals(0, tags.length);

        tags = PrunedTag.parse("a");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=null, css=null, attrName=null, attrValue=null]", tags[0].toString());

        tags = PrunedTag.parse("a#test");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=test, css=null, attrName=null, attrValue=null]", tags[0].toString());

        tags = PrunedTag.parse("a.test");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=null, css=test, attrName=null, attrValue=null]", tags[0].toString());

        tags = PrunedTag.parse("a[target=_blank]");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=null, css=null, attrName=target, attrValue=_blank]", tags[0].toString());

        tags = PrunedTag.parse("a.link,div#123");
        assertEquals(2, tags.length);
        assertEquals("PrunedTag [tag=a, id=null, css=link, attrName=null, attrValue=null]", tags[0].toString());
        assertEquals("PrunedTag [tag=div, id=123, css=null, attrName=null, attrValue=null]", tags[1].toString());

        tags = PrunedTag.parse("a#test-a");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=test-a, css=null, attrName=null, attrValue=null]", tags[0].toString());

        tags = PrunedTag.parse("a.test-a");
        assertEquals(1, tags.length);
        assertEquals("PrunedTag [tag=a, id=null, css=test-a, attrName=null, attrValue=null]", tags[0].toString());
    }

    @Test
    public void test_matches_basicTag() {
        PrunedTag tag = new PrunedTag("div");

        MockNode divNode = new MockNode("div");
        assertTrue(tag.matches(divNode));

        MockNode spanNode = new MockNode("span");
        assertFalse(tag.matches(spanNode));

        // Case insensitive test
        MockNode divUpperNode = new MockNode("DIV");
        assertTrue(tag.matches(divUpperNode));
    }

    @Test
    public void test_matches_withId() {
        PrunedTag tag = new PrunedTag("div");
        tag.setId("test-id");

        // Node with matching id
        MockNode nodeWithId = new MockNode("div");
        nodeWithId.addAttribute("id", "test-id");
        assertTrue(tag.matches(nodeWithId));

        // Node with different id
        MockNode nodeWithDifferentId = new MockNode("div");
        nodeWithDifferentId.addAttribute("id", "other-id");
        assertFalse(tag.matches(nodeWithDifferentId));

        // Node without id attribute
        MockNode nodeWithoutId = new MockNode("div");
        assertFalse(tag.matches(nodeWithoutId));

        // Wrong tag with correct id
        MockNode wrongTag = new MockNode("span");
        wrongTag.addAttribute("id", "test-id");
        assertFalse(tag.matches(wrongTag));
    }

    @Test
    public void test_matches_withCss() {
        PrunedTag tag = new PrunedTag("div");
        tag.setCss("highlight");

        // Node with matching CSS class
        MockNode nodeWithClass = new MockNode("div");
        nodeWithClass.addAttribute("class", "highlight");
        assertTrue(tag.matches(nodeWithClass));

        // Node with multiple classes including target
        MockNode nodeWithMultipleClasses = new MockNode("div");
        nodeWithMultipleClasses.addAttribute("class", "container highlight active");
        assertTrue(tag.matches(nodeWithMultipleClasses));

        // Node with different class
        MockNode nodeWithDifferentClass = new MockNode("div");
        nodeWithDifferentClass.addAttribute("class", "container");
        assertFalse(tag.matches(nodeWithDifferentClass));

        // Node without class attribute
        MockNode nodeWithoutClass = new MockNode("div");
        assertFalse(tag.matches(nodeWithoutClass));

        // Node with empty class
        MockNode nodeWithEmptyClass = new MockNode("div");
        nodeWithEmptyClass.addAttribute("class", "");
        assertFalse(tag.matches(nodeWithEmptyClass));

        // Node with null class
        MockNode nodeWithNullClass = new MockNode("div");
        nodeWithNullClass.addAttribute("class", null);
        assertFalse(tag.matches(nodeWithNullClass));
    }

    @Test
    public void test_matches_withAttribute() {
        PrunedTag tag = new PrunedTag("a");
        tag.setAttr("target", "_blank");

        // Node with matching attribute
        MockNode nodeWithAttr = new MockNode("a");
        nodeWithAttr.addAttribute("target", "_blank");
        assertTrue(tag.matches(nodeWithAttr));

        // Node with different attribute value
        MockNode nodeWithDifferentAttr = new MockNode("a");
        nodeWithDifferentAttr.addAttribute("target", "_self");
        assertFalse(tag.matches(nodeWithDifferentAttr));

        // Node without the attribute
        MockNode nodeWithoutAttr = new MockNode("a");
        assertFalse(tag.matches(nodeWithoutAttr));

        // Wrong tag with correct attribute
        MockNode wrongTag = new MockNode("div");
        wrongTag.addAttribute("target", "_blank");
        assertFalse(tag.matches(wrongTag));
    }

    @Test
    public void test_matches_complexCombinations() {
        // Test tag with both id and css (id takes precedence)
        PrunedTag tagWithIdAndCss = new PrunedTag("div");
        tagWithIdAndCss.setId("main");
        tagWithIdAndCss.setCss("container");

        MockNode nodeWithBothIdAndClass = new MockNode("div");
        nodeWithBothIdAndClass.addAttribute("id", "main");
        nodeWithBothIdAndClass.addAttribute("class", "container");
        assertTrue(tagWithIdAndCss.matches(nodeWithBothIdAndClass));

        // Node with correct id but different class should still match (id takes precedence)
        MockNode nodeWithCorrectIdWrongClass = new MockNode("div");
        nodeWithCorrectIdWrongClass.addAttribute("id", "main");
        nodeWithCorrectIdWrongClass.addAttribute("class", "other");
        assertTrue(tagWithIdAndCss.matches(nodeWithCorrectIdWrongClass));

        // Node with correct class but wrong id should not match
        MockNode nodeWithCorrectClassWrongId = new MockNode("div");
        nodeWithCorrectClassWrongId.addAttribute("id", "other");
        nodeWithCorrectClassWrongId.addAttribute("class", "container");
        assertFalse(tagWithIdAndCss.matches(nodeWithCorrectClassWrongId));

        // Test combination of attribute and css
        PrunedTag tagWithAttrAndCss = new PrunedTag("a");
        tagWithAttrAndCss.setAttr("href", "#");
        tagWithAttrAndCss.setCss("nav-link");

        MockNode nodeMatchingBoth = new MockNode("a");
        nodeMatchingBoth.addAttribute("href", "#");
        nodeMatchingBoth.addAttribute("class", "nav-link");
        assertTrue(tagWithAttrAndCss.matches(nodeMatchingBoth));

        MockNode nodeWithWrongAttr = new MockNode("a");
        nodeWithWrongAttr.addAttribute("href", "http://example.com");
        nodeWithWrongAttr.addAttribute("class", "nav-link");
        assertFalse(tagWithAttrAndCss.matches(nodeWithWrongAttr));
    }

    @Test
    public void test_equals_comprehensive() {
        PrunedTag tag1 = new PrunedTag("div");
        tag1.setId("test");
        tag1.setCss("highlight");
        tag1.setAttr("data-value", "123");

        PrunedTag tag2 = new PrunedTag("div");
        tag2.setId("test");
        tag2.setCss("highlight");
        tag2.setAttr("data-value", "123");

        assertTrue(tag1.equals(tag2));
        assertTrue(tag2.equals(tag1));
        assertEquals(tag1.hashCode(), tag2.hashCode());

        // Different tag
        PrunedTag tagDifferentTag = new PrunedTag("span");
        tagDifferentTag.setId("test");
        tagDifferentTag.setCss("highlight");
        tagDifferentTag.setAttr("data-value", "123");
        assertFalse(tag1.equals(tagDifferentTag));

        // Different id
        PrunedTag tagDifferentId = new PrunedTag("div");
        tagDifferentId.setId("other");
        tagDifferentId.setCss("highlight");
        tagDifferentId.setAttr("data-value", "123");
        assertFalse(tag1.equals(tagDifferentId));

        // Different css
        PrunedTag tagDifferentCss = new PrunedTag("div");
        tagDifferentCss.setId("test");
        tagDifferentCss.setCss("other");
        tagDifferentCss.setAttr("data-value", "123");
        assertFalse(tag1.equals(tagDifferentCss));

        // Different attribute name
        PrunedTag tagDifferentAttrName = new PrunedTag("div");
        tagDifferentAttrName.setId("test");
        tagDifferentAttrName.setCss("highlight");
        tagDifferentAttrName.setAttr("data-other", "123");
        assertFalse(tag1.equals(tagDifferentAttrName));

        // Different attribute value
        PrunedTag tagDifferentAttrValue = new PrunedTag("div");
        tagDifferentAttrValue.setId("test");
        tagDifferentAttrValue.setCss("highlight");
        tagDifferentAttrValue.setAttr("data-value", "456");
        assertFalse(tag1.equals(tagDifferentAttrValue));

        // Null values handling
        PrunedTag tagWithNulls = new PrunedTag("div");
        PrunedTag anotherTagWithNulls = new PrunedTag("div");
        assertTrue(tagWithNulls.equals(anotherTagWithNulls));
    }

    @Test
    public void test_parse_complexCombinations() {
        // Test complex parsing combinations
        PrunedTag[] tags = PrunedTag.parse("div[data-toggle=modal].modal#main-modal");
        assertEquals(1, tags.length);
        PrunedTag tag = tags[0];
        assertEquals("div", tag.toString().split(", ")[0].split("=")[1]);
        assertTrue(tag.toString().contains("id=main-modal"));
        assertTrue(tag.toString().contains("css=modal"));
        assertTrue(tag.toString().contains("attrName=data-toggle"));
        assertTrue(tag.toString().contains("attrValue=modal"));

        // Test multiple tags with different combinations
        tags = PrunedTag.parse("a[href=#].nav-link, div.container, span#status");
        assertEquals(3, tags.length);

        // Test with whitespace
        tags = PrunedTag.parse(" div.test , span#id , a[target=_blank] ");
        assertEquals(3, tags.length);

        // Test with empty entries
        tags = PrunedTag.parse("div,,span,");
        assertEquals(2, tags.length);
    }

    @Test
    public void test_parse_edgeCases() {
        // Test empty string
        PrunedTag[] tags = PrunedTag.parse("");
        assertEquals(0, tags.length);

        // Test null
        tags = PrunedTag.parse(null);
        assertEquals(0, tags.length);

        // Test whitespace only
        tags = PrunedTag.parse("   ");
        assertEquals(0, tags.length);

        // Test commas only
        tags = PrunedTag.parse(",,,");
        assertEquals(0, tags.length);

        // Test mixed whitespace and commas
        tags = PrunedTag.parse(" , , , ");
        assertEquals(0, tags.length);
    }

    @Test
    public void test_parse_invalidFormats() {
        // Test invalid formats that should throw FessSystemException
        try {
            PrunedTag.parse("invalid_format_with_special_chars@#$%");
            fail("Should have thrown FessSystemException");
        } catch (FessSystemException e) {
            assertTrue(e.getMessage().contains("Invalid pruned tag"));
        }

        try {
            PrunedTag.parse("[invalid]");
            fail("Should have thrown FessSystemException");
        } catch (FessSystemException e) {
            assertTrue(e.getMessage().contains("Invalid pruned tag"));
        }

        try {
            PrunedTag.parse(".css-only");
            fail("Should have thrown FessSystemException");
        } catch (FessSystemException e) {
            assertTrue(e.getMessage().contains("Invalid pruned tag"));
        }
    }

    @Test
    public void test_parse_attributeVariations() {
        // Test different attribute formats
        PrunedTag[] tags = PrunedTag.parse("input[type=text]");
        assertEquals(1, tags.length);
        assertTrue(tags[0].toString().contains("attrName=type"));
        assertTrue(tags[0].toString().contains("attrValue=text"));

        tags = PrunedTag.parse("div[data-test=value123]");
        assertEquals(1, tags.length);
        assertTrue(tags[0].toString().contains("attrName=data-test"));
        assertTrue(tags[0].toString().contains("attrValue=value123"));

        tags = PrunedTag.parse("a[href=https://example.com]");
        assertEquals(1, tags.length);
        assertTrue(tags[0].toString().contains("attrName=href"));
        assertTrue(tags[0].toString().contains("attrValue=https://example.com"));
    }

    @Test
    public void test_hashCode_consistency() {
        PrunedTag tag1 = new PrunedTag("div");
        tag1.setId("test");
        tag1.setCss("highlight");

        PrunedTag tag2 = new PrunedTag("div");
        tag2.setId("test");
        tag2.setCss("highlight");

        // Equal objects must have equal hash codes
        assertEquals(tag1.hashCode(), tag2.hashCode());

        // Hash code should be consistent
        int hash1 = tag1.hashCode();
        int hash2 = tag1.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    public void test_setters() {
        PrunedTag tag = new PrunedTag("div");

        // Test setId
        tag.setId("unique-id");
        assertTrue(tag.toString().contains("id=unique-id"));

        // Test setCss
        tag.setCss("my-class");
        assertTrue(tag.toString().contains("css=my-class"));

        // Test setAttr
        tag.setAttr("data-role", "button");
        assertTrue(tag.toString().contains("attrName=data-role"));
        assertTrue(tag.toString().contains("attrValue=button"));

        // Test overwriting values
        tag.setId("new-id");
        tag.setCss("new-class");
        tag.setAttr("role", "link");
        assertTrue(tag.toString().contains("id=new-id"));
        assertTrue(tag.toString().contains("css=new-class"));
        assertTrue(tag.toString().contains("attrName=role"));
        assertTrue(tag.toString().contains("attrValue=link"));
    }

    // Mock implementation of Node for testing without external dependencies
    private static class MockNode implements Node {
        private final String nodeName;
        private final Map<String, MockNode> attributes = new HashMap<>();
        private final MockNamedNodeMap namedNodeMap = new MockNamedNodeMap();

        public MockNode(String nodeName) {
            this.nodeName = nodeName;
        }

        public void addAttribute(String name, String value) {
            MockNode attrNode = new MockNode(name);
            attrNode.nodeValue = value;
            attributes.put(name, attrNode);
            namedNodeMap.setNamedItem(name, attrNode);
        }

        @Override
        public String getNodeName() {
            return nodeName;
        }

        @Override
        public NamedNodeMap getAttributes() {
            return namedNodeMap;
        }

        private String nodeValue;

        @Override
        public String getNodeValue() {
            return nodeValue;
        }

        // Other Node methods (not needed for testing, returning defaults)

        @Override
        public Node getParentNode() {
            return null;
        }

        @Override
        public org.w3c.dom.NodeList getChildNodes() {
            return null;
        }

        @Override
        public Node getFirstChild() {
            return null;
        }

        @Override
        public Node getLastChild() {
            return null;
        }

        @Override
        public Node getPreviousSibling() {
            return null;
        }

        @Override
        public Node getNextSibling() {
            return null;
        }

        @Override
        public org.w3c.dom.Document getOwnerDocument() {
            return null;
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) {
            return null;
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) {
            return null;
        }

        @Override
        public Node removeChild(Node oldChild) {
            return null;
        }

        @Override
        public Node appendChild(Node newChild) {
            return null;
        }

        @Override
        public boolean hasChildNodes() {
            return false;
        }

        @Override
        public Node cloneNode(boolean deep) {
            return null;
        }

        @Override
        public void normalize() {
        }

        @Override
        public boolean isSupported(String feature, String version) {
            return false;
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public String getPrefix() {
            return null;
        }

        @Override
        public void setPrefix(String prefix) {
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public boolean hasAttributes() {
            return !attributes.isEmpty();
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public short compareDocumentPosition(Node other) {
            return 0;
        }

        @Override
        public String getTextContent() {
            return null;
        }

        @Override
        public void setTextContent(String textContent) {
        }

        @Override
        public boolean isSameNode(Node other) {
            return false;
        }

        @Override
        public String lookupPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public boolean isDefaultNamespace(String namespaceURI) {
            return false;
        }

        @Override
        public String lookupNamespaceURI(String prefix) {
            return null;
        }

        @Override
        public boolean isEqualNode(Node arg) {
            return false;
        }

        @Override
        public Object getFeature(String feature, String version) {
            return null;
        }

        @Override
        public Object setUserData(String key, Object data, org.w3c.dom.UserDataHandler handler) {
            return null;
        }

        @Override
        public Object getUserData(String key) {
            return null;
        }

        @Override
        public void setNodeValue(String nodeValue) {
            this.nodeValue = nodeValue;
        }

        @Override
        public short getNodeType() {
            return 0;
        }
    }

    private static class MockNamedNodeMap implements NamedNodeMap {
        private final Map<String, Node> items = new HashMap<>();

        public void setNamedItem(String name, Node node) {
            items.put(name, node);
        }

        @Override
        public Node getNamedItem(String name) {
            return items.get(name);
        }

        @Override
        public Node setNamedItem(Node arg) {
            return null;
        }

        @Override
        public Node removeNamedItem(String name) {
            return null;
        }

        @Override
        public Node item(int index) {
            return null;
        }

        @Override
        public int getLength() {
            return items.size();
        }

        @Override
        public Node getNamedItemNS(String namespaceURI, String localName) {
            return null;
        }

        @Override
        public Node setNamedItemNS(Node arg) {
            return null;
        }

        @Override
        public Node removeNamedItemNS(String namespaceURI, String localName) {
            return null;
        }
    }
}