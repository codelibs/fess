package org.codelibs.fess;

import java.io.File;

import org.dbflute.tomcat.TomcatBoot;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class FessBoot extends TomcatBoot {

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    protected Options options;

    public FessBoot(Options options) {
        super(options.port, options.contextPath);
        this.options = options;
    }

    @Override
    protected String prepareWebappPath() {
        if (options.webappPath != null) {
            return options.webappPath;
        }
        return super.prepareWebappPath();
    }

    @Override
    protected String prepareWebXmlPath() {
        if (options.webXmlPath != null) {
            return options.webXmlPath;
        }
        return super.prepareWebXmlPath();
    }

    protected String getMarkDir() {
        if (options.tempPath != null) {
            System.setProperty(JAVA_IO_TMPDIR, options.tempPath);
            return new File(options.tempPath, "fessboot").getAbsolutePath();
        }
        return new File(System.getProperty(JAVA_IO_TMPDIR), "fessboot").getAbsolutePath();
    }

    // ===================================================================================
    //                                                                        main
    //                                                                        ============

    public static void main(String[] args) {
        final Options options = new Options();

        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + FessBoot.class.getCanonicalName() + " [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        new FessBoot(options).useTldDetect().asDevelopment(isNoneEnv()).bootAwait();
    }

    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

    protected static class Options {

        @Option(name = "--port", metaVar = "port", usage = "Listen port")
        protected int port = 8080;

        @Option(name = "--context-path", metaVar = "contextPath", usage = "Context path")
        protected String contextPath = "/fess";

        @Option(name = "--webapp-path", metaVar = "webappPath", usage = "Webapp path")
        protected String webappPath = null;

        @Option(name = "--webxml-path", metaVar = "webXmlPath", usage = "web.xml path")
        protected String webXmlPath = null;

        @Option(name = "--temp-path", metaVar = "tempPath", usage = "Temporary path")
        protected String tempPath = null;
    }
}
