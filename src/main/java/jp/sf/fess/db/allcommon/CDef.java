/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.allcommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ClassificationCodeType;
import org.seasar.dbflute.jdbc.ClassificationMeta;
import org.seasar.dbflute.jdbc.ClassificationUndefinedHandlingType;

/**
 * The definition of classification.
 * @author DBFlute(AutoGenerator)
 */
public interface CDef extends Classification {

    /** The empty array for no sisters. */
    String[] EMPTY_SISTERS = new String[] {};

    /** The empty map for no sub-items. */
    @SuppressWarnings("unchecked")
    Map<String, Object> EMPTY_SUB_ITEM_MAP = Collections.EMPTY_MAP;

    /**
     * Processing Type
     */
    public enum ProcessType implements CDef {
        /** Crawling: Process When Crawling */
        Crawling("C", "Crawling", EMPTY_SISTERS),
        /** Displaying: Process When Displaying */
        Displaying("D", "Displaying", EMPTY_SISTERS),
        /** Both: Process When Crawling/Displaying */
        Both("B", "Both", EMPTY_SISTERS);
        private static final Map<String, ProcessType> _codeValueMap = new HashMap<String, ProcessType>();
        static {
            for (final ProcessType value : values()) {
                _codeValueMap.put(value.code().toLowerCase(), value);
                for (final String sister : value.sisters()) {
                    _codeValueMap.put(sister.toLowerCase(), value);
                }
            }
        }

        private String _code;

        private String _alias;

        private String[] _sisters;

        private ProcessType(final String code, final String alias,
                final String[] sisters) {
            _code = code;
            _alias = alias;
            _sisters = sisters;
        }

        @Override
        public String code() {
            return _code;
        }

        @Override
        public String alias() {
            return _alias;
        }

        private String[] sisters() {
            return _sisters;
        }

        @Override
        public Map<String, Object> subItemMap() {
            return EMPTY_SUB_ITEM_MAP;
        }

        @Override
        public ClassificationMeta meta() {
            return CDef.DefMeta.ProcessType;
        }

        @Override
        public boolean inGroup(final String groupName) {
            return false;
        }

        /**
         * Get the classification by the code. (CaseInsensitive)
         * @param code The value of code, which is case-insensitive. (NullAllowed: if null, returns null)
         * @return The instance of the corresponding classification to the code. (NullAllowed: if not found, returns null)
         */
        public static ProcessType codeOf(final Object code) {
            if (code == null) {
                return null;
            }
            if (code instanceof ProcessType) {
                return (ProcessType) code;
            }
            return _codeValueMap.get(code.toString().toLowerCase());
        }

        /**
         * Get the classification by the name (also called 'value' in ENUM world).
         * @param name The string of name, which is case-sensitive. (NullAllowed: if null, returns null)
         * @return The instance of the corresponding classification to the name. (NullAllowed: if not found, returns null)
         */
        public static ProcessType nameOf(final String name) {
            if (name == null) {
                return null;
            }
            try {
                return valueOf(name);
            } catch (final RuntimeException ignored) {
                return null;
            }
        }

        /**
         * Get the list of all classification elements. (returns new copied list)
         * @return The list of all classification elements. (NotNull)
         */
        public static List<ProcessType> listAll() {
            return new ArrayList<ProcessType>(Arrays.asList(values()));
        }

        /**
         * Get the list of classification elements in the specified group. (returns new copied list) <br />
         * @param groupName The string of group name, which is case-sensitive. (NullAllowed: if null, returns empty list)
         * @return The list of classification elements in the group. (NotNull)
         */
        public static List<ProcessType> groupOf(final String groupName) {
            return new ArrayList<ProcessType>(4);
        }

        @Override
        public String toString() {
            return code();
        }
    }

    /**
     * Access Type
     */
    public enum AccessType implements CDef {
        /** Web: Web */
        Web("W", "Web", EMPTY_SISTERS),
        /** Xml: Xml */
        Xml("X", "Xml", EMPTY_SISTERS),
        /** Json: Json */
        Json("J", "Json", EMPTY_SISTERS),
        /** Others: Others */
        Others("O", "Others", EMPTY_SISTERS);
        private static final Map<String, AccessType> _codeValueMap = new HashMap<String, AccessType>();
        static {
            for (final AccessType value : values()) {
                _codeValueMap.put(value.code().toLowerCase(), value);
                for (final String sister : value.sisters()) {
                    _codeValueMap.put(sister.toLowerCase(), value);
                }
            }
        }

        private String _code;

        private String _alias;

        private String[] _sisters;

        private AccessType(final String code, final String alias,
                final String[] sisters) {
            _code = code;
            _alias = alias;
            _sisters = sisters;
        }

