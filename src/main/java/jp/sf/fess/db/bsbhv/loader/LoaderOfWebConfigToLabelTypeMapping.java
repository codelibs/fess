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

import jp.sf.fess.db.exbhv.WebConfigToLabelTypeMappingBhv;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.WebConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of WEB_CONFIG_TO_LABEL_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, WEB_CONFIG_ID, LABEL_TYPE_ID
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
 *     LABEL_TYPE, WEB_CRAWLING_CONFIG
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     labelType, webCrawlingConfig
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfWebConfigToLabelTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<WebConfigToLabelTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected WebConfigToLabelTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfWebConfigToLabelTypeMapping ready(
            final List<WebConfigToLabelTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected WebConfigToLabelTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(WebConfigToLabelTypeMappingBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
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
    public List<WebConfigToLabelTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
