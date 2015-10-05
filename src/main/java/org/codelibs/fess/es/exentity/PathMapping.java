package org.codelibs.fess.es.exentity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.fess.es.bsentity.BsPathMapping;

/**
 * @author FreeGen
 */
public class PathMapping extends BsPathMapping {

    private static final long serialVersionUID = 1L;

    private Pattern regexPattern;

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public Matcher getMatcher(final CharSequence input) {
        if (regexPattern == null) {
            regexPattern = Pattern.compile(getRegex());
        }
        return regexPattern.matcher(input);
    }
}
