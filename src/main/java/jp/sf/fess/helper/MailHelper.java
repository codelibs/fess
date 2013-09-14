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

package jp.sf.fess.helper;

import java.io.Serializable;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;

import org.mobylet.mail.MobyletMailer;
import org.mobylet.mail.message.MessageBody;
import org.mobylet.mail.message.MobyletMessage;

public class MailHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    public String from = Constants.DEFAULT_FROM_EMAIL;

    public void send(final String[] toAddresses, final String subject,
            final String text) {
        if (toAddresses == null || toAddresses.length == 0) {
            throw new FessSystemException("TO address is empty.");
        }
        final MobyletMessage message = MobyletMailer
                .createMessage(toAddresses[0].trim());
        if (toAddresses.length > 1) {
            for (int i = 1; i < toAddresses.length; i++) {
                message.to(toAddresses[i].trim());
            }
        }
        final MessageBody body = new MessageBody();
        body.setText(text);
        message.from(from).subject(subject).setBody(body);
        MobyletMailer.send(message);
    }

}
