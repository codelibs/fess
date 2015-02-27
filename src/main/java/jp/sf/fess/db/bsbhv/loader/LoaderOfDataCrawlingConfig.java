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
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.DataCrawlingConfigBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of DATA_CRAWLING_CONFIG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, HANDLER_NAME, HANDLER_PARAMETER, HANDLER_SCRIPT, BOOST, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     DATA_CONFIG_TO_LABEL_TYPE_MAPPING, DATA_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     dataConfigToLabelTypeMappingList, dataConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfDataCrawlingConfig {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<DataCrawlingConfig> _selectedList;

    protected BehaviorSelector _selector;

    protected DataCrawlingConfigBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfDataCrawlingConfig ready(
            final List<DataCrawlingConfig> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected DataCrawlingConfigBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(DataCrawlingConfigBhv.class);
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

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DataCrawlingConfig> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
