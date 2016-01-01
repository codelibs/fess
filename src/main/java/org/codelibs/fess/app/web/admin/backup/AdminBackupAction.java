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
package org.codelibs.fess.app.web.admin.backup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.RenderDataUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.fess.util.StreamUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author shinsuke
 */
public class AdminBackupAction extends FessAdminAction {

    @Resource
    private SystemHelper systemHelper;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameLog()));
    }

    @Execute
    public HtmlResponse index() {
        return asIndexHtml();
    }

    @Execute
    public ActionResponse download(final String id) {
        if (StreamUtil.of(fessConfig.getIndexBackupTargetsAsArray()).anyMatch(s -> s.equals(id))) {
            return asStream(id + ".bulk").contentTypeOctetStream().stream(
                    out -> {
                        try (CurlResponse response =
                                Curl.get(ResourceUtil.getElasticsearchHttpUrl() + "/" + id + "/_data").param("format", "json").execute()) {
                            out.write(response.getContentAsStream());
                        }
                    });
        }
        throwValidationError(messages -> messages.addErrorsCouldNotFindBackupIndex(GLOBAL), () -> {
            return asIndexHtml();
        });
        return redirect(getClass()); // no-op
    }

    private List<Map<String, String>> getBackupItems() {
        return StreamUtil.of(fessConfig.getIndexBackupTargetsAsArray()).filter(name -> StringUtil.isNotBlank(name)).map(name -> {
            final Map<String, String> map = new HashMap<>();
            map.put("id", name);
            map.put("name", name);
            return map;
        }).collect(Collectors.toList());
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminBackup_AdminBackupJsp).renderWith(data -> {
            RenderDataUtil.register(data, "backupItems", getBackupItems());
        });
    }

}
