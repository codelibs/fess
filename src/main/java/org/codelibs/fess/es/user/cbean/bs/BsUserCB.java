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
package org.codelibs.fess.es.user.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.es.user.bsentity.dbmeta.UserDbm;
import org.codelibs.fess.es.user.cbean.UserCB;
import org.codelibs.fess.es.user.cbean.ca.UserCA;
import org.codelibs.fess.es.user.cbean.ca.bs.BsUserCA;
import org.codelibs.fess.es.user.cbean.cq.UserCQ;
import org.codelibs.fess.es.user.cbean.cq.bs.BsUserCQ;
import org.dbflute.cbean.ConditionQuery;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsUserCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsUserCQ _conditionQuery;
    protected BsUserCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "user";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    public UserCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsUserCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (UserCB) this;
    }

    @Override
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        acceptPK((String) primaryKeyMap.get("_id"));
    }

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====

    @Override
    public SearchRequestBuilder build(SearchRequestBuilder builder) {
        if (_conditionQuery != null) {
            QueryBuilder queryBuilder = _conditionQuery.getQuery();
            if (queryBuilder != null) {
                builder.setQuery(queryBuilder);
            }
            _conditionQuery.getFieldSortBuilderList().forEach(sort -> {
                builder.addSort(sort);
            });
        }

        if (_conditionAggregation != null) {
            _conditionAggregation.getAggregationBuilderList().forEach(builder::addAggregation);
        }

        if (_specification != null) {
            builder.setFetchSource(_specification.columnList.toArray(new String[_specification.columnList.size()]), null);
        }

        return builder;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public BsUserCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsUserCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsUserCQ createLocalCQ() {
        return new UserCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsUserCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsUserCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsUserCA createLocalCA() {
        return new UserCA();
    }

    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    public HpSpecification specify() {
        assertSpecifyPurpose();
        if (_specification == null) {
            _specification = new HpSpecification();
        }
        return _specification;
    }

    protected void assertQueryPurpose() {
    }

    protected void assertAggregationPurpose() {
    }

    protected void assertSpecifyPurpose() {
    }

    public static class HpSpecification {
        protected List<String> columnList = new ArrayList<>();

        public void doColumn(String name) {
            columnList.add(name);
        }

        public void columnId() {
            doColumn("_id");
        }

        public void columnBusinessCategory() {
            doColumn("businessCategory");
        }

        public void columnCarLicense() {
            doColumn("carLicense");
        }

        public void columnCity() {
            doColumn("city");
        }

        public void columnDepartmentNumber() {
            doColumn("departmentNumber");
        }

        public void columnDescription() {
            doColumn("description");
        }

        public void columnDestinationIndicator() {
            doColumn("destinationIndicator");
        }

        public void columnDisplayName() {
            doColumn("displayName");
        }

        public void columnEmployeeNumber() {
            doColumn("employeeNumber");
        }

        public void columnEmployeeType() {
            doColumn("employeeType");
        }

        public void columnFacsimileTelephoneNumber() {
            doColumn("facsimileTelephoneNumber");
        }

        public void columnGidNumber() {
            doColumn("gidNumber");
        }

        public void columnGivenName() {
            doColumn("givenName");
        }

        public void columnGroups() {
            doColumn("groups");
        }

        public void columnHomeDirectory() {
            doColumn("homeDirectory");
        }

        public void columnHomePhone() {
            doColumn("homePhone");
        }

        public void columnHomePostalAddress() {
            doColumn("homePostalAddress");
        }

        public void columnInitials() {
            doColumn("initials");
        }

        public void columnInternationaliSDNNumber() {
            doColumn("internationaliSDNNumber");
        }

        public void columnLabeledURI() {
            doColumn("labeledURI");
        }

        public void columnMail() {
            doColumn("mail");
        }

        public void columnMobile() {
            doColumn("mobile");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnPager() {
            doColumn("pager");
        }

        public void columnPassword() {
            doColumn("password");
        }

        public void columnPhysicalDeliveryOfficeName() {
            doColumn("physicalDeliveryOfficeName");
        }

        public void columnPostOfficeBox() {
            doColumn("postOfficeBox");
        }

        public void columnPostalAddress() {
            doColumn("postalAddress");
        }

        public void columnPostalCode() {
            doColumn("postalCode");
        }

        public void columnPreferredLanguage() {
            doColumn("preferredLanguage");
        }

        public void columnRegisteredAddress() {
            doColumn("registeredAddress");
        }

        public void columnRoles() {
            doColumn("roles");
        }

        public void columnRoomNumber() {
            doColumn("roomNumber");
        }

        public void columnState() {
            doColumn("state");
        }

        public void columnStreet() {
            doColumn("street");
        }

        public void columnSurname() {
            doColumn("surname");
        }

        public void columnTelephoneNumber() {
            doColumn("telephoneNumber");
        }

        public void columnTeletexTerminalIdentifier() {
            doColumn("teletexTerminalIdentifier");
        }

        public void columnTitle() {
            doColumn("title");
        }

        public void columnUidNumber() {
            doColumn("uidNumber");
        }

        public void columnX121Address() {
            doColumn("x121Address");
        }
    }
}
