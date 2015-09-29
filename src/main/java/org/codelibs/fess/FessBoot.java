package org.codelibs.fess;

import org.dbflute.tomcat.TomcatBoot;

public class FessBoot {

    public static void main(String[] args) {
        // TODO args
        new TomcatBoot(8080, "/fess").useTldDetect().asDevelopment(isNoneEnv()).bootAwait();
    }

    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

}
