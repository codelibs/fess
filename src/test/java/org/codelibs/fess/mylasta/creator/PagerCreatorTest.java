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
package org.codelibs.fess.mylasta.creator;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.di.core.assembler.AutoBindingDefFactory;
import org.lastaflute.di.core.customizer.ComponentCustomizer;
import org.lastaflute.di.core.meta.impl.InstanceDefFactory;
import org.lastaflute.di.naming.NamingConvention;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
// NamingConventionImpl removed - using mock instead

public class PagerCreatorTest extends UnitFessTestCase {

    private PagerCreator pagerCreator;
    private NamingConvention namingConvention;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        namingConvention = new MockNamingConvention();
        pagerCreator = new PagerCreator(namingConvention);
    }

    // Test constructor initialization
    @Test
    public void test_constructor() {
        // Verify suffix is set correctly
        assertEquals(PagerCreator.SUFFIX, "Pager");

        // Verify name suffix is set
        assertEquals("Pager", pagerCreator.getNameSuffix());

        // Verify instance def is SESSION
        assertEquals(InstanceDefFactory.SESSION, pagerCreator.getInstanceDef());

        // Verify auto binding def is NONE
        assertEquals(AutoBindingDefFactory.NONE, pagerCreator.getAutoBindingDef());
    }

    // Test getPagerCustomizer when no customizer is set
    @Test
    public void test_getPagerCustomizer_null() {
        assertNull(pagerCreator.getPagerCustomizer());
    }

    // Test setPagerCustomizer and getPagerCustomizer
    @Test
    public void test_setPagerCustomizer_and_getPagerCustomizer() {
        // Create a mock customizer
        ComponentCustomizer customizer = new ComponentCustomizer() {
            @Override
            public void customize(org.lastaflute.di.core.ComponentDef componentDef) {
                // Mock implementation
            }
        };

        // Set the customizer
        pagerCreator.setPagerCustomizer(customizer);

        // Verify it can be retrieved
        assertSame(customizer, pagerCreator.getPagerCustomizer());
    }

    // Test multiple set operations
    @Test
    public void test_setPagerCustomizer_multiple_times() {
        // Create first customizer
        ComponentCustomizer customizer1 = new ComponentCustomizer() {
            @Override
            public void customize(org.lastaflute.di.core.ComponentDef componentDef) {
                // First mock implementation
            }
        };

        // Create second customizer
        ComponentCustomizer customizer2 = new ComponentCustomizer() {
            @Override
            public void customize(org.lastaflute.di.core.ComponentDef componentDef) {
                // Second mock implementation
            }
        };

        // Set first customizer
        pagerCreator.setPagerCustomizer(customizer1);
        assertSame(customizer1, pagerCreator.getPagerCustomizer());

        // Replace with second customizer
        pagerCreator.setPagerCustomizer(customizer2);
        assertSame(customizer2, pagerCreator.getPagerCustomizer());
    }

    // Test setting null customizer
    @Test
    public void test_setPagerCustomizer_null() {
        // Create and set a customizer
        ComponentCustomizer customizer = new ComponentCustomizer() {
            @Override
            public void customize(org.lastaflute.di.core.ComponentDef componentDef) {
                // Mock implementation
            }
        };
        pagerCreator.setPagerCustomizer(customizer);
        assertSame(customizer, pagerCreator.getPagerCustomizer());

        // Set null customizer
        pagerCreator.setPagerCustomizer(null);
        assertNull(pagerCreator.getPagerCustomizer());
    }

    // Test with different NamingConvention configurations
    @Test
    public void test_constructor_with_custom_naming_convention() {
        // Create custom naming convention
        NamingConvention customNamingConvention = new MockNamingConvention() {
            @Override
            public String fromClassNameToComponentName(String className) {
                return super.fromClassNameToComponentName(className);
            }
        };

        // Create PagerCreator with custom naming convention
        PagerCreator customPagerCreator = new PagerCreator(customNamingConvention);

        // Verify initialization
        assertEquals("Pager", customPagerCreator.getNameSuffix());
        assertEquals(InstanceDefFactory.SESSION, customPagerCreator.getInstanceDef());
        assertEquals(AutoBindingDefFactory.NONE, customPagerCreator.getAutoBindingDef());
    }

    // Test inherited behavior from ComponentCreatorImpl
    @Test
    public void test_getNamingConvention() {
        assertSame(namingConvention, pagerCreator.getNamingConvention());
    }

    // Test component name creation
    @Test
    public void test_fromClassNameToComponentName() {
        // Test with class name ending with Pager
        String componentName = namingConvention.fromClassNameToComponentName("com.example.TestPager");
        assertNotNull(componentName);
        assertEquals("testPager", componentName);

        // Test with class name not ending with Pager
        String nonPagerComponentName = namingConvention.fromClassNameToComponentName("com.example.TestService");
        assertNotNull(nonPagerComponentName);
        assertEquals("testService", nonPagerComponentName);
    }

    // Test class name creation from component name
    @Test
    public void test_fromComponentNameToClassName() {
        // Test with valid component name - using custom method
        MockNamingConvention mockNaming = (MockNamingConvention) namingConvention;
        String className = mockNaming.fromComponentNameToPartOfClassName("testPager");
        assertNotNull(className);
        assertEquals("TestPager", className);
    }

    // Mock implementation of NamingConvention for testing
    private static class MockNamingConvention implements NamingConvention {
        @Override
        public String getAssistSuffix() {
            return "Assist";
        }

        @Override
        public String getFormSuffix() {
            return "Form";
        }

        @Override
        public String getActionSuffix() {
            return "Action";
        }

        @Override
        public boolean isSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public String toInterfaceClassName(String className) {
            return className + "Interface";
        }

        @Override
        public String toImplementationClassName(String className) {
            return className + "Impl";
        }

        @Override
        public String fromActionNameToPath(String actionName) {
            return "/" + actionName.toLowerCase();
        }

        @Override
        public String fromPathToActionName(String path) {
            return path.substring(1).toLowerCase();
        }

        @Override
        public String fromClassNameToSuffix(String className) {
            if (className.endsWith("Action")) {
                return "Action";
            } else if (className.endsWith("Form")) {
                return "Form";
            } else if (className.endsWith("Assist")) {
                return "Assist";
            }
            return "";
        }

        @Override
        public String fromComponentNameToSuffix(String componentName) {
            return fromClassNameToSuffix(componentName);
        }

        @Override
        public Class<?> fromComponentNameToClass(String componentName) {
            try {
                return Class.forName(componentName);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        @Override
        public Class<?> toCompleteClass(Class<?> clazz) {
            return clazz;
        }

        @Override
        public String fromSuffixToPackageName(String suffix) {
            return "org.codelibs.fess";
        }

        @Override
        public boolean isIgnoreClassName(String className) {
            return false;
        }

        @Override
        public boolean isHotdeployTargetClassName(String className) {
            return false;
        }

        @Override
        public boolean isTargetClassName(String className) {
            return true;
        }

        @Override
        public boolean isTargetClassName(String packageName, String className) {
            return true;
        }

        @Override
        public String getBizRootPackageName() {
            return "org.codelibs.fess";
        }

        @Override
        public String getJobRootPackageName() {
            return "org.codelibs.fess.job";
        }

        @Override
        public String getWebRootPackageName() {
            return "org.codelibs.fess.app.web";
        }

        @Override
        public String getSubApplicationRootPackageName() {
            return "org.codelibs.fess.app";
        }

        @Override
        public String[] getIgnorePackageNames() {
            return new String[0];
        }

        @Override
        public String[] getRootPackageNames() {
            return new String[] { "org.codelibs.fess" };
        }

        @Override
        public String adjustViewRootPath() {
            return "";
        }

        @Override
        public String fromClassNameToComponentName(String className) {
            if (className == null) {
                return null;
            }
            String[] array = className.split("\\.");
            return array.length > 0 ? decapitalizeName(array[array.length - 1]) : null;
        }

        // Not part of interface
        public String fromComponentNameToClassName(String componentName) {
            if (componentName == null) {
                return null;
            }
            return capitalizeName(componentName);
        }

        public String fromClassNameToShortComponentName(String className) {
            return fromClassNameToComponentName(className);
        }

        // Not part of interface - removed @Override
        public String fromComponentNameToPartOfClassName(String componentName) {
            return capitalizeName(componentName);
        }

        public String fromPropertyNameToFieldName(String propertyName) {
            return propertyName;
        }

        public String fromFieldNameToPropertyName(String fieldName) {
            return fieldName;
        }

        public String fromPropertyNameToGetterName(String propertyName) {
            return "get" + capitalizeName(propertyName);
        }

        public String fromPropertyNameToSetterName(String propertyName) {
            return "set" + capitalizeName(propertyName);
        }

        public boolean isValidPropertyName(String propertyName) {
            return propertyName != null && propertyName.length() > 0;
        }

        private String capitalizeName(String name) {
            if (name == null || name.isEmpty()) {
                return name;
            }
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        private String decapitalizeName(String name) {
            if (name == null || name.isEmpty()) {
                return name;
            }
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }

        @Override
        public String getViewRootPath() {
            return "/WEB-INF/view";
        }

        @Override
        public String getViewExtension() {
            return ".jsp";
        }

        @Override
        public String getImplementationPackageName() {
            return "impl";
        }

        @Override
        public String getConverterPackageName() {
            return "converter";
        }

        @Override
        public String getValidatorPackageName() {
            return "validator";
        }

        @Override
        public String getInterceptorPackageName() {
            return "interceptor";
        }

        @Override
        public String getHelperPackageName() {
            return "helper";
        }

        @Override
        public String getRepositoryPackageName() {
            return "repository";
        }

        @Override
        public String getServicePackageName() {
            return "service";
        }

        @Override
        public String getLogicPackageName() {
            return "logic";
        }

        @Override
        public String getImplementationSuffix() {
            return "Impl";
        }

        @Override
        public String getJobSuffix() {
            return "Job";
        }

        @Override
        public String getConverterSuffix() {
            return "Converter";
        }

        @Override
        public String getValidatorSuffix() {
            return "Validator";
        }

        @Override
        public String getInterceptorSuffix() {
            return "Interceptor";
        }

        @Override
        public String getHelperSuffix() {
            return "Helper";
        }

        @Override
        public String getRepositorySuffix() {
            return "Repository";
        }

        @Override
        public String getServiceSuffix() {
            return "Service";
        }

        @Override
        public String getLogicSuffix() {
            return "Logic";
        }
    }
}