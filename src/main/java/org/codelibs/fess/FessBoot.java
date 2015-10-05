package org.codelibs.fess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.dbflute.tomcat.TomcatBoot;

public class FessBoot extends TomcatBoot {

    private static final Logger logger = Logger.getLogger(FessBoot.class.getPackage().getName());

    private static final String LOGGING_PROPERTIES = "/logging.properties";

    private static final String FESS_CONTEXT_PATH = "fess.context.path";

    private static final String FESS_PORT = "fess.port";

    private static final String FESS_TEMP_PATH = "fess.temp.path";

    private static final String FESS_WEBXML_PATH = "fess.webxml.path";

    private static final String FESS_WEBAPP_PATH = "fess.webapp.path";

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private static final int DEFAULT_BUF_SIZE = 4096;

    private static final String UTF_8 = "UTF-8";

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

    @Override
    protected void info(final String msg) {
        logger.info(msg);
    }

    // ===================================================================================
    //                                                                        main
    //                                                                        ============

    public static void main(final String[] args) {
        // load logging.properties
        try (InputStream is = FessBoot.class.getResourceAsStream(LOGGING_PROPERTIES)) {
            if (is != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    final ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUF_SIZE);
                    final byte[] buf = buffer.array();
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    String configContent = out.toString(UTF_8);
                    final String fessHomeDir = System.getProperty("fess.home");
                    if (fessHomeDir != null) {
                        configContent = configContent.replaceAll(Pattern.quote("${fess.home}"), fessHomeDir);
                    }

                    LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(configContent.getBytes(UTF_8)));
                    logger.info(LOGGING_PROPERTIES + " is loaded.");
                }
            }
        } catch (final IOException e) {
            logger.log(Level.WARNING, "Failed to load " + LOGGING_PROPERTIES, e);
        }

        // update java.io.tmpdir
        final String value = System.getProperty(FESS_TEMP_PATH);
        if (value != null) {
            System.setProperty(JAVA_IO_TMPDIR, value);
        }

        new FessBoot(getPort(), getContextPath()).useTldDetect().asDevelopment(isNoneEnv()).bootAwait();
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
}
