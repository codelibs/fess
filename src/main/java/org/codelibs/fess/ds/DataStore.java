/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.ds;

import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;

/**
 * The interface for DataStore.
 */
public interface DataStore {

    /**
     * Store the data.
     * @param config The data configuration.
     * @param callback The callback.
     * @param initParamMap The initial parameters.
     */
    void store(DataConfig config, IndexUpdateCallback callback, DataStoreParams initParamMap);

    /**
     * Stop the data store.
     */
    void stop();

}
