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

import jp.sf.fess.db.exbhv.FileAuthenticationBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of FILE_AUTHENTICATION as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, HOSTNAME, PORT, PROTOCOL_SCHEME, USERNAME, PASSWORD, PARAMETERS, FILE_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     FILE_CRAWLING_CONFIG
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     fileCrawlingConfig
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfFileAuthentication {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<FileAuthentication> _selectedList;

    protected BehaviorSelector _selector;

    protected FileAuthenticationBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfFileAuthentication ready(
            final List<FileAuthentication> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected FileAuthenticationBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(FileAuthenticationBhv.class);
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

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<FileAuthentication> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
