package jp.sf.fess.dict;

import jp.sf.fess.FessSystemException;

public class DictionaryException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    public DictionaryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DictionaryException(final String message) {
        super(message);
    }

}
