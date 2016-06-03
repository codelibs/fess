/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.mylasta.mail;

import org.lastaflute.core.mail.LaTypicalPostcard;
import org.lastaflute.core.mail.MPCall;
import org.lastaflute.core.mail.Postbox;

/**
 * The postcard for MailFlute on LastaFlute.
 * @author FreeGen
 */
public class EsStatusPostcard extends LaTypicalPostcard {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PATH = "es_status.dfmail";

    // ===================================================================================
    //                                                                         Entry Point
    //                                                                         ===========
    public static EsStatusPostcard droppedInto(Postbox postbox, MPCall<EsStatusPostcard> postcardLambda) {
        EsStatusPostcard postcard = new EsStatusPostcard();
        postcardLambda.write(postcard);
        postbox.post(postcard);
        return postcard;
    }

    // ===================================================================================
    //                                                                           Meta Data
    //                                                                           =========
    @Override
    protected String getBodyFile() {
        return PATH;
    }

    @Override
    protected String[] getPropertyNames() {
        return new String[] { "hostname", "server", "statusBefore", "statusAfter", "indexBefore", "indexAfter" };
    }

    // ===================================================================================
    //                                                                    Postcard Request
    //                                                                    ================
    // -----------------------------------------------------
    //                                          Mail Address
    //                                          ------------
    public void setFrom(String from, String personal) {
        doSetFrom(from, personal);
    }

    public void addTo(String to) {
        doAddTo(to);
    }

    public void addTo(String to, String personal) {
        doAddTo(to, personal);
    }

    public void addCc(String cc) {
        doAddCc(cc);
    }

    public void addCc(String cc, String personal) {
        doAddCc(cc, personal);
    }

    public void addBcc(String bcc) {
        doAddBcc(bcc);
    }

    public void addBcc(String bcc, String personal) {
        doAddBcc(bcc, personal);
    }

    public void addReplyTo(String replyTo) {
        doAddReplyTo(replyTo);
    }

    public void addReplyTo(String replyTo, String personal) {
        doAddReplyTo(replyTo, personal);
    }

    // -----------------------------------------------------
    //                                  Application Variable
    //                                  --------------------
    /**
     * Set the value of hostname, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param hostname The parameter value of hostname. (NotNull)
     */
    public void setHostname(String hostname) {
        registerVariable("hostname", hostname);
    }

    /**
     * Set the value of server, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param server The parameter value of server. (NotNull)
     */
    public void setServer(String server) {
        registerVariable("server", server);
    }

    /**
     * Set the value of statusBefore, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param statusBefore The parameter value of statusBefore. (NotNull)
     */
    public void setStatusBefore(String statusBefore) {
        registerVariable("statusBefore", statusBefore);
    }

    /**
     * Set the value of statusAfter, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param statusAfter The parameter value of statusAfter. (NotNull)
     */
    public void setStatusAfter(String statusAfter) {
        registerVariable("statusAfter", statusAfter);
    }

    /**
     * Set the value of indexBefore, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param indexBefore The parameter value of indexBefore. (NotNull)
     */
    public void setIndexBefore(String indexBefore) {
        registerVariable("indexBefore", indexBefore);
    }

    /**
     * Set the value of indexAfter, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param indexAfter The parameter value of indexAfter. (NotNull)
     */
    public void setIndexAfter(String indexAfter) {
        registerVariable("indexAfter", indexAfter);
    }
}
