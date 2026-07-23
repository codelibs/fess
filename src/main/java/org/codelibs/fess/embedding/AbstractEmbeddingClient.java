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
package org.codelibs.fess.embedding;

import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Shared HTTP/proxy infrastructure for {@link EmbeddingClient}
 * implementations. Copied from {@code org.codelibs.fess.llm.AbstractLlmClient},
 * keeping only the parts with an embedding analog (register/init/destroy,
 * proxy configuration, availability-check scheduling);
 * the RAG-specific prompt/chat/stream logic has no counterpart here.
 */
public abstract class AbstractEmbeddingClient implements EmbeddingClient {

    /** System property key for the content-chunking feature toggle. */
    public static final String CONTENT_CHUNKER_ENABLED_PROPERTY = "content_chunker.enabled";

    /** System property key for the active {@link EmbeddingClient} name. */
    public static final String EMBEDDING_NAME_PROPERTY = "content_chunker.embedding.name";

    /** System property key for the vector dimension of the configured embedding provider. */
    public static final String EMBEDDING_DIMENSION_PROPERTY = "content_chunker.embedding.dimension";

    /**
     * Default value of {@link #EMBEDDING_NAME_PROPERTY}: the built-in OpenSearch
     * ML Commons provider, which needs no extra infrastructure.
     */
    public static final String EMBEDDING_NAME_DEFAULT = "opensearch";

    private static final Logger logger = LogManager.getLogger(AbstractEmbeddingClient.class);

    /** The HTTP client used for API communication. */
    protected CloseableHttpClient httpClient;

    /** Cached availability status of the embedding provider. */
    protected volatile Boolean cachedAvailability = null;

    /** The scheduled task for periodic availability checks. */
    protected TimeoutTask availabilityCheckTask;

    /**
     * Set once {@link #destroy()} has run. Guards {@link #getHttpClient()} against silently
     * recreating an HTTP client for a client instance the container has already torn down.
     */
    protected volatile boolean destroyed = false;

    /**
     * Default constructor.
     */
    public AbstractEmbeddingClient() {
        // Default constructor
    }

    /**
     * Registers this client with the {@link EmbeddingClientManager}.
     * Called via postConstruct before init().
     */
    public void register() {
        if (ComponentUtil.hasComponent("embeddingClientManager")) {
            ComponentUtil.getComponent(EmbeddingClientManager.class).register(this);
        }
    }

    /**
     * Initializes the HTTP client and starts availability checking.
     * Should be called from subclass init() methods.
     */
    public void init() {
        if (!getName().equals(getEmbeddingType())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping availability check. embeddingType={}, name={}", getEmbeddingType(), getName());
            }
            return;
        }

