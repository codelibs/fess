package org.codelibs.fess;

import java.io.File;

import org.dbflute.tomcat.TomcatBoot;

public class FessBoot extends TomcatBoot {

    private static final String FESS_CONTEXT_PATH = "fess.context.path";

    private static final String FESS_PORT = "fess.port";

    private static final String FESS_TEMP_PATH = "fess.temp.path";

    private static final String FESS_WEBXML_PATH = "fess.webxml.path";

    private static final String FESS_WEBAPP_PATH = "fess.webapp.path";

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    public FessBoot(int port, String contextPath) {
        super(port, contextPath);
    }

    @Override
    protected String prepareWebappPath() {
        String value = System.getProperty(FESS_WEBAPP_PATH);
        if (value != null) {
            return value;
        }
        return super.prepareWebappPath();
    }

    @Override
    protected String prepareWebXmlPath() {
        String value = System.getProperty(FESS_WEBXML_PATH);
        if (value != null) {
            return value;
        }
        return super.prepareWebXmlPath();
    }

    protected String getMarkDir() {
        String value = System.getProperty(FESS_TEMP_PATH);
        if (value != null) {
            System.setProperty(JAVA_IO_TMPDIR, value);
            return new File(value, "fessboot").getAbsolutePath();
        }
        return new File(System.getProperty(JAVA_IO_TMPDIR), "fessboot").getAbsolutePath();
    }

    // ===================================================================================
    //                                                                        main
    //                                                                        ============

    public static void main(String[] args) {
        new FessBoot(getPort(), getContextPath()).useTldDetect().asDevelopment(isNoneEnv()).bootAwait();
    }

    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

    protected static int getPort() {
        String value = System.getProperty(FESS_PORT);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return 8080;
    }

    protected static String getContextPath() {
        String value = System.getProperty(FESS_CONTEXT_PATH);
        if (value != null) {
            return value;
        }
        return "/fess";
    }
}
