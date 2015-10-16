/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.app.web.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;

public class SearchForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public Map<String, String[]> fields = new HashMap<>();

    //@Maxbytelength(maxbytelength = 1000)
    public String query;

    public String sort;

    public String num;

    public String[] lang;

    public String additional[];

    //@Maxbytelength(maxbytelength = 10)
    public String op;

    //@IntegerType
    public String start;

    //@IntegerType
    public String pn;

    //@Maxbytelength(maxbytelength = 1000)
    public String queryId;

    // response redirect

    //@Required(target = "go")
    //@Maxbytelength(maxbytelength = 4000)
    public String rt;

    //@Required(target = "go,cache")
    //@Maxbytelength(maxbytelength = 100)
    public String docId;

    public String[] hq;

    //@Maxbytelength(maxbytelength = 1000)
    public String hash;

    // xml/json

    //@Maxbytelength(maxbytelength = 20)
    public String type;

    //@Maxbytelength(maxbytelength = 255)
    public String callback;

    public String[] fn;

    // hotsearchword

    //@Maxbytelength(maxbytelength = 100)
    public String range;

    // geo

    public GeoInfo geo;

    // facet

    public FacetInfo facet;

    // advance

    public Map<String, String[]> options = new HashMap<>();

}
