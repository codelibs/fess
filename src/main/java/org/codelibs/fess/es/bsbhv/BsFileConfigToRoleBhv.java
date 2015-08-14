package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.FileConfigToRoleDbm;
import org.codelibs.fess.es.cbean.FileConfigToRoleCB;
import org.codelibs.fess.es.exentity.FileConfigToRole;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class BsFileConfigToRoleBhv extends AbstractBehavior<FileConfigToRole, FileConfigToRoleCB> {

    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return ".fess_config";
    }

    @Override
    public String asEsIndexType() {
        return "file_config_to_role";
    }

    @Override
    public String asEsSearchType() {
        return "file_config_to_role";
    }

    @Override
    public FileConfigToRoleDbm asDBMeta() {
        return FileConfigToRoleDbm.getInstance();
    }

    @Override
    protected <RESULT extends FileConfigToRole> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setFileConfigId(DfTypeUtil.toString(source.get("fileConfigId")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setRoleTypeId(DfTypeUtil.toString(source.get("roleTypeId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<FileConfigToRoleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FileConfigToRole> selectEntity(CBCall<FileConfigToRoleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FileConfigToRole> facadeSelectEntity(FileConfigToRoleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToRole> OptionalEntity<ENTITY> doSelectOptionalEntity(FileConfigToRoleCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FileConfigToRoleCB newConditionBean() {
        return new FileConfigToRoleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FileConfigToRole selectEntityWithDeletedCheck(CBCall<FileConfigToRoleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FileConfigToRole> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FileConfigToRole> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileConfigToRole> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FileConfigToRoleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FileConfigToRole> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FileConfigToRole> typeOfSelectedEntity() {
        return FileConfigToRole.class;
    }

    @Override
    protected Class<FileConfigToRole> typeOfHandlingEntity() {
        return FileConfigToRole.class;
    }

    @Override
    protected Class<FileConfigToRoleCB> typeOfHandlingConditionBean() {
        return FileConfigToRoleCB.class;
    }

    public ListResultBean<FileConfigToRole> selectList(CBCall<FileConfigToRoleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FileConfigToRole> selectPage(CBCall<FileConfigToRoleCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<FileConfigToRole>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<FileConfigToRoleCB> cbLambda, EntityRowHandler<FileConfigToRole> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<FileConfigToRoleCB> cbLambda, EntityRowHandler<List<FileConfigToRole>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(FileConfigToRole entity) {
        doInsert(entity, null);
    }

    public void insert(FileConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(FileConfigToRole entity) {
        doUpdate(entity, null);
    }

    public void update(FileConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FileConfigToRole entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FileConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FileConfigToRole entity) {
        doDelete(entity, null);
    }

    public void delete(FileConfigToRole entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<FileConfigToRoleCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<FileConfigToRole> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<FileConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<FileConfigToRole> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<FileConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<FileConfigToRole> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<FileConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
