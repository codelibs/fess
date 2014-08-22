/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;

public class MailHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    public String from = Constants.DEFAULT_FROM_EMAIL;

    private final boolean debug = false;

    Properties props = new Properties();

    public void addProperty(final String key, final String value) {
        props.put(key, value);
    }

    public void send(final String[] toAddresses, final String subject,
            final String text) {
        if (toAddresses == null || toAddresses.length == 0) {
            throw new FessSystemException("TO address is empty.");
        }

        if (debug) {
            props.put("mail.debug", Constants.TRUE);
        }

        final Session session = Session.getInstance(props, null);
        session.setDebug(debug);

        try {
            // create a message
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            final InternetAddress[] address = new InternetAddress[toAddresses.length];
            for (int i = 0; i < toAddresses.length; i++) {
                address[i] = new InternetAddress(toAddresses[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(text);

            Transport.send(msg);
        } catch (final MessagingException e) {
            throw new FessSystemException("Failed to send "
                    + Arrays.toString(toAddresses), e);
        }
    }

}
