/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ParameterUtilTest extends UnitFessTestCase {
    public void test_convertParameterMap() {
        String parameters;
        Map<String, String> parameterMap;

        parameters = "";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(0, parameterMap.size());

        parameters = "domain";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(1, parameterMap.size());
        assertEquals("", parameterMap.get("domain"));

        parameters = "domain=";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(1, parameterMap.size());
        assertEquals("", parameterMap.get("domain"));

        parameters = "domain=D";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(1, parameterMap.size());
        assertEquals("D", parameterMap.get("domain"));

        parameters = "domain=DOMAIN";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(1, parameterMap.size());
        assertEquals("DOMAIN", parameterMap.get("domain"));

        parameters = "\n";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(0, parameterMap.size());

        parameters = "domain\nworkstation";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(2, parameterMap.size());
        assertEquals("", parameterMap.get("domain"));
        assertEquals("", parameterMap.get("workstation"));

        parameters = "domain=\nworkstation=";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(2, parameterMap.size());
        assertEquals("", parameterMap.get("domain"));
        assertEquals("", parameterMap.get("workstation"));

        parameters = "domain=D\nworkstation=W";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(2, parameterMap.size());
        assertEquals("D", parameterMap.get("domain"));
        assertEquals("W", parameterMap.get("workstation"));

        parameters = "domain=DOMAIN\nworkstation=WORKSTATION";
        parameterMap = ParameterUtil.parse(parameters);
        assertEquals(2, parameterMap.size());
        assertEquals("DOMAIN", parameterMap.get("domain"));
        assertEquals("WORKSTATION", parameterMap.get("workstation"));
    }

    public void test_parseParameter() {
        String value;
        Map<String, String> paramMap;

        value = "a=b";
        paramMap = ParameterUtil.parse(value);
        assertEquals(1, paramMap.size());
        assertEquals("b", paramMap.get("a"));

        value = "a=b\n1=2";
        paramMap = ParameterUtil.parse(value);
        assertEquals(2, paramMap.size());
        assertEquals("b", paramMap.get("a"));
        assertEquals("2", paramMap.get("1"));

        value = "a=";
        paramMap = ParameterUtil.parse(value);
        assertEquals(1, paramMap.size());
        assertEquals("", paramMap.get("a"));

        value = "a";
        paramMap = ParameterUtil.parse(value);
        assertEquals(1, paramMap.size());
        assertEquals("", paramMap.get("a"));

        value = "a=b=c";
        paramMap = ParameterUtil.parse(value);
        assertEquals(1, paramMap.size());
        assertEquals("b=c", paramMap.get("a"));

        value = null;
        paramMap = ParameterUtil.parse(value);
        assertEquals(0, paramMap.size());

        value = "";
        paramMap = ParameterUtil.parse(value);
        assertEquals(0, paramMap.size());

        value = " ";
        paramMap = ParameterUtil.parse(value);
        assertEquals(0, paramMap.size());
    }

    public void test_parseScript() {
        String value;
        Map<String, String> scriptMap;

        value = "a=b";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(1, scriptMap.size());
        assertEquals("b", scriptMap.get("a"));

        value = "a=b\n1=2";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(2, scriptMap.size());
        assertEquals("b", scriptMap.get("a"));
        assertEquals("2", scriptMap.get("1"));

        value = "a=";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(1, scriptMap.size());
        assertEquals("", scriptMap.get("a"));

        value = "a";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(1, scriptMap.size());
        assertEquals("", scriptMap.get("a"));

        value = "a=b=c";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(1, scriptMap.size());
        assertEquals("b=c", scriptMap.get("a"));

        value = null;
        scriptMap = ParameterUtil.parse(value);
        assertEquals(0, scriptMap.size());

        value = "";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(0, scriptMap.size());

        value = " ";
        scriptMap = ParameterUtil.parse(value);
        assertEquals(0, scriptMap.size());
    }

}
