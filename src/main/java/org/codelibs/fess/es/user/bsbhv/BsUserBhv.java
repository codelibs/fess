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
package org.codelibs.fess.es.user.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.user.allcommon.EsAbstractBehavior;
import org.codelibs.fess.es.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.user.bsentity.dbmeta.UserDbm;
import org.codelibs.fess.es.user.cbean.UserCB;
import org.codelibs.fess.es.user.exentity.User;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsUserBhv extends EsAbstractBehavior<User, UserCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_user.user";
    }

    @Override
    public String asEsIndexType() {
        return "user";
    }

    @Override
    public String asEsSearchType() {
        return "user";
    }

    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    protected <RESULT extends User> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBusinessCategory(DfTypeUtil.toString(source.get("businessCategory")));
            result.setCarLicense(DfTypeUtil.toString(source.get("carLicense")));
            result.setCity(DfTypeUtil.toString(source.get("city")));
            result.setDepartmentNumber(DfTypeUtil.toString(source.get("departmentNumber")));
            result.setDescription(DfTypeUtil.toString(source.get("description")));
            result.setDestinationIndicator(DfTypeUtil.toString(source.get("destinationIndicator")));
            result.setDisplayName(DfTypeUtil.toString(source.get("displayName")));
            result.setEmployeeNumber(DfTypeUtil.toString(source.get("employeeNumber")));
            result.setEmployeeType(DfTypeUtil.toString(source.get("employeeType")));
            result.setFacsimileTelephoneNumber(DfTypeUtil.toString(source.get("facsimileTelephoneNumber")));
            result.setGidNumber(DfTypeUtil.toLong(source.get("gidNumber")));
            result.setGivenName(DfTypeUtil.toString(source.get("givenName")));
            result.setGroups(toStringArray(source.get("groups")));
            result.setHomeDirectory(DfTypeUtil.toString(source.get("homeDirectory")));
            result.setHomePhone(DfTypeUtil.toString(source.get("homePhone")));
            result.setHomePostalAddress(DfTypeUtil.toString(source.get("homePostalAddress")));
            result.setInitials(DfTypeUtil.toString(source.get("initials")));
            result.setInternationaliSDNNumber(DfTypeUtil.toString(source.get("internationaliSDNNumber")));
            result.setLabeledURI(DfTypeUtil.toString(source.get("labeledURI")));
            result.setMail(DfTypeUtil.toString(source.get("mail")));
            result.setMobile(DfTypeUtil.toString(source.get("mobile")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setPager(DfTypeUtil.toString(source.get("pager")));
            result.setPassword(DfTypeUtil.toString(source.get("password")));
            result.setPhysicalDeliveryOfficeName(DfTypeUtil.toString(source.get("physicalDeliveryOfficeName")));
            result.setPostOfficeBox(DfTypeUtil.toString(source.get("postOfficeBox")));
            result.setPostalAddress(DfTypeUtil.toString(source.get("postalAddress")));
            result.setPostalCode(DfTypeUtil.toString(source.get("postalCode")));
            result.setPreferredLanguage(DfTypeUtil.toString(source.get("preferredLanguage")));
            result.setRegisteredAddress(DfTypeUtil.toString(source.get("registeredAddress")));
            result.setRoles(toStringArray(source.get("roles")));
            result.setRoomNumber(DfTypeUtil.toString(source.get("roomNumber")));
            result.setState(DfTypeUtil.toString(source.get("state")));
            result.setStreet(DfTypeUtil.toString(source.get("street")));
            result.setSurname(DfTypeUtil.toString(source.get("surname")));
            result.setTelephoneNumber(DfTypeUtil.toString(source.get("telephoneNumber")));
            result.setTeletexTerminalIdentifier(DfTypeUtil.toString(source.get("teletexTerminalIdentifier")));
            result.setTitle(DfTypeUtil.toString(source.get("title")));
            result.setUidNumber(DfTypeUtil.toLong(source.get("uidNumber")));
            result.setX121Address(DfTypeUtil.toString(source.get("x121Address")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends User> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<UserCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<User> selectEntity(CBCall<UserCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<User> facadeSelectEntity(UserCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends User> OptionalEntity<ENTITY> doSelectOptionalEntity(UserCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public UserCB newConditionBean() {
        return new UserCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public User selectEntityWithDeletedCheck(CBCall<UserCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<User> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<User> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends User> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected UserCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends User> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends User> typeOfSelectedEntity() {
        return User.class;
    }

    @Override
    protected Class<User> typeOfHandlingEntity() {
        return User.class;
    }

    @Override
    protected Class<UserCB> typeOfHandlingConditionBean() {
        return UserCB.class;
    }

    public ListResultBean<User> selectList(CBCall<UserCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<User> selectPage(CBCall<UserCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<User>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<UserCB> cbLambda, EntityRowHandler<User> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<UserCB> cbLambda, EntityRowHandler<List<User>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(User entity) {
        doInsert(entity, null);
    }

    public void insert(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(User entity) {
        doUpdate(entity, null);
    }

    public void update(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(User entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(User entity) {
        doDelete(entity, null);
    }

    public void delete(User entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<UserCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<User> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<User> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<User> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<User> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<User> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<User> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
