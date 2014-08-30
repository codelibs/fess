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

import jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of DATA_CONFIG_TO_LABEL_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, DATA_CONFIG_ID, LABEL_TYPE_ID
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
 *     DATA_CRAWLING_CONFIG, LABEL_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     dataCrawlingConfig, labelType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfDataConfigToLabelTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<DataConfigToLabelTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected DataConfigToLabelTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfDataConfigToLabelTypeMapping ready(
            final List<DataConfigToLabelTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected DataConfigToLabelTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(DataConfigToLabelTypeMappingBhv.class);
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

    protected LoaderOfLabelType _foreignLabelTypeLoader;

    public LoaderOfLabelType pulloutLabelType() {
        if (_foreignLabelTypeLoader != null) {
            return _foreignLabelTypeLoader;
        }
        final List<LabelType> pulledList = myBhv().pulloutLabelType(
                _selectedList);
        _foreignLabelTypeLoader = new LoaderOfLabelType().ready(pulledList,
                _selector);
        return _foreignLabelTypeLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DataConfigToLabelTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
