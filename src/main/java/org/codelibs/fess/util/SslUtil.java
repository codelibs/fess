package org.codelibs.fess.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.codelibs.core.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslUtil {
	
    private static final Logger logger = LoggerFactory.getLogger(SslUtil.class);

    private final static String SSL_ENABLED = System.getProperty("fess.es.ssl.enabled");
    private final static String SSL_CREDENTIALS = System.getProperty("sg.ssl.http.user_pwd");
    public final static String SSL_KEY_PATH = System.getProperty("sg.ssl.transport.pemkeyfilepath");
    public final static String SSL_CERT_PATH = System.getProperty("sg.ssl.transport.pemcertfilepath");
    public final static String SSL_CA_PATH = System.getProperty("sg.ssl.transport.pemcafilepath");
	
	public static boolean isSslSecure() {
        return !StringUtil.isEmpty(SSL_ENABLED) && SSL_ENABLED.equals("true");
	}
	
	public static String getBasicAuthEncodedCredentials() {
        try {
			return "Basic " + Base64.getEncoder().encodeToString(SSL_CREDENTIALS.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
            logger.error("Failed to encode https user/pwd.");
            return "";
		}
	}
	
}
