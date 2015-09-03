package org.codelibs.fess.client;

import org.codelibs.fess.FessSystemException;

public class FessEsClientException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    public FessEsClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FessEsClientException(final String message) {
        super(message);
    }

}
