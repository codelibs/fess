/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.plugin;

import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminPluginAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminPluginAction.class);

    @Resource
    private PluginHelper pluginHelper;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
//        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNamePlugin()));

    }

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    public HtmlResponse delete(final PluginBean pluginBean) {
        // TODO
        try {
            pluginHelper.deleteInstalledArtifact(new Artifact(pluginBean.name, pluginBean.version, null));
        } catch (Exception e) {

        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse install(final InstallForm form) {
        // TODO
        return redirect(getClass());
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminPlugin_AdminPluginJsp).renderWith(
                data ->
                        RenderDataUtil.register(data, "installedArtifactItems", getAllInstalledArtifacts()));
    }

    private List<PluginBean> getAllInstalledArtifacts() {
        final List<PluginBean> result = new ArrayList<>();
        for(PluginHelper.ArtifactType artifactType : PluginHelper.ArtifactType.values()) {
            result.addAll(
                    Arrays.stream(pluginHelper.getInstalledArtifacts(artifactType))
                            .map(artifact -> mappingToBean(PluginHelper.ArtifactType.getType(artifactType.getId()).toString(), artifact)).collect(Collectors.toList()));
        }
        return result;
    }

    private PluginBean mappingToBean(final String artifactType, final Artifact artifact) {
        final PluginBean pluginBean = new PluginBean();
        pluginBean.artifactType = artifactType;
        pluginBean.name = artifact.getName();
        pluginBean.version = artifact.getVersion();
        return pluginBean;
    }
}
