/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.suggest.request.popularwords.PopularWordsRequestBuilder;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopularWordHelper {
    private static final Logger logger = LoggerFactory.getLogger(PopularWordHelper.class);

    public List<String> getWordList(String seed, String[] tags, String[] fields, String[] excludes) {
        RoleQueryHelper roleQueryHelper = ComponentUtil.getRoleQueryHelper();
        String[] roles = roleQueryHelper.build().stream().toArray(n -> new String[n]);
        return getWordList(seed, tags, roles, fields, excludes);
    }

    public List<String> getWordList(String seed, String[] tags, String[] roles, String[] fields, String[] excludes) {
        // TODO cache
        final List<String> wordList = new ArrayList<String>();
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
        final PopularWordsRequestBuilder popularWordsRequestBuilder =
                suggestHelper.suggester().popularWords().setSize(fessConfig.getSuggestPopularWordSizeAsInteger().intValue())
                        .setWindowSize(fessConfig.getSuggestPopularWordWindowSizeAsInteger().intValue());
        if (seed != null) {
            popularWordsRequestBuilder.setSeed(seed);
        }
        StreamUtil.of(tags).forEach(tag -> popularWordsRequestBuilder.addTag(tag));
        StreamUtil.of(roles).forEach(role -> popularWordsRequestBuilder.addRole(role));
        StreamUtil.of(fields).forEach(field -> popularWordsRequestBuilder.addField(field));
        StreamUtil.of(excludes).forEach(exclude -> popularWordsRequestBuilder.addExcludeWord(exclude));
        popularWordsRequestBuilder.execute().then(r -> {
            r.getItems().stream().forEach(item -> wordList.add(item.getText()));
        }).error(t -> logger.warn("Failed to generate popular words.", t));

        return wordList;
    }

}
