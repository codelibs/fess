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

package jp.sf.fess.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jp.sf.fess.taglib.FessFunctions;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;

public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Field
    public String id;

    //    @Field
    //    public String segment;

    @Field
    public String digest;

    //    @Field
    //    public String boost;

    @Field
    public String host;

    @Field
    public String site;

    @Field
    public String url;

    @Field
    public String content;

    @Field
    public String title;

    @Field
    public String cache;

    @Field
    public String tstamp;

    @Field
    public List<String> anchor;

    @Field
    public List<String> type;

    @Field
    public Long contentLength;

    @Field
    public String lastModified;

    //    @Field
    //    public String date;

    //    @Field
    //    public String lang;

    @Field
    public String mimetype;

    //    public String getContentTitle() {
    //        ViewHelper viewHelper = SingletonS2Container.getComponent("viewHelper");
    //        if (viewHelper != null) {
    //            return viewHelper.getContentTitle(this);
    //        }
    //        return title;
    //    }
    //
    //    public String getContentDescription() {
    //        ViewHelper viewHelper = SingletonS2Container.getComponent("viewHelper");
    //        if (viewHelper != null) {
    //            return viewHelper.getContentDescription(this);
    //        }
    //        return "";
    //    }
    //
    //    public String getUrlLink() {
    //        ViewHelper viewHelper = SingletonS2Container.getComponent("viewHelper");
    //        if (viewHelper != null) {
    //            return viewHelper.getUrlLink(this);
    //        }
    //        return url;
    //    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(final String digest) {
        this.digest = digest;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTstamp() {
        return tstamp;
    }

    public void setTstamp(final String tstamp) {
        this.tstamp = tstamp;
    }

    public Date getTstampDate() {
        return FessFunctions.parseDate(tstamp);
    }

    public List<String> getAnchor() {
        return anchor;
    }

    public void setAnchor(final List<String> anchor) {
        this.anchor = anchor;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(final String type) {
        mimetype = type;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(final Long contentLength) {
        this.contentLength = contentLength;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(final List<String> type) {
        this.type = type;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(final String cache) {
        this.cache = cache;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(final String lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModifiedDate() {
        return FessFunctions.parseDate(lastModified);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }

}
