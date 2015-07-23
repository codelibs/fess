package org.codelibs.fess.es.bsentity;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.dbflute.Entity;
import org.dbflute.FunCustodial;
import org.dbflute.dbmeta.accessory.EntityModifiedProperties;
import org.dbflute.dbmeta.accessory.EntityUniqueDrivenProperties;
import org.dbflute.util.DfCollectionUtil;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class AbstractEntity implements Entity, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    protected DocMeta docMeta;

    protected final EntityUniqueDrivenProperties __uniqueDrivenProperties = newUniqueDrivenProperties();

    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();

    protected EntityModifiedProperties __specifiedProperties;

    public DocMeta asDocMeta() {
        if (docMeta == null) {
            docMeta = new DocMeta();
        }
        return docMeta;
    }

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

    @Override
    public void markAsSelect() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean createdBySelect() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int instanceHash() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toStringWithRelation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String buildDisplayString(String name, boolean column, boolean relation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void myunlockUndefinedClassificationAccess() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean myundefinedClassificationAccessAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    public abstract Map<String, Object> toSource();

    public class DocMeta {

        protected String id;

        protected Long version;

        private RequestOptionCall<IndexRequestBuilder> indexOption;

        private RequestOptionCall<DeleteRequestBuilder> deleteOption;

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
