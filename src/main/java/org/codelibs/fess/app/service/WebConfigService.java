/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.service;

import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.WebConfigCB;
import org.codelibs.fess.opensearch.config.exbhv.RequestHeaderBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing web crawling configurations.
 * Provides CRUD operations for web configuration settings including
 * listing, retrieving, storing, and deleting web crawling configurations.
 */
public class WebConfigService extends FessAppService {

    /**
     * Default constructor.
     */
    public WebConfigService() {
        // Default constructor
    }

    /**
     * Behavior class for web configuration operations.
     */
    @Resource
    protected WebConfigBhv webConfigBhv;

    /**
     * Behavior class for web authentication operations.
     */
    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    /**
     * Behavior class for request header operations.
     */
    @Resource
    protected RequestHeaderBhv requestHeaderBhv;

    /**
     * Fess configuration settings.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Gets a paginated list of web configurations based on the provided pager.
     *
     * @param webConfigPager The pager containing pagination and search criteria
     * @return List of web configuration objects
     */
    public List<WebConfig> getWebConfigList(final WebConfigPager webConfigPager) {

        final PagingResultBean<WebConfig> webConfigList = webConfigBhv.selectPage(cb -> {
            cb.paging(webConfigPager.getPageSize(), webConfigPager.getCurrentPageNumber());
            setupListCondition(cb, webConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(webConfigList, webConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        webConfigPager.setPageNumberList(webConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return webConfigList;
    }

    /**
     * Deletes a web configuration and all its related data.
     * This includes removing associated web authentications and request headers.
     *
     * @param webConfig The web configuration to delete
     */
    public void delete(final WebConfig webConfig) {

        final String webConfigId = webConfig.getId();

        webConfigBhv.delete(webConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        webAuthenticationBhv.queryDelete(cb -> {
            cb.query().setWebConfigId_Equal(webConfigId);
        });

        requestHeaderBhv.queryDelete(cb -> {
            cb.query().setWebConfigId_Equal(webConfigId);
        });
    }

    /**
     * Gets a web configuration by its ID.
     *
     * @param id The ID of the web configuration
     * @return Optional containing the web configuration if found
     */
    public OptionalEntity<WebConfig> getWebConfig(final String id) {
        return webConfigBhv.selectByPK(id);
    }

    /**
     * Gets a web configuration by its name.
     * If multiple configurations have the same name, returns the first one ordered by sort order.
     *
     * @param name The name of the web configuration
     * @return Optional containing the web configuration if found
     */
    public OptionalEntity<WebConfig> getWebConfigByName(final String name) {
        final ListResultBean<WebConfig> list = webConfigBhv.selectList(cb -> {
            cb.query().setName_Equal(name);
            cb.query().addOrderBy_SortOrder_Asc();
        });
        if (list.isEmpty()) {
            return OptionalEntity.empty();
        }
        return OptionalEntity.of(list.get(0));
    }

    /**
     * Stores a web configuration.
     * Configuration parameters are encrypted before storage.
     *
     * @param webConfig The web configuration to store
     */
    public void store(final WebConfig webConfig) {
        webConfig.setConfigParameter(ParameterUtil.encrypt(webConfig.getConfigParameter()));
        webConfigBhv.insertOrUpdate(webConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    /**
     * Sets up the list condition for querying web configurations.
     * Applies search filters based on pager criteria such as name, URLs, and description.
     *
     * @param cb The condition bean for the query
     * @param webConfigPager The pager containing search criteria
     */
    protected void setupListCondition(final WebConfigCB cb, final WebConfigPager webConfigPager) {
        if (StringUtil.isNotBlank(webConfigPager.name)) {
            cb.query().setName_Wildcard(wrapQuery(webConfigPager.name));
        }
        if (StringUtil.isNotBlank(webConfigPager.urls)) {
            cb.query().setUrls_Wildcard(wrapQuery(webConfigPager.urls));
        }
        if (StringUtil.isNotBlank(webConfigPager.description)) {
            if (webConfigPager.description.startsWith("*")) {
                cb.query().setDescription_Wildcard(webConfigPager.description);
            } else if (webConfigPager.description.endsWith("*")) {
                cb.query().setDescription_Prefix(webConfigPager.description.replaceAll("\\*$", StringUtil.EMPTY));
            } else {
                cb.query().setDescription_MatchPhrase(webConfigPager.description);
            }
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
