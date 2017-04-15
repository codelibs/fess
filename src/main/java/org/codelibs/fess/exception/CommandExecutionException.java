package org.codelibs.fess.exception;

public class CommandExecutionException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable e) {
        super(message, e);
    }

}