        @Override
        public String code() {
            return _code;
        }

        @Override
        public String alias() {
            return _alias;
        }

        private String[] sisters() {
            return _sisters;
        }

        @Override
        public Map<String, Object> subItemMap() {
            return EMPTY_SUB_ITEM_MAP;
        }

        @Override
        public ClassificationMeta meta() {
            return CDef.DefMeta.AccessType;
        }

        @Override
        public boolean inGroup(final String groupName) {
            return false;
        }

        /**
         * Get the classification by the code. (CaseInsensitive)
         * @param code The value of code, which is case-insensitive. (NullAllowed: if null, returns null)
         * @return The instance of the corresponding classification to the code. (NullAllowed: if not found, returns null)
         */
        public static AccessType codeOf(final Object code) {
            if (code == null) {
                return null;
            }
            if (code instanceof AccessType) {
                return (AccessType) code;
            }
            return _codeValueMap.get(code.toString().toLowerCase());
        }

        /**
         * Get the classification by the name (also called 'value' in ENUM world).
         * @param name The string of name, which is case-sensitive. (NullAllowed: if null, returns null)
         * @return The instance of the corresponding classification to the name. (NullAllowed: if not found, returns null)
         */
        public static AccessType nameOf(final String name) {
            if (name == null) {
                return null;
            }
            try {
                return valueOf(name);
            } catch (final RuntimeException ignored) {
                return null;
            }
        }

        /**
         * Get the list of all classification elements. (returns new copied list)
         * @return The list of all classification elements. (NotNull)
         */
        public static List<AccessType> listAll() {
            return new ArrayList<AccessType>(Arrays.asList(values()));
        }

        /**
         * Get the list of classification elements in the specified group. (returns new copied list) <br />
         * @param groupName The string of group name, which is case-sensitive. (NullAllowed: if null, returns empty list)
         * @return The list of classification elements in the group. (NotNull)
         */
        public static List<AccessType> groupOf(final String groupName) {
            return new ArrayList<AccessType>(4);
        }

        @Override
        public String toString() {
            return code();
        }
    }

    public enum DefMeta implements ClassificationMeta {
        /** Processing Type */
        ProcessType,
        /** Access Type */
        AccessType;
        @Override
        public String classificationName() {
            return name(); // same as definition name
        }

        @Override
        public Classification codeOf(final Object code) {
            if ("ProcessType".equals(name())) {
                return CDef.ProcessType.codeOf(code);
            }
            if ("AccessType".equals(name())) {
                return CDef.AccessType.codeOf(code);
            }
            throw new IllegalStateException("Unknown definition: " + this); // basically unreachable
        }

        @Override
        public Classification nameOf(final String name) {
            if ("ProcessType".equals(name())) {
                return CDef.ProcessType.valueOf(name);
            }
            if ("AccessType".equals(name())) {
                return CDef.AccessType.valueOf(name);
            }
            throw new IllegalStateException("Unknown definition: " + this); // basically unreachable
        }

        @Override
        public List<Classification> listAll() {
            if ("ProcessType".equals(name())) {
                return toClassificationList(CDef.ProcessType.listAll());
            }
            if ("AccessType".equals(name())) {
                return toClassificationList(CDef.AccessType.listAll());
            }
            throw new IllegalStateException("Unknown definition: " + this); // basically unreachable
        }

        @Override
        public List<Classification> groupOf(final String groupName) {
            if ("ProcessType".equals(name())) {
                return toClassificationList(CDef.ProcessType.groupOf(groupName));
            }
            if ("AccessType".equals(name())) {
                return toClassificationList(CDef.AccessType.groupOf(groupName));
            }
            throw new IllegalStateException("Unknown definition: " + this); // basically unreachable
        }

        @SuppressWarnings("unchecked")
        private List<Classification> toClassificationList(final List<?> clsList) {
            return (List<Classification>) clsList;
        }

        @Override
        public ClassificationCodeType codeType() {
            if ("ProcessType".equals(name())) {
                return ClassificationCodeType.String;
            }
            if ("AccessType".equals(name())) {
                return ClassificationCodeType.String;
            }
            return ClassificationCodeType.String; // as default
        }

        @Override
        public ClassificationUndefinedHandlingType undefinedHandlingType() {
            if ("ProcessType".equals(name())) {
                return ClassificationUndefinedHandlingType.EXCEPTION;
            }
            if ("AccessType".equals(name())) {
                return ClassificationUndefinedHandlingType.EXCEPTION;
            }
            return ClassificationUndefinedHandlingType.LOGGING; // as default
        }
    }
}
