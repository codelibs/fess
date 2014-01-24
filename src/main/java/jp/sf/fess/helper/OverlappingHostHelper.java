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

package jp.sf.fess.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.db.exentity.OverlappingHost;
import jp.sf.fess.service.OverlappingHostService;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class OverlappingHostHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<OverlappingHost> overlappingHostList;

    @InitMethod
    public void init() {
        if (overlappingHostList == null) {
            overlappingHostList = new ArrayList<OverlappingHost>();
        }
        final OverlappingHostService overlappingHostService = SingletonS2Container
                .getComponent(OverlappingHostService.class);
        overlappingHostList.addAll(overlappingHostService
                .getOverlappingHostList());
    }

    public void setOverlappingHostList(
            final List<OverlappingHost> overlappingHostList) {
        this.overlappingHostList = overlappingHostList;
    }

    public void add(final OverlappingHost overlappingHost) {
        if (overlappingHostList == null) {
            overlappingHostList = new ArrayList<OverlappingHost>();
        }
        overlappingHostList.add(overlappingHost);
    }

    public String convert(final String url) {
        if (overlappingHostList == null) {
            init();
        }

        String newUrl = url;
        for (final OverlappingHost overlappingHost : overlappingHostList) {
            newUrl = overlappingHost.convert(newUrl);
        }
        return newUrl;
    }
}
