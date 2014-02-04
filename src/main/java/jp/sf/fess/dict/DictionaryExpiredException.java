package jp.sf.fess.dict;

import org.seasar.struts.exception.ActionMessagesException;

public class DictionaryExpiredException extends ActionMessagesException {

    private static final long serialVersionUID = 1L;

    public DictionaryExpiredException() {
        super("errors.expired_dict_id");
    }
}
