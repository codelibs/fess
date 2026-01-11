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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ParameterUtilTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void test_encryptParameter() {
        String value;
        String expect;

        value = null;
        expect = "";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "";
        expect = "";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "\n";
        expect = "";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "=";
        expect = "unknown.1=";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "=1\n=";
        expect = "unknown.1=1\nunknown.2=";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "a=b";
        expect = "a=b";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "password=b";
        expect = "password={cipher}5691346cc398a4450114883140fa84a7";
        assertEquals(expect, ParameterUtil.encrypt(value));

        value = "aaa.password=b\naaa=c\nccc.key=d";
        expect = "aaa.password={cipher}5691346cc398a4450114883140fa84a7\n" + "aaa=c\n" + "ccc.key={cipher}bf66204f1a59036869a684d61d337bd4";
        assertEquals(expect, ParameterUtil.encrypt(value));
    }

    @Test
    public void test_parse_advancedScenarios() {
        String value;
        Map<String, String> paramMap;

        // Test with whitespace around keys and values
        value = "  key1  =  value1  \n  key2=value2\n";
        paramMap = ParameterUtil.parse(value);
        assertEquals(2, paramMap.size());
        assertEquals("value1", paramMap.get("key1"));
        assertEquals("value2", paramMap.get("key2"));

        // Test with empty lines and mixed whitespace
        value = "key1=value1\n\n\t\nkey2=value2\n \n";
        paramMap = ParameterUtil.parse(value);
        assertEquals(2, paramMap.size());
        assertEquals("value1", paramMap.get("key1"));
        assertEquals("value2", paramMap.get("key2"));

        // Test with unknown keys (starting with =)
        value = "=value1\n=value2\nkey3=value3";
        paramMap = ParameterUtil.parse(value);
        assertEquals(3, paramMap.size());
        assertEquals("value1", paramMap.get("unknown.1"));
        assertEquals("value2", paramMap.get("unknown.2"));
        assertEquals("value3", paramMap.get("key3"));

        // Test with multiple equals signs
        value = "key1=value=with=equals\nkey2=another=value";
        paramMap = ParameterUtil.parse(value);
        assertEquals(2, paramMap.size());
        assertEquals("value=with=equals", paramMap.get("key1"));
        assertEquals("another=value", paramMap.get("key2"));

        // Test with various line separators
        value = "key1=value1\rkey2=value2\r\nkey3=value3";
        paramMap = ParameterUtil.parse(value);
        assertEquals(3, paramMap.size());
        assertEquals("value1", paramMap.get("key1"));
        assertEquals("value2", paramMap.get("key2"));
        assertEquals("value3", paramMap.get("key3"));

        // Test with special characters in keys and values
        value = "key.with.dots=value\nkey_with_underscores=value with spaces\nkey-with-dashes=value@#$%";
        paramMap = ParameterUtil.parse(value);
        assertEquals(3, paramMap.size());
        assertEquals("value", paramMap.get("key.with.dots"));
        assertEquals("value with spaces", paramMap.get("key_with_underscores"));
        assertEquals("value@#$%", paramMap.get("key-with-dashes"));
    }

    @Test
    public void test_encrypt_advancedScenarios() {
        String value;
        String result;

        // Test with already encrypted values (using valid hex)
        value = "password={cipher}5691346cc398a4450114883140fa84a7";
        result = ParameterUtil.encrypt(value);
        assertEquals("password={cipher}5691346cc398a4450114883140fa84a7", result);

        // Test with mixed encrypted and unencrypted
        value = "password={cipher}5691346cc398a4450114883140fa84a7\nsecretkey=unencrypted";
        result = ParameterUtil.encrypt(value);
        assertTrue(result.contains("password={cipher}5691346cc398a4450114883140fa84a7"));
        assertTrue(result.contains("secretkey={cipher}"));
        assertFalse(result.contains("secretkey=unencrypted"));

        // Test with keys that don't match pattern
        value = "normalfield=value\npassword=secret";
        result = ParameterUtil.encrypt(value);
        assertTrue("Result should contain normalfield=value, but was: " + result, result.contains("normalfield=value"));
        assertTrue("Result should contain password={cipher}, but was: " + result, result.contains("password={cipher}"));

        // Test with empty encryption target
        value = "password=";
        result = ParameterUtil.encrypt(value);
        assertTrue(result.contains("password={cipher}"));

        // Test with only whitespace value
        value = "password=   ";
        result = ParameterUtil.encrypt(value);
        assertTrue(result.contains("password={cipher}"));
    }

    @Test
    public void test_loadConfigParams() {
        Map<String, Object> paramMap;
        String configParam;

        // Test with empty map and null config
        paramMap = new HashMap<>();
        ParameterUtil.loadConfigParams(paramMap, null);
        assertEquals(0, paramMap.size());

        // Test with empty map and empty config
        paramMap = new HashMap<>();
        ParameterUtil.loadConfigParams(paramMap, "");
        assertEquals(0, paramMap.size());

        // Test with empty map and whitespace config
        paramMap = new HashMap<>();
        ParameterUtil.loadConfigParams(paramMap, "   \n  ");
        assertEquals(0, paramMap.size());

        // Test loading single parameter
        paramMap = new HashMap<>();
        configParam = "key1=value1";
        ParameterUtil.loadConfigParams(paramMap, configParam);
        assertEquals(1, paramMap.size());
        assertEquals("value1", paramMap.get("key1"));

        // Test loading multiple parameters
        paramMap = new HashMap<>();
        configParam = "key1=value1\nkey2=value2\nkey3=value3";
        ParameterUtil.loadConfigParams(paramMap, configParam);
        assertEquals(3, paramMap.size());
        assertEquals("value1", paramMap.get("key1"));
        assertEquals("value2", paramMap.get("key2"));
        assertEquals("value3", paramMap.get("key3"));

        // Test loading into existing map
        paramMap = new HashMap<>();
        paramMap.put("existing", "existingValue");
        configParam = "key1=value1\nkey2=value2";
        ParameterUtil.loadConfigParams(paramMap, configParam);
        assertEquals(3, paramMap.size());
        assertEquals("existingValue", paramMap.get("existing"));
        assertEquals("value1", paramMap.get("key1"));
        assertEquals("value2", paramMap.get("key2"));

        // Test overwriting existing keys
        paramMap = new HashMap<>();
        paramMap.put("key1", "oldValue");
        configParam = "key1=newValue";
        ParameterUtil.loadConfigParams(paramMap, configParam);
        assertEquals(1, paramMap.size());
        assertEquals("newValue", paramMap.get("key1"));
    }

    @Test
    public void test_createConfigParameterMap() {
        String configParameters;
        Map<ConfigName, Map<String, String>> result;

        // Test with null parameters
        result = ParameterUtil.createConfigParameterMap(null);
        assertNotNull(result);
        assertEquals(7, result.size());
        assertTrue(result.containsKey(ConfigName.CONFIG));
        assertTrue(result.containsKey(ConfigName.CLIENT));
        assertTrue(result.containsKey(ConfigName.XPATH));
        assertTrue(result.containsKey(ConfigName.META));
        assertTrue(result.containsKey(ConfigName.VALUE));
        assertTrue(result.containsKey(ConfigName.SCRIPT));
        assertTrue(result.containsKey(ConfigName.FIELD));
        assertEquals(0, result.get(ConfigName.CONFIG).size());

        // Test with empty parameters
        result = ParameterUtil.createConfigParameterMap("");
        assertNotNull(result);
        assertEquals(7, result.size());
        for (Map<String, String> map : result.values()) {
            assertEquals(0, map.size());
        }

        // Test with config parameters
        configParameters = "config.timeout=30\nconfig.retry=3";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.CONFIG).size());
        assertEquals("30", result.get(ConfigName.CONFIG).get("timeout"));
        assertEquals("3", result.get(ConfigName.CONFIG).get("retry"));
        assertEquals(0, result.get(ConfigName.CLIENT).size());

        // Test with client parameters
        configParameters = "client.host=localhost\nclient.port=9200";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.CLIENT).size());
        assertEquals("localhost", result.get(ConfigName.CLIENT).get("host"));
        assertEquals("9200", result.get(ConfigName.CLIENT).get("port"));
        assertEquals(0, result.get(ConfigName.CONFIG).size());

        // Test with xpath parameters
        configParameters = "field.xpath.title=//title\nfield.xpath.content=//body";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.XPATH).size());
        assertEquals("//title", result.get(ConfigName.XPATH).get("title"));
        assertEquals("//body", result.get(ConfigName.XPATH).get("content"));

        // Test with meta parameters
        configParameters = "field.meta.author=author\nfield.meta.date=date";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.META).size());
        assertEquals("author", result.get(ConfigName.META).get("author"));
        assertEquals("date", result.get(ConfigName.META).get("date"));

        // Test with value parameters
        configParameters = "field.value.type=document\nfield.value.lang=en";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.VALUE).size());
        assertEquals("document", result.get(ConfigName.VALUE).get("type"));
        assertEquals("en", result.get(ConfigName.VALUE).get("lang"));

        // Test with script parameters
        configParameters = "field.script.title=doc.title\nfield.script.boost=2.0";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.SCRIPT).size());
        assertEquals("doc.title", result.get(ConfigName.SCRIPT).get("title"));
        assertEquals("2.0", result.get(ConfigName.SCRIPT).get("boost"));

        // Test with field config parameters
        configParameters = "field.config.analyzer=standard\nfield.config.store=true";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(2, result.get(ConfigName.FIELD).size());
        assertEquals("standard", result.get(ConfigName.FIELD).get("analyzer"));
        assertEquals("true", result.get(ConfigName.FIELD).get("store"));

        // Test with mixed parameters
        configParameters =
                "config.timeout=30\nclient.host=localhost\nfield.xpath.title=//title\nfield.meta.author=author\nfield.value.type=document\nfield.script.boost=2.0\nfield.config.analyzer=standard";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(1, result.get(ConfigName.CONFIG).size());
        assertEquals(1, result.get(ConfigName.CLIENT).size());
        assertEquals(1, result.get(ConfigName.XPATH).size());
        assertEquals(1, result.get(ConfigName.META).size());
        assertEquals(1, result.get(ConfigName.VALUE).size());
        assertEquals(1, result.get(ConfigName.SCRIPT).size());
        assertEquals(1, result.get(ConfigName.FIELD).size());

        // Test with parameters that don't match any prefix
        configParameters = "unknown.param=value\nother=value2";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        for (Map<String, String> map : result.values()) {
            assertEquals(0, map.size());
        }

        // Test with empty values
        configParameters = "config.empty=\nfield.xpath.empty=";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals(1, result.get(ConfigName.CONFIG).size());
        assertEquals("", result.get(ConfigName.CONFIG).get("empty"));
        assertEquals(1, result.get(ConfigName.XPATH).size());
        assertEquals("", result.get(ConfigName.XPATH).get("empty"));
    }

    @Test
    public void test_createConfigParameterMap_edgeCases() {
        String configParameters;
        Map<ConfigName, Map<String, String>> result;

        // Test with parameters that have prefix but no suffix
        configParameters = "config.=value\nclient.=value2";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals("value", result.get(ConfigName.CONFIG).get(""));
        assertEquals("value2", result.get(ConfigName.CLIENT).get(""));

        // Test with whitespace in parameters
        configParameters = "  config.timeout  =  30  \n  client.host  =  localhost  ";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals("30", result.get(ConfigName.CONFIG).get("timeout"));
        assertEquals("localhost", result.get(ConfigName.CLIENT).get("host"));

        // Test with multiple equals signs
        configParameters = "config.equation=a=b+c\nfield.xpath.complex=//div[@class='content']";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals("a=b+c", result.get(ConfigName.CONFIG).get("equation"));
        assertEquals("//div[@class='content']", result.get(ConfigName.XPATH).get("complex"));

        // Test with special characters in keys and values
        configParameters = "config.key-with-dashes=value\nfield.config.key_with_underscores=value@#$%";
        result = ParameterUtil.createConfigParameterMap(configParameters);
        assertEquals("value", result.get(ConfigName.CONFIG).get("key-with-dashes"));
        assertEquals("value@#$%", result.get(ConfigName.FIELD).get("key_with_underscores"));
    }

    @Test
    public void test_integrationScenarios() {
        // Test complete workflow: parse -> encrypt -> createConfigParameterMap
        String originalParams = "config.timeout=30\npassword=secret\nclient.host=localhost";

        // First parse
        Map<String, String> parsed = ParameterUtil.parse(originalParams);
        assertEquals(3, parsed.size());
        assertEquals("secret", parsed.get("password"));

        // Then encrypt
        String encrypted = ParameterUtil.encrypt(originalParams);
        assertTrue(encrypted.contains("password={cipher}"));
        assertTrue(encrypted.contains("config.timeout=30"));

        // Then create config map
        Map<ConfigName, Map<String, String>> configMap = ParameterUtil.createConfigParameterMap(encrypted);
        assertEquals("30", configMap.get(ConfigName.CONFIG).get("timeout"));
        assertEquals("localhost", configMap.get(ConfigName.CLIENT).get("host"));

        // Test loadConfigParams with complex scenario
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("existing", "value");
        String configParam = "new.param=value\nconfig.setting=true";
        ParameterUtil.loadConfigParams(targetMap, configParam);
        assertEquals(3, targetMap.size());
        assertEquals("value", targetMap.get("existing"));
        assertEquals("value", targetMap.get("new.param"));
        assertEquals("true", targetMap.get("config.setting"));
    }

    @Test
    public void test_performanceAndBoundary() {
        // Test with large input
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeInput.append("key").append(i).append("=value").append(i).append("\n");
        }

        Map<String, String> result = ParameterUtil.parse(largeInput.toString());
        assertEquals(1000, result.size());
        assertEquals("value500", result.get("key500"));

        // Test createConfigParameterMap with large input
        StringBuilder configInput = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            configInput.append("config.key").append(i).append("=value").append(i).append("\n");
            configInput.append("client.key").append(i).append("=value").append(i).append("\n");
        }

        Map<ConfigName, Map<String, String>> configResult = ParameterUtil.createConfigParameterMap(configInput.toString());
        assertEquals(100, configResult.get(ConfigName.CONFIG).size());
        assertEquals(100, configResult.get(ConfigName.CLIENT).size());
        assertEquals("value50", configResult.get(ConfigName.CONFIG).get("key50"));
    }
}
