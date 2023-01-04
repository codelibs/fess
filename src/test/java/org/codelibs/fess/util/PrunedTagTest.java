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

import org.codelibs.fess.unit.UnitFessTestCase;

public class PrunedTagTest extends UnitFessTestCase {

    public void test_hashCode() {
        PrunedTag prunedtag = new PrunedTag("tag");

        assertTrue(prunedtag.hashCode() >= 0);

    }

    public void test_equals() {

        PrunedTag prunedtag = new PrunedTag("tag");

        assertTrue(prunedtag.equals(prunedtag));
        assertEquals(false, prunedtag.equals(null));
        assertEquals(false, prunedtag.equals(""));

    }

    public void test_toString() {
        String tag = "tag", id = "id", css = "css", attrName = "attrName", attrValue = "attrValue";
        PrunedTag prunedtag = new PrunedTag(tag);
        prunedtag.setAttr(attrName, attrValue);
        prunedtag.setId(id);
        prunedtag.setCss(css);
        assertEquals("PrunedTag [tag=" + tag + ", id=" + id + ", css=" + css + ", attrName=" + attrName + ", attrValue=" + attrValue + "]",
                prunedtag.toString());
    }

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
}