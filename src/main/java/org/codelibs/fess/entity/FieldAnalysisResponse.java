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

package org.codelibs.fess.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

public class FieldAnalysisResponse extends
        LinkedHashMap<String, Map<String, List<Map<String, Object>>>> {

    private static final long serialVersionUID = 1L;

    protected long execTime;

    public FieldAnalysisResponse(final NamedList<Object> response) {
        final NamedList<?> fnList = getNamedList(
                getNamedList(response, "analysis"), "field_names");
        if (fnList != null) {
            for (int i = 0; i < fnList.size(); i++) {
                final String fieldName = fnList.getName(i);
                final NamedList<?> analysisList = getNamedList(
                        fnList.getVal(i), "index");
                final Map<String, List<Map<String, Object>>> analysisMap = new LinkedHashMap<String, List<Map<String, Object>>>();
                put(fieldName, analysisMap);

                if (analysisList != null) {
                    for (int j = 0; j < analysisList.size(); j++) {
                        final String analysisName = analysisList.getName(j);
                        final Object analysisDataList = analysisList.getVal(j);
                        if (analysisDataList instanceof List<?>) {
                            final List<Map<String, Object>> dataMapList = getDataMap((List<Object>) analysisDataList);
                            analysisMap.put(analysisName, dataMapList);
                        }
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> getDataMap(
            final List<Object> analysisDataList) {
        final List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
        for (final Object dataList : analysisDataList) {
            if (dataList instanceof SimpleOrderedMap<?>) {
                final Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                for (int k = 0; k < ((SimpleOrderedMap<?>) dataList).size(); k++) {
                    final String dataName = ((SimpleOrderedMap<?>) dataList)
                            .getName(k);
                    final Object dataObj = ((SimpleOrderedMap<?>) dataList)
                            .getVal(k);
                    dataMap.put(dataName, dataObj);
                }
                dataMapList.add(dataMap);
            }
        }
        return dataMapList;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(final long execTime) {
        this.execTime = execTime;
    }

    private NamedList<?> getNamedList(final Object obj, final String key) {
        if (obj instanceof NamedList<?>) {
            return (NamedList<?>) ((NamedList<?>) obj).get(key);
        }
        return null;
    }

    @Override
    public String toString() {
        return "FieldAnalysisResponse [execTime=" + execTime + "]";
    }

}
