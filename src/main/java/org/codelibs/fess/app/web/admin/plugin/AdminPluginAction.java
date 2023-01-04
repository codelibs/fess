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
package org.codelibs.fess.app.web.admin.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.helper.PluginHelper.ArtifactType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.exception.ValidationErrorException;

public class AdminPluginAction extends FessAdminAction {

    public static final String ROLE = "admin-plugin";

    private static final Logger logger = LogManager.getLogger(AdminPluginAction.class);

    private static final String UPLOAD = "upload";

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNamePlugin()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final DeleteForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminPlugin_AdminPluginJsp));
        verifyToken(() -> asHtml(path_AdminPlugin_AdminPluginJsp));
        final Artifact artifact = new Artifact(form.name, form.version, null);
        deleteArtifact(artifact);
        saveInfo(messages -> messages.addSuccessDeletePlugin(GLOBAL, artifact.getFileName()));
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse install(final InstallForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminPlugin_AdminPluginInstallpluginJsp));
        verifyToken(() -> asHtml(path_AdminPlugin_AdminPluginInstallpluginJsp));
        try {
            if (UPLOAD.equals(form.id)) {
                if (form.jarFile == null) {
                    throwValidationError(messages -> messages.addErrorsPluginFileIsNotFound(GLOBAL, form.id), this::asListHtml);
                }
                if (!form.jarFile.getFileName().endsWith(".jar")) {
                    throwValidationError(messages -> messages.addErrorsFileIsNotSupported(GLOBAL, form.jarFile.getFileName()),
                            this::asListHtml);
                }
                final String filename = form.jarFile.getFileName();
                final File tempFile = ComponentUtil.getSystemHelper().createTempFile("tmp-adminplugin-", ".jar");
                try (final InputStream is = form.jarFile.getInputStream(); final OutputStream os = new FileOutputStream(tempFile)) {
                    CopyUtil.copy(is, os);
                } catch (final Exception e) {
                    if (tempFile.exists() && !tempFile.delete()) {
                        logger.warn("Failed to delete {}.", tempFile.getAbsolutePath());
                    }
                    logger.debug("Failed to copy {}", filename, e);
                    throwValidationError(messages -> messages.addErrorsFailedToInstallPlugin(GLOBAL, filename), this::asListHtml);
                }
                new Thread(() -> {
                    try {
                        final PluginHelper pluginHelper = ComponentUtil.getPluginHelper();
                        final Artifact artifact =
                                pluginHelper.getArtifactFromFileName(ArtifactType.UNKNOWN, filename, tempFile.getAbsolutePath());
                        pluginHelper.installArtifact(artifact);
                    } catch (final Exception e) {
                        logger.warn("Failed to install {}", filename, e);
                    } finally {
                        if (tempFile.exists() && !tempFile.delete()) {
                            logger.warn("Failed to delete {}.", tempFile.getAbsolutePath());
                        }
                    }
                }).start();
                saveInfo(messages -> messages.addSuccessInstallPlugin(GLOBAL, form.jarFile.getFileName()));
            } else {
                final Artifact artifact = getArtifactFromInstallForm(form);
                if (artifact == null) {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asListHtml);
                }
                installArtifact(artifact);
                saveInfo(messages -> messages.addSuccessInstallPlugin(GLOBAL, artifact.getFileName()));
            }
        } catch (final ValidationErrorException e) {
            throw e;
        } catch (final Exception e) {
            throwValidationError(messages -> messages.addErrorsFailedToInstallPlugin(GLOBAL, form.id), this::asListHtml);
        }
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse installplugin() {
        saveToken();
        return asHtml(path_AdminPlugin_AdminPluginInstallpluginJsp).renderWith(data -> {
            final List<Map<String, String>> result = new ArrayList<>();
            final Map<String, String> map = new HashMap<>();
            map.put("id", UPLOAD);
            map.put("name", "");
            map.put("version", "");
            result.add(map);
            try {
                result.addAll(getAllAvailableArtifacts());
            } catch (final Exception e) {
                saveError(messages -> messages.addErrorsFailedToFindPlugins(GLOBAL));
                logger.warn("Failed to access a plugin repository.", e);
            }
            RenderDataUtil.register(data, "availableArtifactItems", result);
        }).useForm(InstallForm.class, op -> op.setup(form -> {}));
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminPlugin_AdminPluginJsp)
                .renderWith(data -> data.register("installedArtifactItems", getAllInstalledArtifacts())).useForm(DeleteForm.class);
    }

    public static List<Map<String, String>> getAllAvailableArtifacts() {
        final PluginHelper pluginHelper = ComponentUtil.getPluginHelper();
        final List<Map<String, String>> result = new ArrayList<>();
        for (final PluginHelper.ArtifactType artifactType : PluginHelper.ArtifactType.values()) {
            result.addAll(Arrays.stream(pluginHelper.getAvailableArtifacts(artifactType)).map(AdminPluginAction::beanToMap)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public static List<Map<String, String>> getAllInstalledArtifacts() {
        final PluginHelper pluginHelper = ComponentUtil.getPluginHelper();
        final List<Map<String, String>> result = new ArrayList<>();
        for (final PluginHelper.ArtifactType artifactType : PluginHelper.ArtifactType.values()) {
            result.addAll(Arrays.stream(pluginHelper.getInstalledArtifacts(artifactType)).map(AdminPluginAction::beanToMap)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public static Map<String, String> beanToMap(final Artifact artifact) {
        final Map<String, String> item = new HashMap<>();
        item.put("type", artifact.getType().getId());
        item.put("id", artifact.getName() + ":" + artifact.getVersion());
        item.put("name", artifact.getName());
        item.put("version", artifact.getVersion());
        item.put("url", artifact.getUrl());
        return item;
    }

    private Artifact getArtifactFromInstallForm(final InstallForm form) {
        final String[] values = form.id.split(":");
        return ComponentUtil.getPluginHelper().getArtifact(values[0], values[1]);
    }

    public static void installArtifact(final Artifact artifact) {
        new Thread(() -> {
            final PluginHelper pluginHelper = ComponentUtil.getPluginHelper();
            final Artifact[] artifacts = pluginHelper.getInstalledArtifacts(artifact.getType());
            try {
                pluginHelper.installArtifact(artifact);
            } catch (final Exception e) {
                logger.warn("Failed to install {}", artifact.getFileName(), e);
            }
            for (final Artifact a : artifacts) {
                if (a.getName().equals(artifact.getName()) && !a.getVersion().equals(artifact.getVersion())) {
                    try {
                        pluginHelper.deleteInstalledArtifact(a);
                    } catch (final Exception e) {
                        logger.warn("Failed to delete {}", a.getFileName(), e);
                    }
                }
            }
        }).start();
    }

    public static void deleteArtifact(final Artifact artifact) {
        new Thread(() -> {
            try {
                ComponentUtil.getPluginHelper().deleteInstalledArtifact(artifact);
            } catch (final Exception e) {
                logger.warn("Failed to delete {}", artifact.getFileName(), e);
            }
        }).start();
    }
}
