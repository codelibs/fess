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
package org.codelibs.fess.tomcat.webresources;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.webresources.StandardRoot;

/**
 * Fess-specific web resource root implementation for Tomcat.
 * This class extends StandardRoot to provide custom web resource handling
 * for Fess plugins, including support for plugin JAR files in the WEB-INF/plugin directory.
 */
public class FessWebResourceRoot extends StandardRoot {
    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(FessWebResourceRoot.class.getName());

    /**
     * Constructs a new FessWebResourceRoot with the specified context.
     *
     * @param context the Tomcat context for this web resource root
     */
    public FessWebResourceRoot(final Context context) {
        super(context);
    }

    /**
     * Processes WEB-INF/lib directory and additionally scans WEB-INF/plugin for Fess plugins.
     * This method extends the standard processing to include plugin JAR files that contain
     * the "Fess-WebAppJar" manifest attribute.
     *
     * @throws LifecycleException if an error occurs during processing
     */
    @Override
    protected void processWebInfLib() throws LifecycleException {
        super.processWebInfLib();

        final WebResource[] possibleJars = listResources("/WEB-INF/plugin", false);

        for (final WebResource possibleJar : possibleJars) {
            if (possibleJar.isFile() && possibleJar.getName().endsWith(".jar")) {
                try (final JarFile jarFile = new JarFile(possibleJar.getCanonicalPath())) {
                    final Manifest manifest = jarFile.getManifest();
                    if (manifest != null && manifest.getEntries() != null) {
                        final Attributes attributes = manifest.getMainAttributes();
                        if (attributes != null
                                && (attributes.get("Fess-WebAppJar") != null || attributes.getValue("Fess-WebAppJar") != null)) {
                            createWebResourceSet(ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", possibleJar.getURL(), "/");
                        }
                    }
                } catch (final Exception e) {
                    logger.log(Level.WARNING, e, () -> {
                        final String canonicalPath = possibleJar.getCanonicalPath();
                        return "Failed to read " + canonicalPath;
                    });
                }
            }
        }
    }
}
