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

package jp.sf.fess.form;

import java.io.Serializable;

import jp.sf.fess.entity.FacetInfo;
import jp.sf.fess.entity.GeoInfo;
import jp.sf.fess.entity.MoreLikeThisInfo;
import jp.sf.fess.util.SearchParamMap;

import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public class IndexForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Maxbytelength(maxbytelength = 1000)
    public String query;

    public String additional[];

    @Maxbytelength(maxbytelength = 1000)
    public String sort;

    @IntegerType
    public String start;

    @IntegerType
    public String pn;

    @IntegerType
    public String num;

    public String[] lang;

    @Maxbytelength(maxbytelength = 1000)
    public String queryId;

    public SearchParamMap fields = new SearchParamMap();

    // response redirect

    @Required(target = "go")
    @Maxbytelength(maxbytelength = 4000)
    public String rt;

    @Required(target = "go,cache")
    @Maxbytelength(maxbytelength = 100)
    public String docId;

    public String[] hq;

    @Maxbytelength(maxbytelength = 1000)
    public String hash;

    // xml/json

    @Maxbytelength(maxbytelength = 20)
    public String type;

    @Maxbytelength(maxbytelength = 255)
    public String callback;

    public String[] fn;

    // hotsearchword

    @Maxbytelength(maxbytelength = 100)
    public String range;

    // geo

    public GeoInfo geo;

    // facet

    public FacetInfo facet;

    // mlt

    public MoreLikeThisInfo mlt;

    // advance

    public SearchParamMap options = new SearchParamMap();

}
