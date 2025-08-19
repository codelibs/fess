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
package org.codelibs.fess.sso;

import org.codelibs.fess.unit.UnitFessTestCase;

public class SsoResponseTypeTest extends UnitFessTestCase {

    public void test_values() {
        // Test that values() returns all enum constants
        SsoResponseType[] values = SsoResponseType.values();
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals(SsoResponseType.METADATA, values[0]);
        assertEquals(SsoResponseType.LOGOUT, values[1]);
    }

    public void test_valueOf() {
        // Test valueOf for valid enum names
        assertEquals(SsoResponseType.METADATA, SsoResponseType.valueOf("METADATA"));
        assertEquals(SsoResponseType.LOGOUT, SsoResponseType.valueOf("LOGOUT"));
    }

    public void test_valueOf_invalidName() {
        // Test valueOf with invalid name throws exception
        try {
            SsoResponseType.valueOf("INVALID");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("INVALID"));
        }
    }

    public void test_valueOf_null() {
        // Test valueOf with null throws exception
        try {
            SsoResponseType.valueOf(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    public void test_name() {
        // Test name() method returns correct string representation
        assertEquals("METADATA", SsoResponseType.METADATA.name());
        assertEquals("LOGOUT", SsoResponseType.LOGOUT.name());
    }

    public void test_ordinal() {
        // Test ordinal() method returns correct position
        assertEquals(0, SsoResponseType.METADATA.ordinal());
        assertEquals(1, SsoResponseType.LOGOUT.ordinal());
    }

    public void test_toString() {
        // Test toString() method (default implementation returns name)
        assertEquals("METADATA", SsoResponseType.METADATA.toString());
        assertEquals("LOGOUT", SsoResponseType.LOGOUT.toString());
    }

    public void test_equals() {
        // Test equals method for enum constants
        assertTrue(SsoResponseType.METADATA.equals(SsoResponseType.METADATA));
        assertTrue(SsoResponseType.LOGOUT.equals(SsoResponseType.LOGOUT));
        assertFalse(SsoResponseType.METADATA.equals(SsoResponseType.LOGOUT));
        assertFalse(SsoResponseType.LOGOUT.equals(SsoResponseType.METADATA));
        assertFalse(SsoResponseType.METADATA.equals(null));
        assertFalse(SsoResponseType.METADATA.equals("METADATA"));
    }

    public void test_hashCode() {
        // Test hashCode consistency
        int metadataHashCode = SsoResponseType.METADATA.hashCode();
        int logoutHashCode = SsoResponseType.LOGOUT.hashCode();

        // Hash code should be consistent for the same object
        assertEquals(metadataHashCode, SsoResponseType.METADATA.hashCode());
        assertEquals(logoutHashCode, SsoResponseType.LOGOUT.hashCode());

        // Different enum constants should have different hash codes (usually)
        assertNotSame(metadataHashCode, logoutHashCode);
    }

    public void test_compareTo() {
        // Test compareTo method for enum constants
        assertEquals(0, SsoResponseType.METADATA.compareTo(SsoResponseType.METADATA));
        assertEquals(0, SsoResponseType.LOGOUT.compareTo(SsoResponseType.LOGOUT));
        assertTrue(SsoResponseType.METADATA.compareTo(SsoResponseType.LOGOUT) < 0);
        assertTrue(SsoResponseType.LOGOUT.compareTo(SsoResponseType.METADATA) > 0);
    }

    public void test_compareTo_null() {
        // Test compareTo with null throws exception
        try {
            SsoResponseType.METADATA.compareTo(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    public void test_getDeclaringClass() {
        // Test getDeclaringClass returns correct class
        assertEquals(SsoResponseType.class, SsoResponseType.METADATA.getDeclaringClass());
        assertEquals(SsoResponseType.class, SsoResponseType.LOGOUT.getDeclaringClass());
    }

    public void test_enumConstantReference() {
        // Test that enum constants are singleton instances
        SsoResponseType metadata1 = SsoResponseType.METADATA;
        SsoResponseType metadata2 = SsoResponseType.valueOf("METADATA");
        assertSame(metadata1, metadata2);

        SsoResponseType logout1 = SsoResponseType.LOGOUT;
        SsoResponseType logout2 = SsoResponseType.valueOf("LOGOUT");
        assertSame(logout1, logout2);
    }

    public void test_switchStatement() {
        // Test enum usage in switch statement
        for (SsoResponseType type : SsoResponseType.values()) {
            String result = processResponseType(type);
            assertNotNull(result);

            switch (type) {
            case METADATA:
                assertEquals("metadata", result);
                break;
            case LOGOUT:
                assertEquals("logout", result);
                break;
            default:
                fail("Unexpected enum value: " + type);
            }
        }
    }

    private String processResponseType(SsoResponseType type) {
        switch (type) {
        case METADATA:
            return "metadata";
        case LOGOUT:
            return "logout";
        default:
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}