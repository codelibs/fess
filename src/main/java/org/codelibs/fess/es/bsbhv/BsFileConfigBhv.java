package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.FileConfigDbm;
import org.codelibs.fess.es.cbean.FileConfigCB;
import org.codelibs.fess.es.exentity.FileConfig;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class BsFileConfigBhv extends AbstractBehavior<FileConfig, FileConfigCB> {

    @Override
    public String asTableDbName() {
        return "file_config";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public FileConfigDbm asDBMeta() {
        return FileConfigDbm.getInstance();
    }

    @Override
    protected <RESULT extends FileConfig> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable(toBoolean(source.get("available")));
            result.setBoost(toFloat(source.get("boost")));
            result.setConfigParameter(toString(source.get("configParameter")));
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setDepth(toInteger(source.get("depth")));
            result.setExcludedDocPaths(toString(source.get("excludedDocPaths")));
            result.setExcludedPaths(toString(source.get("excludedPaths")));
            result.setId(toString(source.get("id")));
            result.setIncludedDocPaths(toString(source.get("includedDocPaths")));
            result.setIncludedPaths(toString(source.get("includedPaths")));
            result.setIntervalTime(toInteger(source.get("intervalTime")));
            result.setMaxAccessCount(toLong(source.get("maxAccessCount")));
            result.setName(toString(source.get("name")));
            result.setNumOfThread(toInteger(source.get("numOfThread")));
            result.setPaths(toString(source.get("paths")));
            result.setSortOrder(toInteger(source.get("sortOrder")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<FileConfigCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FileConfig> selectEntity(CBCall<FileConfigCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FileConfig> facadeSelectEntity(FileConfigCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(FileConfigCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FileConfigCB newConditionBean() {
        return new FileConfigCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FileConfig selectEntityWithDeletedCheck(CBCall<FileConfigCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FileConfig> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FileConfig> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfig> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FileConfigCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FileConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FileConfig> typeOfSelectedEntity() {
        return FileConfig.class;
    }

    @Override
    protected Class<FileConfig> typeOfHandlingEntity() {
        return FileConfig.class;
    }

    @Override
    protected Class<FileConfigCB> typeOfHandlingConditionBean() {
        return FileConfigCB.class;
    }

    public ListResultBean<FileConfig> selectList(CBCall<FileConfigCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FileConfig> selectPage(CBCall<FileConfigCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<FileConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(FileConfig entity) {
        doInsert(entity, null);
    }

    public void insert(FileConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(FileConfig entity) {
        doUpdate(entity, null);
    }

    public void update(FileConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FileConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FileConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FileConfig entity) {
        doDelete(entity, null);
    }

    public void delete(FileConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
