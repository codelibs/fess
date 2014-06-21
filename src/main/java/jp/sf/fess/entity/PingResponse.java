package jp.sf.fess.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.response.SolrPingResponse;

public class PingResponse {
    private int status = 0;

    private Target[] targets;

    public PingResponse(Collection<SolrPingResponse> responses) {
        List<Target> targetList = new ArrayList<>();
        for (SolrPingResponse response : responses) {
            int status = response.getStatus();
            if (status != 0) {
                status = 1;
            }
            targetList.add(new Target(status, response.getRequestUrl(),
                    response.getElapsedTime(), response.getQTime()));
        }
        targets = targetList.toArray(new Target[targetList.size()]);
    }

    public static class Target {

        private int status;

        private String url;

        private long searchTime;

        private int queryTime;

        public Target(int status, String url, long elapsedTime, int qTime) {
            this.status = status;
            this.url = url;
            this.searchTime = elapsedTime;
            this.queryTime = qTime;
        }

        public int getStatus() {
            return status;
        }

        public String getUrl() {
            return url;
        }

        public long getSearchTime() {
            return searchTime;
        }

        public int getQueryTime() {
            return queryTime;
        }

    }

    public int getStatus() {
        return status;
    }

    public Target[] getTargets() {
        return targets;
    }

}
