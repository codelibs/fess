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
package org.codelibs.fess;

// DO NOT DEPEND OTHER JARs

import java.io.File;

import org.apache.catalina.Host;
import org.apache.catalina.core.StandardHost;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.tomcat.valve.SuppressErrorReportValve;
import org.dbflute.tomcat.TomcatBoot;

public class FessBoot extends TomcatBoot {

    private static final String LOGGING_PROPERTIES = "logging.properties";

    private static final String FESS_CONTEXT_PATH = "fess.context.path";

    private static final String FESS_PORT = "fess.port";

    private static final String FESS_TEMP_PATH = "fess.temp.path";

    private static final String FESS_WEBAPP_PATH = "fess.webapp.path";

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private static final String TOMCAT_CONFIG_PATH = "tomcat.config.path";

    public FessBoot(final int port, final String contextPath) {
        super(port, contextPath);
    }

    @Override
    protected String prepareWebappPath() {
        final String value = System.getProperty(FESS_WEBAPP_PATH);
        if (value != null) {
            return value;
        }
        return super.prepareWebappPath();
    }

    @Override
    protected String getMarkDir() {
        return new File(System.getProperty(JAVA_IO_TMPDIR), "fessboot").getAbsolutePath();
    }

    // ===================================================================================
    //                                                                        main
    //                                                                        ============

    public static void main(final String[] args) {
        // update java.io.tmpdir
        final String tempPath = System.getProperty(FESS_TEMP_PATH);
        if (tempPath != null) {
            System.setProperty(JAVA_IO_TMPDIR, tempPath);
        }

        final TomcatBoot tomcatBoot = new FessBoot(getPort(), getContextPath()) //
                .useTldDetect(); // for JSP
        if (tempPath != null) {
            tomcatBoot.atBaseDir(new File(tempPath, "webapp").getAbsolutePath());
        }
        final String tomcatConfigPath = getTomcatConfigPath();
        if (tomcatConfigPath != null) {
            tomcatBoot.configure(tomcatConfigPath); // e.g. URIEncoding
        }
        tomcatBoot.logging(LOGGING_PROPERTIES, op -> {
            op.ignoreNoFile();
            String fessLogPath = System.getProperty("fess.log.path");
            if (fessLogPath == null) {
                fessLogPath = "../../logs";
            }
            op.replace("fess.log.path", fessLogPath.replace("\\", "/"));
        }).asYouLikeIt(resource -> {
            Host host = resource.getHost();
            if (host instanceof StandardHost) {
                ((StandardHost) host).setErrorReportValveClass(SuppressErrorReportValve.class.getName());
            }
        }).asDevelopment(isNoneEnv()).bootAwait();
    }

    public static void shutdown(final String[] args) {
        System.exit(0);
    }

    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

    protected static int getPort() {
        final String value = System.getProperty(FESS_PORT);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return 8080;
    }

    protected static String getContextPath() {
        final String value = System.getProperty(FESS_CONTEXT_PATH);
        if (value != null) {
            if ("/".equals(value)) {
                return StringUtil.EMPTY;
            } else {
                return value;
            }
        }
        return StringUtil.EMPTY;
    }

    protected static String getTomcatConfigPath() {
        return System.getProperty(TOMCAT_CONFIG_PATH);
    }
}
