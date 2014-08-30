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

import jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
import jp.sf.fess.db.exentity.RoleType;

import org.seasar.dbflute.BehaviorSelector;

/**
 * The referrer loader of LABEL_TYPE_TO_ROLE_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, LABEL_TYPE_ID, ROLE_TYPE_ID
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
 *     LABEL_TYPE, ROLE_TYPE
 *
 * [referrer table]
 *
 *
 * [foreign property]
 *     labelType, roleType
 *
 * [referrer property]
 *
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfLabelTypeToRoleTypeMapping {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<LabelTypeToRoleTypeMapping> _selectedList;

    protected BehaviorSelector _selector;

    protected LabelTypeToRoleTypeMappingBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfLabelTypeToRoleTypeMapping ready(
            final List<LabelTypeToRoleTypeMapping> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected LabelTypeToRoleTypeMappingBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(LabelTypeToRoleTypeMappingBhv.class);
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
    public List<LabelTypeToRoleTypeMapping> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
