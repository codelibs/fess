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
package org.codelibs.fess.app.web.admin.design;

import org.codelibs.fess.unit.UnitFessTestCase;

public class AdminDesignActionTest extends UnitFessTestCase {
    public void test_decodeJsp() {
        assertEquals("&lt;% a %&gt;", AdminDesignAction.decodeJsp("<% a %>"));
        assertEquals("&lt;%= a %&gt;", AdminDesignAction.decodeJsp("<%= a %>"));
        assertEquals("&lt;% a\nb %&gt;", AdminDesignAction.decodeJsp("<% a\nb %>"));
        assertEquals("&lt;%= a\nb %&gt;", AdminDesignAction.decodeJsp("<%= a\nb %>"));
        assertEquals("<% a", AdminDesignAction.decodeJsp("<% a"));
        assertEquals("<%= a", AdminDesignAction.decodeJsp("<%= a"));
        assertEquals("<% try{ %>", AdminDesignAction.decodeJsp("<!--TRY-->"));
        assertEquals("<% }catch(Exception e){session.invalidate();} %>",
                AdminDesignAction.decodeJsp("<!--CACHE_AND_SESSION_INVALIDATE-->"));
        assertEquals("&lt;% a %&gt; %>", AdminDesignAction.decodeJsp("<% a %> %>"));
        assertEquals("&lt;% a %&gt; <%", AdminDesignAction.decodeJsp("<% a %> <%"));
        assertEquals("&lt;% <% a %&gt;", AdminDesignAction.decodeJsp("<% <% a %>"));
        assertEquals("%> &lt;% a %&gt;", AdminDesignAction.decodeJsp("%> <% a %>"));
    }

    public void test_encodeJsp() {
        assertEquals("<!--TRY-->", AdminDesignAction.encodeJsp("<% try{ %>"));
        assertEquals("<!--CACHE_AND_SESSION_INVALIDATE-->",
                AdminDesignAction.encodeJsp("<% }catch(Exception e){session.invalidate();} %>"));
    }
}
