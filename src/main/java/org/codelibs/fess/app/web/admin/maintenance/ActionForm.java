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
package org.codelibs.fess.app.web.admin.maintenance;

import org.codelibs.fess.util.ComponentUtil;

import jakarta.validation.constraints.Size;

/**
 * The form for maintenance actions.
 */
public class ActionForm {
    /**
     * Default constructor.
     */
    public ActionForm() {
        // nothing
    }

    /**
     * The flag to replace aliases.
     */
    @Size(max = 10)
    public String replaceAliases;

    /**
     * The flag to reset dictionaries.
     */
    @Size(max = 10)
    public String resetDictionaries;

    /**
     * The number of shards for doc.
     */
    @Size(max = 10)
    public String numberOfShardsForDoc = ComponentUtil.getFessConfig().getIndexNumberOfShards();

    /**
     * The auto expand replicas for doc.
     */
    @Size(max = 10)
    public String autoExpandReplicasForDoc = ComponentUtil.getFessConfig().getIndexAutoExpandReplicas();
}
