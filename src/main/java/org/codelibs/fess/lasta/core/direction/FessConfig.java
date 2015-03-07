package org.codelibs.fess.lasta.core.direction;

import org.codelibs.fess.lasta.core.direction.FessEnv;

/**
 * @author FreeGen
 */
public interface FessConfig extends FessEnv {

    /** The key of the configuration. e.g. Fess */
    String DOMAIN_TITLE = "domain.title";

    /** The key of the configuration. e.g. / */
    String COOKIE_DEFAULT_PATH = "cookie.default.path";

    /** The key of the configuration. e.g. 31556926 */
    String COOKIE_DEFAULT_EXPIRE = "cookie.default.expire";

    /** The key of the configuration. e.g. 315360000 */
    String COOKIE_ETERNAL_EXPIRE = "cookie.eternal.expire";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'domain.title'. <br />
     * The value is, e.g. Fess <br />
     * comment: The title of domain the application for logging
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getDomainTitle();

    /**
     * Get the value for the key 'cookie.default.path'. <br />
     * The value is, e.g. / <br />
     * comment: The default path of cookie (basically '/' if no context path)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getCookieDefaultPath();

    /**
     * Get the value for the key 'cookie.default.expire'. <br />
     * The value is, e.g. 31556926 <br />
     * comment: The default expire of cookie in seconds e.g. 31556926: one year, 86400: one day
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getCookieDefaultExpire();

    /**
     * Get the value for the key 'cookie.default.expire' as {@link Integer}. <br />
     * The value is, e.g. 31556926 <br />
     * comment: The default expire of cookie in seconds e.g. 31556926: one year, 86400: one day
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCookieDefaultExpireAsInteger();

    /**
     * Get the value for the key 'cookie.eternal.expire'. <br />
     * The value is, e.g. 315360000 <br />
     * comment: The eternal expire of cookie in seconds e.g. 315360000: ten year, 86400: one day
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getCookieEternalExpire();

    /**
     * Get the value for the key 'cookie.eternal.expire' as {@link Integer}. <br />
     * The value is, e.g. 315360000 <br />
     * comment: The eternal expire of cookie in seconds e.g. 315360000: ten year, 86400: one day
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCookieEternalExpireAsInteger();

    /**
     * The simple implementation for configuration.
     * @author FreeGen
     */
    public static class SimpleImpl extends FessEnv.SimpleImpl implements FessConfig {

        /** The serial version UID for object serialization. (Default) */
        private static final long serialVersionUID = 1L;

        /** {@inheritDoc} */
        public String getDomainTitle() {
            return get(FessConfig.DOMAIN_TITLE);
        }

        /** {@inheritDoc} */
        public String getCookieDefaultPath() {
            return get(FessConfig.COOKIE_DEFAULT_PATH);
        }

        /** {@inheritDoc} */
        public String getCookieDefaultExpire() {
            return get(FessConfig.COOKIE_DEFAULT_EXPIRE);
        }

        /** {@inheritDoc} */
        public Integer getCookieDefaultExpireAsInteger() {
            return getAsInteger(FessConfig.COOKIE_DEFAULT_EXPIRE);
        }

        /** {@inheritDoc} */
        public String getCookieEternalExpire() {
            return get(FessConfig.COOKIE_ETERNAL_EXPIRE);
        }

        /** {@inheritDoc} */
        public Integer getCookieEternalExpireAsInteger() {
            return getAsInteger(FessConfig.COOKIE_ETERNAL_EXPIRE);
        }
    }
}
