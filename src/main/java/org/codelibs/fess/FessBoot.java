package org.codelibs.fess;

import org.dbflute.jetty.JettyBoot;

public class FessBoot {

    public static void main(String[] args) {
        // TODO args
        new JettyBoot(8080, "/fess").asDevelopment(isNoneEnv()).bootAwait();
    }

    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

}
