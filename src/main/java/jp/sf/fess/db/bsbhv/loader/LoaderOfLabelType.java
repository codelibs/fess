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

import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.exbhv.LabelTypeBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of LABEL_TYPE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, VALUE, INCLUDED_PATHS, EXCLUDED_PATHS, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, LABEL_TYPE_TO_ROLE_TYPE_MAPPING, WEB_CONFIG_TO_LABEL_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     dataConfigToLabelTypeMappingList, fileConfigToLabelTypeMappingList, labelTypeToRoleTypeMappingList, webConfigToLabelTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfLabelType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<LabelType> _selectedList;

    protected BehaviorSelector _selector;

    protected LabelTypeBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfLabelType ready(final List<LabelType> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected LabelTypeBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(LabelTypeBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<DataConfigToLabelTypeMapping> _referrerDataConfigToLabelTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfDataConfigToLabelTypeMapping> loadDataConfigToLabelTypeMappingList(
            final ConditionBeanSetupper<DataConfigToLabelTypeMappingCB> setupper) {
        myBhv().loadDataConfigToLabelTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<DataConfigToLabelTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<DataConfigToLabelTypeMapping> referrerList) {
                                _referrerDataConfigToLabelTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfDataConfigToLabelTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfDataConfigToLabelTypeMapping> handler) {
                handler.handle(new LoaderOfDataConfigToLabelTypeMapping()
                        .ready(_referrerDataConfigToLabelTypeMappingList,
                                _selector));
            }
        };
    }

    protected List<FileConfigToLabelTypeMapping> _referrerFileConfigToLabelTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfFileConfigToLabelTypeMapping> loadFileConfigToLabelTypeMappingList(
            final ConditionBeanSetupper<FileConfigToLabelTypeMappingCB> setupper) {
        myBhv().loadFileConfigToLabelTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<FileConfigToLabelTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<FileConfigToLabelTypeMapping> referrerList) {
                                _referrerFileConfigToLabelTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfFileConfigToLabelTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfFileConfigToLabelTypeMapping> handler) {
                handler.handle(new LoaderOfFileConfigToLabelTypeMapping()
                        .ready(_referrerFileConfigToLabelTypeMappingList,
                                _selector));
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

    protected List<WebConfigToLabelTypeMapping> _referrerWebConfigToLabelTypeMappingList;

    public NestedReferrerLoaderGateway<LoaderOfWebConfigToLabelTypeMapping> loadWebConfigToLabelTypeMappingList(
            final ConditionBeanSetupper<WebConfigToLabelTypeMappingCB> setupper) {
        myBhv().loadWebConfigToLabelTypeMappingList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<WebConfigToLabelTypeMapping>() {
                            @Override
                            public void handle(
                                    final List<WebConfigToLabelTypeMapping> referrerList) {
                                _referrerWebConfigToLabelTypeMappingList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfWebConfigToLabelTypeMapping>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfWebConfigToLabelTypeMapping> handler) {
                handler.handle(new LoaderOfWebConfigToLabelTypeMapping().ready(
                        _referrerWebConfigToLabelTypeMappingList, _selector));
            }
        };
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<LabelType> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
