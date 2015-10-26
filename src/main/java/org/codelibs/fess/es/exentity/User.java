package org.codelibs.fess.es.exentity;

import java.util.Base64;
import java.util.stream.Stream;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.bsentity.BsUser;

/**
 * @author FreeGen
 */
public class User extends BsUser {

    private static final long serialVersionUID = 1L;

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public String[] getRoleNames() {
        return Stream.of(getRoles()).map(role -> new String(Base64.getDecoder().decode(role), Constants.CHARSET_UTF_8))
                .toArray(n -> new String[n]);
    }

    public String[] getGroupNames() {
        return Stream.of(getGroups()).map(group -> new String(Base64.getDecoder().decode(group), Constants.CHARSET_UTF_8))
                .toArray(n -> new String[n]);
    }
}
