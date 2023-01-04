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
package org.codelibs.fess.es.user.allcommon;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.dbflute.Entity;
import org.dbflute.FunCustodial;
import org.dbflute.dbmeta.accessory.EntityModifiedProperties;
import org.dbflute.dbmeta.accessory.EntityUniqueDrivenProperties;
import org.dbflute.util.DfCollectionUtil;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class EsAbstractEntity implements Entity, Serializable, Cloneable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DocMeta docMeta;
    protected final EntityUniqueDrivenProperties __uniqueDrivenProperties = newUniqueDrivenProperties();
    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();
    protected EntityModifiedProperties __specifiedProperties;
    protected boolean __createdBySelect;

    // ===================================================================================
    //                                                                            Doc Meta
    //                                                                            ========
    public DocMeta asDocMeta() {
        if (docMeta == null) {
            docMeta = new DocMeta();
        }
        return docMeta;
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    public Set<String> mymodifiedProperties() {
        return __modifiedProperties.getPropertyNames();
    }

    public void mymodifyProperty(String propertyName) {
        registerModifiedProperty(propertyName);
    }

    public void mymodifyPropertyCancel(String propertyName) {
        __modifiedProperties.remove(propertyName);
    }

    public void clearModifiedInfo() {
        __modifiedProperties.clear();
    }

    public boolean hasModification() {
        return !__modifiedProperties.isEmpty();
    }

    protected EntityModifiedProperties newModifiedProperties() {
        return new EntityModifiedProperties();
    }

    protected void registerModifiedProperty(String propertyName) {
        __modifiedProperties.addPropertyName(propertyName);
        registerSpecifiedProperty(propertyName); // synchronize if exists, basically for user's manual call
    }

    public void modifiedToSpecified() {
        if (__modifiedProperties.isEmpty()) {
            return; // basically no way when called in Framework (because called when SpecifyColumn exists)
        }
        __specifiedProperties = newModifiedProperties();
        __specifiedProperties.accept(__modifiedProperties);
    }

    // ===================================================================================
    //                                                                Specified Properties
    //                                                                ====================
    public Set<String> myspecifiedProperties() {
        if (__specifiedProperties != null) {
            return __specifiedProperties.getPropertyNames();
        }
        return DfCollectionUtil.emptySet();
    }

    public void myspecifyProperty(String propertyName) {
        registerSpecifiedProperty(propertyName);
    }

    public void myspecifyPropertyCancel(String propertyName) {
        if (__specifiedProperties != null) {
            __specifiedProperties.remove(propertyName);
        }
    }

    public void clearSpecifiedInfo() {
        if (__specifiedProperties != null) {
            __specifiedProperties.clear();
        }
    }

    protected void checkSpecifiedProperty(String propertyName) {
        FunCustodial.checkSpecifiedProperty(this, propertyName, __specifiedProperties);
    }

    protected void registerSpecifiedProperty(String propertyName) { // basically called by modified property registration
        if (__specifiedProperties != null) { // normally false, true if e.g. setting after selected
            __specifiedProperties.addPropertyName(propertyName);
        }
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    @Override
    public boolean hasPrimaryKeyValue() {
        return asDocMeta().id() != null;
    }

    protected EntityUniqueDrivenProperties newUniqueDrivenProperties() {
        return new EntityUniqueDrivenProperties();
    }

    @Override
    public Set<String> myuniqueDrivenProperties() {
        return __uniqueDrivenProperties.getPropertyNames();
    }

    @Override
    public void myuniqueByProperty(String propertyName) {
        __uniqueDrivenProperties.addPropertyName(propertyName);
    }

    @Override
    public void myuniqueByPropertyCancel(String propertyName) {
        __uniqueDrivenProperties.remove(propertyName);
    }

    @Override
    public void clearUniqueDrivenInfo() {
        __uniqueDrivenProperties.clear();
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    @Override
    public void myunlockUndefinedClassificationAccess() {
    }

    @Override
    public boolean myundefinedClassificationAccessAllowed() {
        return false;
    }

    // ===================================================================================
    //                                                                     Birthplace Mark
    //                                                                     ===============
    @Override
    public void markAsSelect() {
        __createdBySelect = true;
    }

    @Override
    public boolean createdBySelect() {
        return __createdBySelect;
    }

    public void clearMarkAsSelect() {
        __createdBySelect = false;
    }

    // ===================================================================================
    //                                                                        Empty String
    //                                                                        ============
    protected String convertEmptyToNull(String value) {
        return (value != null && value.length() == 0) ? null : value;
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    public abstract Map<String, Object> toSource();

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    // #pending hashCode(), equals()
    @Override
    public int instanceHash() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + doBuildColumnString(", ") + "@" + Integer.toHexString(hashCode());
    }

    protected abstract String doBuildColumnString(String dm);

    @Override
    public String toStringWithRelation() { // #pending
        return toString();
    }

    @Override
    public String buildDisplayString(String name, boolean column, boolean relation) { // #pending
        return toString();
    }

    // ===================================================================================
    //                                                                        Assist Class
    //                                                                        ============
    public class DocMeta implements Serializable {

        private static final long serialVersionUID = 1L;

        protected String id;

        protected Long version;

        protected Long seqNo;

        protected Long primaryTerm;

        private transient RequestOptionCall<IndexRequestBuilder> indexOption;

        private transient RequestOptionCall<DeleteRequestBuilder> deleteOption;

        public DocMeta id(String id) {
            this.id = id;
            myuniqueByProperty("_id");
            return this;
        }

        public String id() {
            return id;
        }

        public DocMeta version(Long version) {
            this.version = version;
            return this;
        }

        public Long version() {
            return version;
        }

        public DocMeta seqNo(Long seqNo) {
            this.seqNo = seqNo;
            return this;
        }

        public Long seqNo() {
            return seqNo;
        }

        public DocMeta primaryTerm(Long primaryTerm) {
            this.primaryTerm = primaryTerm;
            return this;
        }

        public Long primaryTerm() {
            return primaryTerm;
        }

        public DocMeta indexOption(RequestOptionCall<IndexRequestBuilder> builder) {
            this.indexOption = builder;
            return this;
        }

        public RequestOptionCall<IndexRequestBuilder> indexOption() {
            return indexOption;
        }

        public DocMeta deleteOption(RequestOptionCall<DeleteRequestBuilder> builder) {
            this.deleteOption = builder;
            return this;
        }

        public RequestOptionCall<DeleteRequestBuilder> deleteOption() {
            return deleteOption;
        }
    }

    @FunctionalInterface
    public interface RequestOptionCall<OP> {
        void callback(OP op);
    }
}
