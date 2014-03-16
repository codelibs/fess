package jp.sf.fess.entity;

import org.apache.solr.client.solrj.response.QueryResponse;

public class SpellCheckResponse extends SuggestResponse {
    public SpellCheckResponse(final QueryResponse queryResponse, final int num,
            final String query) {
        super(queryResponse, num, query);
    }
}
