/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.RoleType;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of DATA_CONFIG_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, DATA_CONFIG_ID, ROLE_TYPE_ID
 *
 * [sequence]
 *
 *
 * [identity]
 *     ID
 *
 * [version-no]
 *
 *
 * [foreign table]
 *     DATA_CRAWLING_CONFIG, ROLE_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     dataCrawlingConfig, roleType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfDataConfigToRoleTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<DataConfigToRoleTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected DataConfigToRoleTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfDataConfigToRoleTypeMapping ready(
            final List<DataConfigToRoleTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected DataConfigToRoleTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(DataConfigToRoleTypeMappingBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfDataCrawlingConfig _foreignDataCrawlingConfigLoader;

    public LoaderOfDataCrawlingConfig pulloutDataCrawlingConfig() {
        if (_foreignDataCrawlingConfigLoader != null) {
            return _foreignDataCrawlingConfigLoader;
        }
        final List<DataCrawlingConfig> pulledList = myBhv()
                .pulloutDataCrawlingConfig(_selectedList);
        _foreignDataCrawlingConfigLoader = new LoaderOfDataCrawlingConfig()
                .ready(pulledList, _selector);
        return _foreignDataCrawlingConfigLoader;
    }

    protected LoaderOfRoleType _foreignRoleTypeLoader;

    public LoaderOfRoleType pulloutRoleType() {
        if (_foreignRoleTypeLoader != null) {
            return _foreignRoleTypeLoader;
        }
        final List<RoleType> pulledList = myBhv()
                .pulloutRoleType(_selectedList);
        _foreignRoleTypeLoader = new LoaderOfRoleType().ready(pulledList,
                _selector);
        return _foreignRoleTypeLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DataConfigToRoleTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
