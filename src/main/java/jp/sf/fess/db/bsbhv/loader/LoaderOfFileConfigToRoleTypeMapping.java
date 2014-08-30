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

import jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.RoleType;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of FILE_CONFIG_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, FILE_CONFIG_ID, ROLE_TYPE_ID
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
 *     FILE_CRAWLING_CONFIG, ROLE_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     fileCrawlingConfig, roleType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfFileConfigToRoleTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<FileConfigToRoleTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected FileConfigToRoleTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfFileConfigToRoleTypeMapping ready(
            final List<FileConfigToRoleTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected FileConfigToRoleTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(FileConfigToRoleTypeMappingBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfFileCrawlingConfig _foreignFileCrawlingConfigLoader;

    public LoaderOfFileCrawlingConfig pulloutFileCrawlingConfig() {
        if (_foreignFileCrawlingConfigLoader != null) {
            return _foreignFileCrawlingConfigLoader;
        }
        final List<FileCrawlingConfig> pulledList = myBhv()
                .pulloutFileCrawlingConfig(_selectedList);
        _foreignFileCrawlingConfigLoader = new LoaderOfFileCrawlingConfig()
                .ready(pulledList, _selector);
        return _foreignFileCrawlingConfigLoader;
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
    public List<FileConfigToRoleTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
