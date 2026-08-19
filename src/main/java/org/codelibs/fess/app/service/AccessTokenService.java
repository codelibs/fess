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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.AccessTokenPager;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.AccessTokenCB;
import org.codelibs.fess.opensearch.config.exbhv.AccessTokenBhv;
import org.codelibs.fess.opensearch.config.exentity.AccessToken;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The service for access token.
 */
public class AccessTokenService {

    /**
     * Default constructor.
     */
    public AccessTokenService() {
        // nothing
    }

    /**
     * The behavior of access token.
     */
    @Resource
    protected AccessTokenBhv accessTokenBhv;

    /**
     * The Fess configuration.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Get the list of access tokens.
     * @param accessTokenPager The pager for access token.
     * @return The list of access tokens.
     */
    public List<AccessToken> getAccessTokenList(final AccessTokenPager accessTokenPager) {

        final PagingResultBean<AccessToken> accessTokenList = accessTokenBhv.selectPage(cb -> {
            cb.paging(accessTokenPager.getPageSize(), accessTokenPager.getCurrentPageNumber());
            setupListCondition(cb, accessTokenPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(accessTokenList, accessTokenPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        accessTokenPager.setPageNumberList(
                accessTokenList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return accessTokenList;
    }

    /**
     * Get the access token.
     * @param id The ID of the access token.
     * @return The access token.
     */
    public OptionalEntity<AccessToken> getAccessToken(final String id) {
        return accessTokenBhv.selectByPK(id);
    }

    /**
     * Store the access token.
     * @param accessToken The access token.
     */
    public void store(final AccessToken accessToken) {

        accessTokenBhv.insertOrUpdate(accessToken, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    /**
     * Delete the access token.
     * @param accessToken The access token.
     */
    public void delete(final AccessToken accessToken) {

        accessTokenBhv.delete(accessToken, op -> op.setRefreshPolicy(Constants.TRUE));

    }

    /**
     * Set up the list condition.
     * @param cb The callback.
     * @param accessTokenPager The pager for access token.
     */
    protected void setupListCondition(final AccessTokenCB cb, final AccessTokenPager accessTokenPager) {
        if (accessTokenPager.id != null) {
            cb.query().docMeta().setId_Equal(accessTokenPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

    /**
     * Get the permissions a request carries: the ones the registered token was issued with, plus
     * the ones the caller named in the token's own request parameter.
     *
     * <p>These are the permissions a search is filtered by, which is what the request parameter
     * exists for -- an application that embeds Fess passes the end user's permissions per request
     * rather than issuing a token each. Anything that decides what the CALLER may do, rather than
     * which documents it may see, has to read {@link #getTokenPermissions(HttpServletRequest)}
     * instead.
     *
     * @param request The request.
     * @return The permissions.
     */
    public OptionalEntity<Set<String>> getPermissions(final HttpServletRequest request) {
        return resolvePermissions(request, true);
    }

    /**
     * Get only the permissions the registered token itself was issued with.
     *
     * <p>The request parameter is deliberately not read. Its values come from the caller, so a set
     * that included them could be raised by the caller to any permission at all -- which is exactly
     * what it is for while the set decides which documents come back, and exactly what it must not
     * do while the set decides whether the administration API answers.
     *
     * @param request The request.
     * @return The permissions the token carries.
     */
    public OptionalEntity<Set<String>> getTokenPermissions(final HttpServletRequest request) {
        return resolvePermissions(request, false);
    }

    /**
     * Resolves the registered token and collects its permissions.
     *
     * @param request The request.
     * @param withRequestParameter Whether to add the values of the token's request parameter.
     * @return The permissions, empty when the request carries no token.
     */
    protected OptionalEntity<Set<String>> resolvePermissions(final HttpServletRequest request, final boolean withRequestParameter) {
        final String token = ComponentUtil.getAccessTokenHelper().getAccessTokenFromRequest(request);
        if (StringUtil.isNotBlank(token)) {
            return accessTokenBhv.selectEntity(cb -> {
                cb.query().setToken_Term(token);
            })
                    .map(accessToken -> OptionalEntity.of(collectPermissions(accessToken, request, withRequestParameter)))
                    .orElseThrow(() -> new InvalidAccessTokenException("invalid_token", "The access token is not registered."));
        }
        return OptionalEntity.empty();
    }

    /**
     * Collects the permissions of a resolved token.
     *
     * @param accessToken The registered token.
     * @param request The request, read only for the token's own parameter.
     * @param withRequestParameter Whether to add the values of that parameter.
     * @return The permissions.
     */
    protected Set<String> collectPermissions(final AccessToken accessToken, final HttpServletRequest request,
            final boolean withRequestParameter) {
        final Set<String> permissionSet = new HashSet<>();
        final Long expiredTime = accessToken.getExpiredTime();
        if (expiredTime != null && expiredTime.longValue() > 0
                && expiredTime.longValue() < ComponentUtil.getSystemHelper().getCurrentTimeAsLong()) {
            throw new InvalidAccessTokenException("invalid_token",
                    "The token is expired(" + FessFunctions.formatDate(FessFunctions.date(expiredTime)) + ").");
        }
        stream(accessToken.getPermissions()).of(stream -> stream.forEach(permissionSet::add));
        if (withRequestParameter) {
            final String name = accessToken.getParameterName();
            stream(request.getParameterValues(name)).of(stream -> stream.filter(StringUtil::isNotBlank).forEach(permissionSet::add));
        }
        return permissionSet;
    }
}