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

import jp.sf.fess.db.exbhv.WebConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.db.exentity.WebConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of WEB_CONFIG_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, WEB_CONFIG_ID, ROLE_TYPE_ID
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
 *     ROLE_TYPE, WEB_CRAWLING_CONFIG
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     roleType, webCrawlingConfig
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfWebConfigToRoleTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<WebConfigToRoleTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected WebConfigToRoleTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfWebConfigToRoleTypeMapping ready(
            final List<WebConfigToRoleTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected WebConfigToRoleTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(WebConfigToRoleTypeMappingBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
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

    protected LoaderOfWebCrawlingConfig _foreignWebCrawlingConfigLoader;

    public LoaderOfWebCrawlingConfig pulloutWebCrawlingConfig() {
        if (_foreignWebCrawlingConfigLoader != null) {
            return _foreignWebCrawlingConfigLoader;
        }
        final List<WebCrawlingConfig> pulledList = myBhv()
                .pulloutWebCrawlingConfig(_selectedList);
        _foreignWebCrawlingConfigLoader = new LoaderOfWebCrawlingConfig()
                .ready(pulledList, _selector);
        return _foreignWebCrawlingConfigLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<WebConfigToRoleTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