        final int timeout = getTimeout();
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(timeout))
                .setResponseTimeout(Timeout.ofMilliseconds(timeout))
                .build();
        final HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultConnectionConfig(ConnectionConfig.custom().setConnectTimeout(Timeout.ofMilliseconds(timeout)).build())
                        .build())
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries();
        configureProxy(builder);
        httpClient = builder.build();
        if (logger.isDebugEnabled()) {
            logger.debug("[Embedding] {} initialized. timeout={}ms", getName(), timeout);
        }

        startAvailabilityCheck();
    }

    /**
     * Configures proxy settings on the HTTP client builder if a proxy is configured.
     *
     * @param builder the HTTP client builder to configure
     */
    protected void configureProxy(final HttpClientBuilder builder) {
        final String proxyHost = getProxyHost();
        final Integer proxyPort = getProxyPort();
        if (StringUtil.isBlank(proxyHost) || proxyPort == null) {
            return;
        }
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        builder.setProxy(proxy);
        final String proxyUsername = getProxyUsername();
        if (StringUtil.isNotBlank(proxyUsername)) {
            final String proxyPassword = getProxyPassword();
            final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                    new UsernamePasswordCredentials(proxyUsername, proxyPassword != null ? proxyPassword.toCharArray() : new char[0]));
            builder.setDefaultCredentialsProvider(credsProvider);
        }
    }

    /**
     * Gets the HTTP proxy host. Defaults to {@code http.proxy.host} from FessConfig.
     *
     * @return the proxy host, or null/blank if no proxy is configured
     */
    protected String getProxyHost() {
        return ComponentUtil.getFessConfig().getHttpProxyHost();
    }

    /**
     * Gets the HTTP proxy port. Defaults to {@code http.proxy.port} from FessConfig.
     *
     * @return the proxy port, or null if no proxy is configured
     */
    protected Integer getProxyPort() {
        return ComponentUtil.getFessConfig().getHttpProxyPortAsInteger();
    }

    /**
     * Gets the HTTP proxy username. Defaults to {@code http.proxy.username} from FessConfig.
     *
     * @return the proxy username, or null/blank if no proxy authentication is required
     */
    protected String getProxyUsername() {
        return ComponentUtil.getFessConfig().getHttpProxyUsername();
    }

    /**
     * Gets the HTTP proxy password. Defaults to {@code http.proxy.password} from FessConfig.
     *
     * @return the proxy password, or null if no proxy authentication is required
     */
    protected String getProxyPassword() {
        return ComponentUtil.getFessConfig().getHttpProxyPassword();
    }

    /**
     * Cleans up resources. After this call, {@link #getHttpClient()} throws
     * {@link IllegalStateException} instead of silently recreating a client.
     */
    public synchronized void destroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("[Embedding] {} shutting down.", getName());
        }
        destroyed = true;
        if (availabilityCheckTask != null && !availabilityCheckTask.isCanceled()) {
            availabilityCheckTask.cancel();
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.warn("Failed to close HTTP client", e);
            }
            httpClient = null;
        }
    }

    /**
     * Starts periodic availability checking if content chunking is enabled.
     */
    protected void startAvailabilityCheck() {
        if (!isContentChunkerEnabled()) {
            return;
        }
        final int checkInterval = getAvailabilityCheckInterval();
        if (checkInterval <= 0) {
            return;
        }
        updateAvailability();
        availabilityCheckTask = TimeoutManager.getInstance().addTimeoutTarget(this::updateAvailability, checkInterval, true);
    }

    /**
     * Updates the cached availability state.
     */
    protected void updateAvailability() {
        cachedAvailability = checkAvailabilityNow();
    }

    @Override
    public boolean isAvailable() {
        if (cachedAvailability != null) {
            return cachedAvailability;
        }
        return checkAvailabilityNow();
    }

    /**
     * Gets the HTTP client, initializing it if necessary.
     *
     * <p>Synchronized so two threads racing here on the first lazy call -- e.g. right after
     * a live {@code content_chunker.embedding.name}/{@code rag.llm.name} switch activates a
     * client whose {@code init()} was skipped at postConstruct time (the name-mismatch guard
     * in {@link #init()}) -- cannot both observe {@code httpClient == null} and each build (and
     * leak) their own client.</p>
     *
     * @return the HTTP client
     * @throws IllegalStateException if this client has already been {@link #destroy() destroyed}
     */
    public synchronized CloseableHttpClient getHttpClient() {
        if (destroyed) {
            throw new IllegalStateException(getName() + " embedding client has already been destroyed");
        }
        if (httpClient == null) {
            init();
        }
        return httpClient;
    }

    /**
     * Performs the actual availability check against the embedding provider.
     *
     * @return true if the provider is available
     */
    protected abstract boolean checkAvailabilityNow();

    /**
     * Gets the request timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    protected abstract int getTimeout();

    /**
     * Gets the availability check interval in seconds.
     *
     * @return the interval in seconds
     */
    protected abstract int getAvailabilityCheckInterval();

    /**
     * Checks if content chunking is enabled.
     *
     * @return true if enabled
     */
    protected abstract boolean isContentChunkerEnabled();

    /**
     * Gets the configured embedding provider type from the
     * {@value #EMBEDDING_NAME_PROPERTY} system property.
     *
     * @return the embedding type from configuration (default {@value #EMBEDDING_NAME_DEFAULT})
     */
    protected String getEmbeddingType() {
        return ComponentUtil.getFessConfig().getSystemProperty(EMBEDDING_NAME_PROPERTY, EMBEDDING_NAME_DEFAULT);
    }

    /**
     * Gets the configuration prefix for this provider.
     *
     * @return the config prefix (e.g. "content_chunker.embedding.ollama")
     */
    protected abstract String getConfigPrefix();

    /**
     * Gets an integer configuration value using the config prefix and key suffix.
     *
     * @param keySuffix the key suffix (appended to getConfigPrefix() + ".")
     * @param defaultValue the default value
     * @return the configured or default value
     */
    protected int getConfigInt(final String keySuffix, final int defaultValue) {
        final String key = getConfigPrefix() + "." + keySuffix;
        final String configValue = ComponentUtil.getFessConfig().getOrDefault(key, null);
        if (configValue != null) {
            try {
                final int value = Integer.parseInt(configValue);
                if (value > 0) {
                    return value;
                }
            } catch (final NumberFormatException e) {
                logger.warn("Invalid config value for key={}. Using default: {}", key, defaultValue);
            }
        }
        return defaultValue;
    }

}
