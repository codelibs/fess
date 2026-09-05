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
package org.codelibs.fess.opensearch.client;

import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;

/**
 * Verifies that a search is never sent with the total turned off.
 *
 * <p>{@code track_total_hits=false} makes OpenSearch leave {@code hits.total} out of the response,
 * and {@code SearchHits#getTotalHits()} then returns null. Every record count Fess shows - the
 * pager, the result screen, {@code record_count} in the API - is derived from that total, and the
 * response handler read it unconditionally, so such a search came back as zero documents with a
 * NullPointerException in fess.log. The value is refused where it enters instead.</p>
 */
public class SearchEngineClientTrackTotalHitsTest extends UnitFessTestCase {

    @Test
    public void test_requestedFalse_isRefused() {
        try {
            build("false", ComponentUtil.getFessConfig());
            fail("InvalidQueryException should be thrown for track_total_hits=false.");
        } catch (final InvalidQueryException e) {
            assertNotNull(e.getMessageCode());
            assertTrue(e.getMessage().contains("false"));
        }
    }

    // The parameter is compared without regard to case wherever else it is read, so the refusal
    // cannot be sidestepped by spelling it differently.
    @Test
    public void test_requestedFalseInAnyCase_isRefused() {
        try {
            build("False", ComponentUtil.getFessConfig());
            fail("InvalidQueryException should be thrown for track_total_hits=False.");
        } catch (final InvalidQueryException e) {
            assertNotNull(e.getMessageCode());
        }
    }

    @Test
    public void test_requestedTrue_countsEveryMatch() {
        assertEquals(Integer.MAX_VALUE, trackTotalHitsUpTo(build("true", ComponentUtil.getFessConfig())));
    }

    @Test
    public void test_requestedNumber_countsUpToIt() {
        assertEquals(100, trackTotalHitsUpTo(build("100", ComponentUtil.getFessConfig())));
    }

    @Test
    public void test_notRequested_countsUpToTheConfiguredValue() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        assertEquals(fessConfig.getQueryTrackTotalHitsValue(), trackTotalHitsUpTo(build(null, fessConfig)));
    }

    // An administrator can put the same unsupported value in the configuration file, and it is
    // refused there too rather than quietly producing searches that answer nothing.
    @Test
    public void test_configuredFalse_isRefused() {
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getQueryTrackTotalHits() {
                return "false";
            }

            @Override
            public Object getQueryTrackTotalHitsValue() {
                return Boolean.FALSE;
            }
        };

        try {
            build(null, fessConfig);
            fail("InvalidQueryException should be thrown for query.track.total.hits=false.");
        } catch (final InvalidQueryException e) {
            assertNotNull(e.getMessageCode());
            assertTrue(e.getMessage().contains("false"));
        }
    }

    private Integer trackTotalHitsUpTo(final SearchRequestBuilder searchRequestBuilder) {
        return searchRequestBuilder.request().source().trackTotalHitsUpTo();
    }

    private SearchRequestBuilder build(final String trackTotalHits, final FessConfig fessConfig) {
        final SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
        SearchConditionBuilder.builder(searchRequestBuilder).trackTotalHits(trackTotalHits).buildTrackTotalHits(fessConfig);
        return searchRequestBuilder;
    }
}
