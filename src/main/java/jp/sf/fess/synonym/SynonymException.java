package jp.sf.fess.synonym;

import jp.sf.fess.FessSystemException;

public class SynonymException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    public SynonymException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
