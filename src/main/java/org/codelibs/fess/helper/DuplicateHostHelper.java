/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.DuplicateHostService;
import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.codelibs.fess.util.ComponentUtil;

public class DuplicateHostHelper {
    private static final Logger logger = LogManager.getLogger(DuplicateHostHelper.class);

    protected List<DuplicateHost> duplicateHostList;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<>();
        }
        final DuplicateHostService duplicateHostService = ComponentUtil.getComponent(DuplicateHostService.class);
        duplicateHostList.addAll(duplicateHostService.getDuplicateHostList());
    }

    public void setDuplicateHostList(final List<DuplicateHost> duplicateHostList) {
        this.duplicateHostList = duplicateHostList;
    }

    public void add(final DuplicateHost duplicateHost) {
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<>();
        }
        duplicateHostList.add(duplicateHost);
    }

    public String convert(final String url) {
        if (duplicateHostList == null) {
            init();
        }

        String newUrl = url;
        for (final DuplicateHost duplicateHost : duplicateHostList) {
            newUrl = duplicateHost.convert(newUrl);
        }
        return newUrl;
    }
}
