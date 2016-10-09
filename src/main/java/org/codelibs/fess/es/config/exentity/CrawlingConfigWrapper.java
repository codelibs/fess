package org.codelibs.fess.es.config.exentity;

import java.util.Map;

import org.codelibs.fess.crawler.client.CrawlerClientFactory;

public class CrawlingConfigWrapper implements CrawlingConfig {

    private CrawlingConfig crawlingConfig;

    public CrawlingConfigWrapper(final CrawlingConfig crawlingConfig) {
        this.crawlingConfig = crawlingConfig;
    }

    public String getId() {
        return crawlingConfig.getId();
    }

    public String getName() {
        return crawlingConfig.getName();
    }

    public String[] getPermissions() {
        return crawlingConfig.getPermissions();
    }

    public String[] getLabelTypeValues() {
        return crawlingConfig.getLabelTypeValues();
    }

    public String getDocumentBoost() {
        return crawlingConfig.getDocumentBoost();
    }

    public String getIndexingTarget(String input) {
        return crawlingConfig.getIndexingTarget(input);
    }

    public String getConfigId() {
        return crawlingConfig.getConfigId();
    }

    public Integer getTimeToLive() {
        return crawlingConfig.getTimeToLive();
    }

    public Map<String, Object> initializeClientFactory(CrawlerClientFactory crawlerClientFactory) {
        return crawlingConfig.initializeClientFactory(crawlerClientFactory);
    }

    public Map<String, String> getConfigParameterMap(ConfigName name) {
        return crawlingConfig.getConfigParameterMap(name);
    }
}
