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

package jp.sf.fess.db.allcommon;

import org.seasar.dbflute.Entity;

/**
 * The interface of entity defined common columns.
 * @author DBFlute(AutoGenerator)
 */
public interface EntityDefinedCommonColumn extends Entity {

    /**
     * Enable common column auto set up. <br />
     * It's only for after disable because the default is enabled.
     */
    void enableCommonColumnAutoSetup();

    /**
     * Disable common column auto set up. <br />
     * This is an old style. You can get the same process
     * by varyingInsert() and varyingUpdate() and so on.
     */
    void disableCommonColumnAutoSetup();

    /**
     * Can the entity set up common column by auto? (basically for Framework)
     * @return The determination, true or false.
     */
    boolean canCommonColumnAutoSetup();
}
