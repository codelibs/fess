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

import jp.sf.fess.db.cbean.FileAuthenticationCB;
import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.exbhv.FileCrawlingConfigBhv;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.NestedReferrerLoaderGateway;
import org.seasar.dbflute.bhv.ReferrerListHandler;
import org.seasar.dbflute.bhv.ReferrerLoaderHandler;

/**
 * The referrer loader of FILE_CRAWLING_CONFIG as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, NAME, PATHS, INCLUDED_PATHS, EXCLUDED_PATHS, INCLUDED_DOC_PATHS, EXCLUDED_DOC_PATHS, CONFIG_PARAMETER, DEPTH, MAX_ACCESS_COUNT, NUM_OF_THREAD, INTERVAL_TIME, BOOST, AVAILABLE, SORT_ORDER, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
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
 *     FILE_AUTHENTICATION, FILE_CONFIG_TO_LABEL_TYPE_MAPPING, FILE_CONFIG_TO_ROLE_TYPE_MAPPING
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     fileAuthenticationList, fileConfigToLabelTypeMappingList, fileConfigToRoleTypeMappingList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfFileCrawlingConfig {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<FileCrawlingConfig> _selectedList;

    protected BehaviorSelector _selector;

    protected FileCrawlingConfigBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfFileCrawlingConfig ready(
            final List<FileCrawlingConfig> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected FileCrawlingConfigBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(FileCrawlingConfigBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    protected List<FileAuthentication> _referrerFileAuthenticationList;

    public NestedReferrerLoaderGateway<LoaderOfFileAuthentication> loadFileAuthenticationList(
            final ConditionBeanSetupper<FileAuthenticationCB> setupper) {
        myBhv().loadFileAuthenticationList(_selectedList, setupper)
                .withNestedReferrer(
                        new ReferrerListHandler<FileAuthentication>() {
                            @Override
                            public void handle(
                                    final List<FileAuthentication> referrerList) {
                                _referrerFileAuthenticationList = referrerList;
                            }
                        });
        return new NestedReferrerLoaderGateway<LoaderOfFileAuthentication>() {
            @Override
            public void withNestedReferrer(
                    final ReferrerLoaderHandler<LoaderOfFileAuthentication> handler) {
                handler.handle(new LoaderOfFileAuthentication().ready(
                        _referrerFileAuthenticationList, _selector));
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

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<FileCrawlingConfig> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
