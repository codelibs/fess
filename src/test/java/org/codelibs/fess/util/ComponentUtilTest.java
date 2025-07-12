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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ComponentUtilTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ComponentUtil.setFessConfig(null);
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_setFessConfig_null() {
        ComponentUtil.setFessConfig(null);
        assertTrue(FessProp.propMap.isEmpty());
    }

    public void test_setFessConfig_notNull() {
        FessConfig mockConfig = new FessConfig.SimpleImpl();
        ComponentUtil.setFessConfig(mockConfig);
        assertSame(mockConfig, ComponentUtil.getFessConfig());
    }

    public void test_getFessConfig_cached() {
        FessConfig mockConfig = new FessConfig.SimpleImpl();
        ComponentUtil.setFessConfig(mockConfig);

        FessConfig result1 = ComponentUtil.getFessConfig();
        FessConfig result2 = ComponentUtil.getFessConfig();

        assertSame(result1, result2);
        assertSame(mockConfig, result1);
    }

    public void test_register_and_getComponent() {
        String testInstance = "test_instance";
        String componentName = "testComponent";

        ComponentUtil.register(testInstance, componentName);

        String retrieved = ComponentUtil.getComponent(componentName);
        assertSame(testInstance, retrieved);
    }

    public void test_processAfterContainerInit_available() {
        List<String> results = new ArrayList<>();
        Runnable process = () -> results.add("executed");

        ComponentUtil.processAfterContainerInit(process);

        assertEquals(0, results.size());
    }

    public void test_processAfterContainerInit_notAvailable() {
        List<String> results = new ArrayList<>();
        Runnable process = () -> results.add("executed");

        ComponentUtil.setFessConfig(null);

        ComponentUtil.processAfterContainerInit(process);

        assertEquals(0, results.size());
    }

    public void test_doInitProcesses() {
        List<String> executionOrder = new ArrayList<>();

        ComponentUtil.setFessConfig(null);
        ComponentUtil.processAfterContainerInit(() -> executionOrder.add("first"));
        ComponentUtil.processAfterContainerInit(() -> executionOrder.add("second"));

        assertEquals(0, executionOrder.size());

        ComponentUtil.doInitProcesses(Runnable::run);

        assertEquals(2, executionOrder.size());
        assertEquals("first", executionOrder.get(0));
        assertEquals("second", executionOrder.get(1));

        ComponentUtil.processAfterContainerInit(() -> executionOrder.add("third"));
        assertEquals(2, executionOrder.size());
    }

    public void test_available_withSystemHelper() {
        ComponentUtil.register(new Object(), "systemHelper");

        assertFalse(ComponentUtil.available());
    }

    public void test_available_withoutSystemHelper() {
        ComponentUtil.setFessConfig(null);

        assertFalse(ComponentUtil.available());
    }

    public void test_hasComponent_existsInMap() {
        String testInstance = "test_instance";
        String componentName = "testComponent";

        ComponentUtil.register(testInstance, componentName);

        assertTrue(ComponentUtil.hasComponent(componentName));
    }

    public void test_hasComponent_notExists() {
        assertFalse(ComponentUtil.hasComponent("nonExistentComponent"));
    }

    public void test_hasQueryParser() {
        assertFalse(ComponentUtil.hasQueryParser());

        ComponentUtil.register(new Object(), "queryParser");
        assertTrue(ComponentUtil.hasQueryParser());
    }

    public void test_hasViewHelper() {
        assertFalse(ComponentUtil.hasViewHelper());

        ComponentUtil.register(new Object(), "viewHelper");
        assertTrue(ComponentUtil.hasViewHelper());
    }

    public void test_hasQueryHelper() {
        assertFalse(ComponentUtil.hasQueryHelper());

        ComponentUtil.register(new Object(), "queryHelper");
        assertTrue(ComponentUtil.hasQueryHelper());
    }

    public void test_hasPopularWordHelper() {
        assertFalse(ComponentUtil.hasPopularWordHelper());

        ComponentUtil.register(new Object(), "popularWordHelper");
        assertTrue(ComponentUtil.hasPopularWordHelper());
    }

    public void test_hasRelatedQueryHelper() {
        assertFalse(ComponentUtil.hasRelatedQueryHelper());

        ComponentUtil.register(new Object(), "relatedQueryHelper");
        assertTrue(ComponentUtil.hasRelatedQueryHelper());
    }

    public void test_hasIngestFactory() {
        assertFalse(ComponentUtil.hasIngestFactory());

        ComponentUtil.register(new Object(), "ingestFactory");
        assertTrue(ComponentUtil.hasIngestFactory());
    }

    public void test_getJobExecutor_withSuffix() {
        try {
            ComponentUtil.getJobExecutor("testJobExecutor");
            fail("Should throw exception");
        } catch (Exception e) {
            // Expected
        }
    }

    public void test_getJobExecutor_withoutSuffix() {
        try {
            ComponentUtil.getJobExecutor("test");
            fail("Should throw exception");
        } catch (Exception e) {
            // Expected
        }
    }

    public void test_getComponent_byClass_fromMap() {
        String testInstance = "test_instance";
        ComponentUtil.register(testInstance, String.class.getCanonicalName());

        String retrieved = ComponentUtil.getComponent(String.class);
        assertSame(testInstance, retrieved);
    }

    public void test_getComponent_byName_fromMap() {
        Integer testInstance = 42;
        String componentName = "testInteger";
        ComponentUtil.register(testInstance, componentName);

        Integer retrieved = ComponentUtil.getComponent(componentName);
        assertSame(testInstance, retrieved);
    }

    public void test_register_overwrite() {
        String first = "first";
        String second = "second";
        String componentName = "testComponent";

        ComponentUtil.register(first, componentName);
        String retrieved1 = ComponentUtil.getComponent(componentName);
        assertSame(first, retrieved1);

        ComponentUtil.register(second, componentName);
        String retrieved2 = ComponentUtil.getComponent(componentName);
        assertSame(second, retrieved2);
    }

    public void test_multiple_components() {
        String comp1 = "component1";
        String comp2 = "component2";
        Integer comp3 = 123;

        ComponentUtil.register(comp1, "name1");
        ComponentUtil.register(comp2, "name2");
        ComponentUtil.register(comp3, "name3");

        assertEquals(comp1, ComponentUtil.getComponent("name1"));
        assertEquals(comp2, ComponentUtil.getComponent("name2"));
        assertEquals(comp3, ComponentUtil.getComponent("name3"));
    }
}