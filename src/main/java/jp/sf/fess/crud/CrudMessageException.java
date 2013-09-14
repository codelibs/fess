/*
 * Copyright 2009-2013 the Fess Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sf.fess.crud;

/**
 * @author shinsuke
 */
public class CrudMessageException extends RuntimeException {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 4564000116499132363L;

    private final String messageId;

    private final Object[] args;

    /**
     * @return Returns the messageId.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return Returns the args.
     */
    public Object[] getArgs() {
        return args;
    }

    public CrudMessageException(final String messageId) {
        super(messageId);
        this.messageId = messageId;
        args = null;
    }

    public CrudMessageException(final String messageId, final Object[] args) {
        super(messageId);
        this.messageId = messageId;
        this.args = args;
    }

    public CrudMessageException(final String messageId, final String message,
            final Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
        args = null;
    }

    public CrudMessageException(final String messageId, final Object[] args,
            final String message, final Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
        this.args = args;
    }

    public CrudMessageException(final String messageId, final String message) {
        super(message);
        this.messageId = messageId;
        args = null;
    }

    public CrudMessageException(final String messageId, final Object[] args,
            final String message) {
        super(message);
        this.messageId = messageId;
        this.args = args;
    }

    public CrudMessageException(final String messageId, final Throwable cause) {
        super(cause);
        this.messageId = messageId;
        args = null;
    }

    public CrudMessageException(final String messageId, final Object[] args,
            final Throwable cause) {
        super(cause);
        this.messageId = messageId;
        this.args = args;
    }

}
