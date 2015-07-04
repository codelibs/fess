package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.PathMappingDbm;
import org.codelibs.fess.es.cbean.PathMappingCB;
import org.codelibs.fess.es.exentity.PathMapping;
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
public abstract class BsPathMappingBhv extends AbstractBehavior<PathMapping, PathMappingCB> {

    @Override
    public String asTableDbName() {
        return "path_mapping";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public PathMappingDbm asDBMeta() {
        return PathMappingDbm.getInstance();
    }

    @Override
    protected <RESULT extends PathMapping> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy((String) source.get("createdBy"));
            result.setCreatedTime((Long) source.get("createdTime"));
            result.setId((String) source.get("id"));
            result.setProcessType((String) source.get("processType"));
            result.setRegex((String) source.get("regex"));
            result.setReplacement((String) source.get("replacement"));
            result.setSortOrder((Integer) source.get("sortOrder"));
            result.setUpdatedBy((String) source.get("updatedBy"));
            result.setUpdatedTime((Long) source.get("updatedTime"));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<PathMappingCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<PathMapping> selectEntity(CBCall<PathMappingCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<PathMapping> facadeSelectEntity(PathMappingCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends PathMapping> OptionalEntity<ENTITY> doSelectOptionalEntity(PathMappingCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public PathMappingCB newConditionBean() {
        return new PathMappingCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public PathMapping selectEntityWithDeletedCheck(CBCall<PathMappingCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<PathMapping> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<PathMapping> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends PathMapping> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected PathMappingCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends PathMapping> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends PathMapping> typeOfSelectedEntity() {
        return PathMapping.class;
    }

    @Override
    protected Class<PathMapping> typeOfHandlingEntity() {
        return PathMapping.class;
    }

    @Override
    protected Class<PathMappingCB> typeOfHandlingConditionBean() {
        return PathMappingCB.class;
    }

    public ListResultBean<PathMapping> selectList(CBCall<PathMappingCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<PathMapping> selectPage(CBCall<PathMappingCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<PathMapping>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(PathMapping entity) {
        doInsert(entity, null);
    }

    public void insert(PathMapping entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(PathMapping entity) {
        doUpdate(entity, null);
    }

    public void update(PathMapping entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(PathMapping entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(PathMapping entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(PathMapping entity) {
        doDelete(entity, null);
    }

    public void delete(PathMapping entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
