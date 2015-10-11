package org.codelibs.fess.es.exentity;

import java.util.Map;

import org.codelibs.fess.crawler.client.CrawlerClientFactory;

public interface CrawlingConfig {

    String getId();

    String getName();

    String[] getRoleTypeValues();

    String[] getLabelTypeValues();

    String getDocumentBoost();

    String getIndexingTarget(String input);

    String getConfigId();

    void initializeClientFactory(CrawlerClientFactory crawlerClientFactory);

    Map<String, String> getConfigParameterMap(ConfigName name);

    public enum ConfigType {
        WEB("W"), FILE("F"), DATA("D");

        private final String typePrefix;

        ConfigType(final String typePrefix) {
            this.typePrefix = typePrefix;
        }

        public String getTypePrefix() {
            return typePrefix;
        }

        public String getConfigId(final String id) {
            if (id == null) {
                return null;
            }
            return typePrefix + id.toString();
        }
    }

    public enum ConfigName {
        CLIENT, XPATH, META, VALUE, SCRIPT, FIELD;
    }
}