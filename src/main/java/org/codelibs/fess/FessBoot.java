package org.codelibs.fess;

import java.io.File;

import org.dbflute.tomcat.TomcatBoot;

public class FessBoot extends TomcatBoot {

    private static final String LOGGING_PROPERTIES = "logging.properties";

    private static final String FESS_CONTEXT_PATH = "fess.context.path";

    private static final String FESS_PORT = "fess.port";

    private static final String FESS_TEMP_PATH = "fess.temp.path";

    private static final String FESS_WEBXML_PATH = "fess.webxml.path";

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
    protected String prepareWebXmlPath() {
        final String value = System.getProperty(FESS_WEBXML_PATH);
        if (value != null) {
            return value;
        }
        return super.prepareWebXmlPath();
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
        final String value = System.getProperty(FESS_TEMP_PATH);
        if (value != null) {
            System.setProperty(JAVA_IO_TMPDIR, value);
        }

        final String tomcatConfigPath = getTomcatConfigPath();
        TomcatBoot tomcatBoot = new FessBoot(getPort(), getContextPath()) //
                .useTldDetect(); // for JSP
        if (tomcatConfigPath != null) {
            tomcatBoot.configure(tomcatConfigPath); // e.g. URIEncoding
        }
        tomcatBoot.logging(LOGGING_PROPERTIES, op -> {
            op.ignoreNoFile();
            final String fessHomeDir = System.getProperty("fess.home");
            if (fessHomeDir != null) {
                op.replace("fess.home", fessHomeDir);
            }
        }) // uses jdk14logger
                .asDevelopment(isNoneEnv()).bootAwait();
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
            return value;
        }
        return "/fess";
    }

    protected static String getTomcatConfigPath() {
        return System.getProperty(TOMCAT_CONFIG_PATH);
    }
}
