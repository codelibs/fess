/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.codelibs.fess.app.service.DuplicateHostService;
import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.lastaflute.di.core.SingletonLaContainer;

public class DuplicateHostHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<DuplicateHost> duplicateHostList;

    @PostConstruct
    public void init() {
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<DuplicateHost>();
        }
        final DuplicateHostService duplicateHostService = SingletonLaContainer.getComponent(DuplicateHostService.class);
        duplicateHostList.addAll(duplicateHostService.getDuplicateHostList());
    }

    public void setDuplicateHostList(final List<DuplicateHost> duplicateHostList) {
        this.duplicateHostList = duplicateHostList;
    }

    public void add(final DuplicateHost duplicateHost) {
        if (duplicateHostList == null) {
            duplicateHostList = new ArrayList<DuplicateHost>();
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
