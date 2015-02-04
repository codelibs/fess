/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.bsbhv.loader;

import java.util.List;

import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.RoleTypeBhv;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of ROLE_TYPE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, VALUE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
 *
 * [sequence]
 *
 *
 * [identity]
 *     ID
 *
 * [version-no]
 *     VERSION_NO
 *
 * [foreign table]
 *
 *
 * [referrer table]
 *     DATA_CONFIG_TO_ROLE_TYPE_MAPPING, FILE_CONFIG_TO_ROLE_TYPE_MAPPING, LABEL_TYPE_TO_ROLE_TYPE_MAPPING, WEB_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     dataConfigToRoleTypeMappingList, fileConfigToRoleTypeMappingList, labelTypeToRoleTypeMappingList, webConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfRoleType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<RoleType> _selectedList;

    protected BehaviorSelector _selector;

    protected RoleTypeBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfRoleType ready(final List<RoleType> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected RoleTypeBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(RoleTypeBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<DataConfigToRoleTypeMapping> _referrerDataConfigToRoleTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfDataConfigToRoleTypeMapping> loadDataConfigToRoleTypeMappingList(
            final ConditionBeanSetupper<DataConfigToRoleTypeMappingCB> setupper) {
        myBhv().loadDataConfigToRoleTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<DataConfigToRoleTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<DataConfigToRoleTypeMapping> referrerList) {
                                _referrerDataConfigToRoleTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfDataConfigToRoleTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfDataConfigToRoleTypeMapping> handler) {
                handler.handle(new LoaderOfDataConfigToRoleTypeMapping().ready(
                        _referrerDataConfigToRoleTypeMappingList, _selector));
            }
        };
    }

    protected List<FileConfigToRoleTypeMapping> _referrerFileConfigToRoleTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfFileConfigToRoleTypeMapping> loadFileConfigToRoleTypeMappingList(
            final ConditionBeanSetupper<FileConfigToRoleTypeMappingCB> setupper) {
        myBhv().loadFileConfigToRoleTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<FileConfigToRoleTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<FileConfigToRoleTypeMapping> referrerList) {
                                _referrerFileConfigToRoleTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfFileConfigToRoleTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfFileConfigToRoleTypeMapping> handler) {
                handler.handle(new LoaderOfFileConfigToRoleTypeMapping().ready(
                        _referrerFileConfigToRoleTypeMappingList, _selector));
            }
        };
    }

    protected List<LabelTypeToRoleTypeMapping> _referrerLabelTypeToRoleTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfLabelTypeToRoleTypeMapping> loadLabelTypeToRoleTypeMappingList(
            final ConditionBeanSetupper<LabelTypeToRoleTypeMappingCB> setupper) {
        myBhv().loadLabelTypeToRoleTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<LabelTypeToRoleTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<LabelTypeToRoleTypeMapping> referrerList) {
                                _referrerLabelTypeToRoleTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfLabelTypeToRoleTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfLabelTypeToRoleTypeMapping> handler) {
                handler.handle(new LoaderOfLabelTypeToRoleTypeMapping().ready(
                        _referrerLabelTypeToRoleTypeMappingList, _selector));
            }
        };
    }

    protected List<WebConfigToRoleTypeMapping> _referrerWebConfigToRoleTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfWebConfigToRoleTypeMapping> loadWebConfigToRoleTypeMappingList(
            final ConditionBeanSetupper<WebConfigToRoleTypeMappingCB> setupper) {
        myBhv().loadWebConfigToRoleTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<WebConfigToRoleTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<WebConfigToRoleTypeMapping> referrerList) {
                                _referrerWebConfigToRoleTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfWebConfigToRoleTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfWebConfigToRoleTypeMapping> handler) {
                handler.handle(new LoaderOfWebConfigToRoleTypeMapping().ready(
                        _referrerWebConfigToRoleTypeMappingList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<RoleType> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
