package org.codelibs.fess.client;

import org.codelibs.fess.FessSystemException;

public class SearchException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(String message) {
        super(message);
    }

}
