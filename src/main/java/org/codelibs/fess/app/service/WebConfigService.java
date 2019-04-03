/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.es.config.cbean.WebConfigCB;
import org.codelibs.fess.es.config.exbhv.RequestHeaderBhv;
import org.codelibs.fess.es.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class WebConfigService {

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    @Resource
    protected RequestHeaderBhv requestHeaderBhv;

    @Resource
    protected FessConfig fessConfig;

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

    public OptionalEntity<WebConfig> getWebConfig(final String id) {
        return webConfigBhv.selectByPK(id);
    }

    public void store(final WebConfig webConfig) {
        webConfigBhv.insertOrUpdate(webConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    protected void setupListCondition(final WebConfigCB cb, final WebConfigPager webConfigPager) {
        if (webConfigPager.id != null) {
            cb.query().docMeta().setId_Equal(webConfigPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
