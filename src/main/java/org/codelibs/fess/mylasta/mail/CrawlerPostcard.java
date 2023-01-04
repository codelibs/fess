/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
public class CrawlerPostcard extends LaTypicalPostcard {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PATH = "crawler.dfmail";

    // ===================================================================================
    //                                                                         Entry Point
    //                                                                         ===========
    public static CrawlerPostcard droppedInto(Postbox postbox, MPCall<CrawlerPostcard> postcardLambda) {
        CrawlerPostcard postcard = new CrawlerPostcard();
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
        return new String[] { "hostname", "jobname", "webFsCrawlStartTime", "webFsCrawlEndTime", "webFsCrawlExecTime", "webFsIndexExecTime",
                "webFsIndexSize", "dataCrawlStartTime", "dataCrawlEndTime", "dataCrawlExecTime", "dataIndexExecTime", "dataIndexSize",
                "crawlerStartTime", "crawlerEndTime", "crawlerExecTime", "status" };
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
     * Set the value of jobname, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param jobname The parameter value of jobname. (NotNull)
     */
    public void setJobname(String jobname) {
        registerVariable("jobname", jobname);
    }

    /**
     * Set the value of webFsCrawlStartTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param webFsCrawlStartTime The parameter value of webFsCrawlStartTime. (NotNull)
     */
    public void setWebFsCrawlStartTime(String webFsCrawlStartTime) {
        registerVariable("webFsCrawlStartTime", webFsCrawlStartTime);
    }

    /**
     * Set the value of webFsCrawlEndTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param webFsCrawlEndTime The parameter value of webFsCrawlEndTime. (NotNull)
     */
    public void setWebFsCrawlEndTime(String webFsCrawlEndTime) {
        registerVariable("webFsCrawlEndTime", webFsCrawlEndTime);
    }

    /**
     * Set the value of webFsCrawlExecTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param webFsCrawlExecTime The parameter value of webFsCrawlExecTime. (NotNull)
     */
    public void setWebFsCrawlExecTime(String webFsCrawlExecTime) {
        registerVariable("webFsCrawlExecTime", webFsCrawlExecTime);
    }

    /**
     * Set the value of webFsIndexExecTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param webFsIndexExecTime The parameter value of webFsIndexExecTime. (NotNull)
     */
    public void setWebFsIndexExecTime(String webFsIndexExecTime) {
        registerVariable("webFsIndexExecTime", webFsIndexExecTime);
    }

    /**
     * Set the value of webFsIndexSize, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param webFsIndexSize The parameter value of webFsIndexSize. (NotNull)
     */
    public void setWebFsIndexSize(String webFsIndexSize) {
        registerVariable("webFsIndexSize", webFsIndexSize);
    }

    /**
     * Set the value of dataCrawlStartTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param dataCrawlStartTime The parameter value of dataCrawlStartTime. (NotNull)
     */
    public void setDataCrawlStartTime(String dataCrawlStartTime) {
        registerVariable("dataCrawlStartTime", dataCrawlStartTime);
    }

    /**
     * Set the value of dataCrawlEndTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param dataCrawlEndTime The parameter value of dataCrawlEndTime. (NotNull)
     */
    public void setDataCrawlEndTime(String dataCrawlEndTime) {
        registerVariable("dataCrawlEndTime", dataCrawlEndTime);
    }

    /**
     * Set the value of dataCrawlExecTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param dataCrawlExecTime The parameter value of dataCrawlExecTime. (NotNull)
     */
    public void setDataCrawlExecTime(String dataCrawlExecTime) {
        registerVariable("dataCrawlExecTime", dataCrawlExecTime);
    }

    /**
     * Set the value of dataIndexExecTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param dataIndexExecTime The parameter value of dataIndexExecTime. (NotNull)
     */
    public void setDataIndexExecTime(String dataIndexExecTime) {
        registerVariable("dataIndexExecTime", dataIndexExecTime);
    }

    /**
     * Set the value of dataIndexSize, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param dataIndexSize The parameter value of dataIndexSize. (NotNull)
     */
    public void setDataIndexSize(String dataIndexSize) {
        registerVariable("dataIndexSize", dataIndexSize);
    }

    /**
     * Set the value of crawlerStartTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param crawlerStartTime The parameter value of crawlerStartTime. (NotNull)
     */
    public void setCrawlerStartTime(String crawlerStartTime) {
        registerVariable("crawlerStartTime", crawlerStartTime);
    }

    /**
     * Set the value of crawlerEndTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param crawlerEndTime The parameter value of crawlerEndTime. (NotNull)
     */
    public void setCrawlerEndTime(String crawlerEndTime) {
        registerVariable("crawlerEndTime", crawlerEndTime);
    }

    /**
     * Set the value of crawlerExecTime, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param crawlerExecTime The parameter value of crawlerExecTime. (NotNull)
     */
    public void setCrawlerExecTime(String crawlerExecTime) {
        registerVariable("crawlerExecTime", crawlerExecTime);
    }

    /**
     * Set the value of status, used in parameter comment. <br>
     * Even if empty string, treated as empty plainly. So "IF pmb != null" is false if empty.
     * @param status The parameter value of status. (NotNull)
     */
    public void setStatus(String status) {
        registerVariable("status", status);
    }
}
