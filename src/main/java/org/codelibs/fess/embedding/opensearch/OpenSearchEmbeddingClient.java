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
package org.codelibs.fess.embedding.opensearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.embedding.AbstractEmbeddingClient;
import org.codelibs.fess.embedding.EmbeddingException;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SystemUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Embedding client implementation for OpenSearch ML Commons.
 * Calls the model-scoped Predict API
 * ({@code POST /_plugins/_ml/models/{model_id}/_predict}) against a
 * pre-registered, pre-deployed {@code TEXT_EMBEDDING} model on the OpenSearch
 * cluster - by default the same cluster Fess already uses as its search
 * engine. The model must be deployed by the operator beforehand; this client
 * performs no model management (no register/deploy/undeploy, no task polling).
 *
 * @see <a href="https://docs.opensearch.org/latest/ml-commons-plugin/api/train-predict/predict/">ML Commons Predict API</a>
 */
public class OpenSearchEmbeddingClient extends AbstractEmbeddingClient {

    private static final Logger logger = LogManager.getLogger(OpenSearchEmbeddingClient.class);

    /** Shared ObjectMapper instance for JSON processing. */
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /** The name identifier for the OpenSearch embedding client. */
    protected static final String NAME = "opensearch";

    /** Hard cap on a single backoff sleep, regardless of computed delay. */
    private static final long MAX_BACKOFF_MS = 60_000L;

    /**
     * Maximum number of texts sent in a single
     * {@code POST /_plugins/_ml/models/{model_id}/_predict} request.
     * ML Commons documents no hard limit on the {@code text_docs} array, so
     * this is a protective soft cap rather than an API requirement. fess
     * core's {@code ChunkVectorHelper} can flatten every chunk across a whole
     * {@code bulk_size} of documents into one list, and each predict call runs
     * as a synchronous task on the ML threadpool of the same cluster serving
     * live search traffic; oversized batches raise heap pressure that trips
     * the ML memory circuit breaker (a retryable 429). 128 keeps each request
     * bounded while still amortizing HTTP overhead across a reasonable batch;
     * larger lists are split into contiguous sub-batches of at most this size
     * and their vectors concatenated in order.
     */
    private static final int MAX_BATCH_ITEMS = 128;

    private static final String CONFIG_API_URL = "api.url";
    private static final String CONFIG_MODEL_ID = "model.id";
    private static final String CONFIG_RETRY_MAX = "retry.max";
    private static final String CONFIG_RETRY_BASE_DELAY_MS = "retry.base.delay.ms";
    private static final String CONFIG_CONNECT_TIMEOUT = "connect.timeout";
    private static final String CONFIG_DOCUMENT_PREFIX = "document.prefix";
    private static final String CONFIG_QUERY_PREFIX = "query.prefix";
    private static final String CONFIG_USERNAME = "username";
    private static final String CONFIG_PASSWORD = "password";

    /** Final fallback API URL when neither config nor the search-engine address system property is set. */
    protected static final String DEFAULT_API_URL = "http://localhost:9200";

    /** ML Commons model state meaning the model is deployed on every eligible node. */
    protected static final String MODEL_STATE_DEPLOYED = "DEPLOYED";

    /** ML Commons model state meaning the model is deployed on a subset of eligible nodes; predict still works. */
    protected static final String MODEL_STATE_PARTIALLY_DEPLOYED = "PARTIALLY_DEPLOYED";

    /** The ML Commons algorithm expected for models used by this client. */
    protected static final String EXPECTED_ALGORITHM = "TEXT_EMBEDDING";

    /** Tensor name selected via {@code target_response} and located in the predict output. */
    protected static final String SENTENCE_EMBEDDING = "sentence_embedding";

    /** Accepts ML Commons model ids (URL-safe tokens) for safe splicing into the request path. */
    protected static final Pattern MODEL_ID_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-]+");

    /**
     * Default prefix prepended to document/chunk texts: empty. Pretrained
     * OpenSearch models (MiniLM etc.) use no prefix convention; configure the
     * {@code document.prefix}/{@code query.prefix} keys for e5-style models.
     */
    protected static final String DEFAULT_DOCUMENT_PREFIX = StringUtil.EMPTY;

    /** Default prefix prepended to query texts: empty (see {@link #DEFAULT_DOCUMENT_PREFIX}). */
    protected static final String DEFAULT_QUERY_PREFIX = StringUtil.EMPTY;

    /**
     * Default constructor.
     */
    public OpenSearchEmbeddingClient() {
        // Default constructor
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getDimension() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(EMBEDDING_DIMENSION_PROPERTY, null);
        if (StringUtil.isBlank(value)) {
            throw new EmbeddingException(EMBEDDING_DIMENSION_PROPERTY + " is not configured");
        }
        final int dimension;
        try {
            dimension = Integer.parseInt(value.trim());
        } catch (final NumberFormatException e) {
            throw new EmbeddingException("Invalid " + EMBEDDING_DIMENSION_PROPERTY + " value: " + value, e);
        }
        if (dimension <= 0) {
            throw new EmbeddingException(EMBEDDING_DIMENSION_PROPERTY + " must be positive: " + value);
        }
        return dimension;
    }

    @Override
    protected boolean checkAvailabilityNow() {
        final String apiUrl = getApiUrl();
        final String modelId = getModelId();
        if (StringUtil.isBlank(apiUrl) || StringUtil.isBlank(modelId)) {
            return false;
        }
        try {
            final HttpGet request = new HttpGet(apiUrl + "/_plugins/_ml/models/" + modelId);
            try (var response = getHttpClient().execute(request)) {
                final int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[Embedding:OPENSEARCH] Model get failed. modelId={}, statusCode={}", modelId, statusCode);
                    }
                    return false;
                }
                final String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                return isModelDeployed(responseBody);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("[Embedding:OPENSEARCH] OpenSearch is not available. url={}, error={}", apiUrl, e.getMessage());
            }
            return false;
        }
    }

    /**
     * Checks whether the model described by the
     * {@code GET /_plugins/_ml/models/{model_id}} response body is in a
     * deployed state ({@code DEPLOYED} or {@code PARTIALLY_DEPLOYED}). Every
     * other state ({@code REGISTERING}, {@code REGISTERED}, {@code DEPLOYING},
     * {@code UNDEPLOYED}, {@code DEPLOY_FAILED}, ...) means predict calls
     * would fail, so it is reported as unavailable. Also emits WARN-level
     * diagnostics via {@link #warnOnModelDiagnostics(JsonNode)}.
     *
     * @param responseBody the response body from the model get endpoint
     * @return true if the model is deployed enough to serve predict calls
     */
    protected boolean isModelDeployed(final String responseBody) {
        try {
            final JsonNode jsonNode = objectMapper.readTree(responseBody);
            warnOnModelDiagnostics(jsonNode);
            final String modelState = jsonNode.path("model_state").asText();
            if (MODEL_STATE_DEPLOYED.equals(modelState) || MODEL_STATE_PARTIALLY_DEPLOYED.equals(modelState)) {
                return true;
            }
            logger.warn("[Embedding:OPENSEARCH] Model is not deployed. modelId={}, modelState={}", getModelId(), modelState);
            return false;
        } catch (final Exception e) {
            logger.warn("[Embedding:OPENSEARCH] Failed to parse OpenSearch ML model response. error={}", e.getMessage());
            return false;
        }
    }

    /**
     * Emits WARN-level diagnostics for likely misconfigurations visible in the
     * model document, without failing the availability check: an
     * {@code algorithm} other than {@code TEXT_EMBEDDING}, and a
     * {@code model_config.embedding_dimension} that differs from the configured
     * {@link #getDimension()}. The latter catches a dimension misconfiguration
     * at startup instead of at the first embed call. Diagnostics only; the
     * availability result is unaffected.
     *
     * @param modelNode the parsed model get response
     */
    protected void warnOnModelDiagnostics(final JsonNode modelNode) {
        final JsonNode algorithmNode = modelNode.path("algorithm");
        if (!algorithmNode.isMissingNode() && !EXPECTED_ALGORITHM.equals(algorithmNode.asText())) {
            logger.warn("[Embedding:OPENSEARCH] Model algorithm is not {}. modelId={}, algorithm={}", EXPECTED_ALGORITHM, getModelId(),
                    algorithmNode.asText());
        }
        final JsonNode dimensionNode = modelNode.path("model_config").path("embedding_dimension");
        if (dimensionNode.isNumber()) {
            try {
                final int configuredDimension = getDimension();
                if (dimensionNode.asInt() != configuredDimension) {
                    logger.warn("[Embedding:OPENSEARCH] Model embedding dimension mismatch. modelId={}, model={}, configured={}",
                            getModelId(), dimensionNode.asInt(), configuredDimension);
                }
            } catch (final EmbeddingException e) {
                // The configured dimension is missing or invalid; this method is a
                // diagnostic aid only, so do not let getDimension()'s validation
                // failure break the availability check.
                if (logger.isDebugEnabled()) {
                    logger.debug("[Embedding:OPENSEARCH] Skipping dimension diagnostic. error={}", e.getMessage());
                }
            }
        }
    }

    @Override
    public List<float[]> embedDocuments(final List<String> texts) {
        return embedInSubBatches("embedDocuments", applyPrefix(texts, getDocumentPrefix()));
    }

    @Override
    public List<float[]> embedQuery(final List<String> texts) {
        return embedInSubBatches("embedQuery", applyPrefix(texts, getQueryPrefix()));
    }

    /**
     * Splits the (already prefixed, if applicable) {@code texts} into contiguous
     * sub-batches of at most {@link #MAX_BATCH_ITEMS} items, calls
     * {@link #callEmbedApi(String, List)} once per sub-batch, and concatenates
     * the resulting vectors in input order. Lists at or below the cap are sent as
     * a single request. See {@link #MAX_BATCH_ITEMS} for why the cap exists.
     *
     * <p>Invariants preserved across the concatenation: output order equals input
     * order; the result count equals the input count; empty/{@code null} input
     * returns an empty list with no API call (which also avoids the server's
     * {@code "Empty text docs"} 400); and any sub-batch failure (after its own
     * retries) propagates, failing the whole call.
     *
     * @param operation log label, e.g. {@code "embedDocuments"} or {@code "embedQuery"}
     * @param texts the texts to embed, in the form to send as-is to the API
     * @return the parsed vectors, one per input text, in the same order
     * @throws EmbeddingException if any sub-batch call fails or returns an unusable response
     */
    private List<float[]> embedInSubBatches(final String operation, final List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return Collections.emptyList();
        }
        final int total = texts.size();
        if (total <= MAX_BATCH_ITEMS) {
            return callEmbedApi(operation, texts);
        }
        final List<float[]> vectors = new ArrayList<>(total);
        for (int start = 0; start < total; start += MAX_BATCH_ITEMS) {
            final int end = Math.min(start + MAX_BATCH_ITEMS, total);
            // subList is a read-only view; callEmbedApi only serializes and sizes it.
            vectors.addAll(callEmbedApi(operation, texts.subList(start, end)));
        }
        return vectors;
    }

    /**
     * Prepends {@code prefix} to every element of {@code texts}. A blank
     * ({@code null} or empty) prefix is treated as a no-op, leaving {@code texts}
     * unchanged rather than concatenating an empty string.
     *
     * @param texts the input texts
     * @param prefix the prefix to prepend, or blank for none
     * @return the prefixed texts, or {@code texts} unchanged when {@code prefix} is blank
     */
    static List<String> applyPrefix(final List<String> texts, final String prefix) {
        if (texts == null || texts.isEmpty() || StringUtil.isBlank(prefix)) {
            return texts;
        }
        final List<String> prefixed = new ArrayList<>(texts.size());
        for (final String text : texts) {
            prefixed.add(prefix + text);
        }
        return prefixed;
    }

    /**
     * Calls ML Commons' {@code POST /_plugins/_ml/models/{model_id}/_predict}
     * endpoint with the given (already prefixed, if applicable) texts as a
     * single request. The body always carries {@code "return_number": true}
     * (without it the response omits the {@code data} field entirely) and
     * {@code "target_response": ["sentence_embedding"]} (without it every doc
     * returns four tensors including per-token embeddings, a much larger
     * payload). Callers must bound the sub-batch size;
     * {@link #embedInSubBatches(String, List)} splits larger inputs into
     * sub-batches of at most {@link #MAX_BATCH_ITEMS} and invokes this method
     * once per sub-batch.
     *
     * @param operation log label, e.g. {@code "embedDocuments"} or {@code "embedQuery"}
     * @param texts the texts to embed, in the form to send as-is to the API
     * @return the parsed vectors, one per input text, in the same order
     * @throws EmbeddingException if the model id is not configured, the provider call fails,
     *         or the response is unusable
     */
    private List<float[]> callEmbedApi(final String operation, final List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return Collections.emptyList();
        }
        final String modelId = getModelId();
        if (StringUtil.isBlank(modelId)) {
            throw new EmbeddingException(getConfigPrefix() + "." + CONFIG_MODEL_ID + " is not configured");
        }
        final String url = getApiUrl() + "/_plugins/_ml/models/" + modelId + "/_predict";
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text_docs", texts);
        requestBody.put("return_number", true);
        requestBody.put("target_response", List.of(SENTENCE_EMBEDDING));
        final long startTime = System.currentTimeMillis();
        try {
            final String json = objectMapper.writeValueAsString(requestBody);
            return executeWithRetry(operation, () -> {
                final HttpPost httpRequest = new HttpPost(url);
                httpRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                try (var response = getHttpClient().execute(httpRequest)) {
                    final int statusCode = response.getCode();
                    if (statusCode < 200 || statusCode >= 300) {
                        if (isRetryableStatus(statusCode)) {
                            throw new RetryableHttpException(statusCode, response.getReasonPhrase());
                        }
                        logger.warn("[Embedding:OPENSEARCH] API error. url={}, statusCode={}, message={}", url, statusCode,
                                response.getReasonPhrase());
                        throw new EmbeddingException("OpenSearch ML predict API error: " + statusCode + " " + response.getReasonPhrase());
                    }
                    String responseBody;
                    try {
                        responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                    } catch (final org.apache.hc.core5.http.ParseException pe) {
                        throw new IOException("Failed to parse OpenSearch ML predict response body", pe);
                    }
                    final List<float[]> vectors = parsePredictResponse(responseBody, texts.size());
                    logger.info("[Embedding:OPENSEARCH] {} response received. count={}, elapsedTime={}ms", operation, vectors.size(),
                            System.currentTimeMillis() - startTime);
                    return vectors;
                }
            });
        } catch (final EmbeddingException e) {
            throw e;
        } catch (final Exception e) {
            logger.warn("[Embedding:OPENSEARCH] Failed to call OpenSearch ML predict API. url={}, error={}", url, e.getMessage(), e);
            throw new EmbeddingException("Failed to call OpenSearch ML predict API", e);
        }
    }

    /**
     * Parses the predict response body into a list of vectors, validating that
     * the returned {@code inference_results} count matches
     * {@code expectedCount} and that every vector's length matches
     * {@link #getDimension()}. Each result's {@code output} array is scanned
     * for the entry named {@code sentence_embedding} (a defensive
     * select-by-name, even though {@code target_response} makes it the only
     * entry) and its {@code data} array is read positionally: N
     * {@code text_docs} produce exactly N {@code inference_results} in input
     * order with no per-item id/index field, so reassembling by array order is
     * correct (same situation as Ollama's {@code /api/embed}).
     *
     * @param responseBody the raw JSON response body
     * @param expectedCount the expected number of vectors (= number of input texts)
     * @return the parsed vectors, in response order
     * @throws EmbeddingException if the response is malformed or a count/dimension mismatch is detected
     */
    protected List<float[]> parsePredictResponse(final String responseBody, final int expectedCount) {
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (final IOException e) {
            throw new EmbeddingException("Failed to parse OpenSearch ML predict response", e);
        }
        final JsonNode resultsNode = jsonNode.path("inference_results");
        if (!resultsNode.isArray()) {
            throw new EmbeddingException("OpenSearch ML predict response missing 'inference_results' array");
        }
        if (resultsNode.size() != expectedCount) {
            throw new EmbeddingException(
                    "OpenSearch ML predict response count mismatch: expected=" + expectedCount + ", actual=" + resultsNode.size());
        }
        final int dimension = getDimension();
        final List<float[]> vectors = new ArrayList<>(resultsNode.size());
        int vectorIndex = 0;
        for (final JsonNode resultNode : resultsNode) {
            final JsonNode dataNode = findSentenceEmbeddingData(resultNode);
            if (dataNode == null) {
                throw new EmbeddingException(
                        "OpenSearch ML predict response missing '" + SENTENCE_EMBEDDING + "' output: index=" + vectorIndex);
            }
            if (!dataNode.isArray() || dataNode.size() != dimension) {
                throw new EmbeddingException("OpenSearch ML predict vector dimension mismatch: expected=" + dimension + ", actual="
                        + (dataNode.isArray() ? dataNode.size() : -1));
            }
            final float[] vector = new float[dimension];
            for (int i = 0; i < dimension; i++) {
                final JsonNode componentNode = dataNode.get(i);
                if (componentNode == null || !componentNode.isNumber()) {
                    throw new EmbeddingException(
                            "OpenSearch ML predict vector component is not numeric: index=" + vectorIndex + ", position=" + i);
                }
                // A JSON magnitude that overflows the double range (e.g. 1e999) parses as a
                // NUMBER node whose value is +/-Infinity and passes isNumber(); reject any
                // non-finite component so an unusable vector is never stored silently.
                final float component = (float) componentNode.asDouble();
                if (!Float.isFinite(component)) {
                    throw new EmbeddingException(
                            "OpenSearch ML predict vector component is not finite: index=" + vectorIndex + ", position=" + i);
                }
                vector[i] = component;
            }
            vectors.add(vector);
            vectorIndex++;
        }
        return vectors;
    }

    /**
     * Locates the {@code data} array of the {@code sentence_embedding} entry in
     * a single inference result's {@code output} array.
     *
     * @param resultNode one entry of the {@code inference_results} array
     * @return the {@code data} node of the {@code sentence_embedding} output, or
     *         {@code null} when no such output entry exists
     */
    protected JsonNode findSentenceEmbeddingData(final JsonNode resultNode) {
        final JsonNode outputNode = resultNode.path("output");
        if (!outputNode.isArray()) {
            return null;
        }
        for (final JsonNode tensorNode : outputNode) {
            if (SENTENCE_EMBEDDING.equals(tensorNode.path("name").asText())) {
                return tensorNode.path("data");
            }
        }
        return null;
    }

    /**
     * Gets the OpenSearch API URL. Resolution order: the configured
     * {@code content_chunker.embedding.opensearch.api.url} value, then the
     * {@code fess.search_engine.http_address} system property (set for the web
     * JVM by {@code SearchEngineClient} and forwarded to the chunk child JVM by
     * {@code ChunkVectorJob}), then {@value #DEFAULT_API_URL}. Normalized by
     * stripping trailing slashes only, so callers can append fixed
     * {@code /_plugins/_ml/...} paths without producing duplicates.
     *
     * @return the normalized API base URL
     */
    protected String getApiUrl() {
        String url = getConfigString(CONFIG_API_URL, StringUtil.EMPTY);
        if (StringUtil.isBlank(url)) {
            url = SystemUtil.getSearchEngineHttpAddress();
        }
        if (StringUtil.isBlank(url)) {
            url = DEFAULT_API_URL;
        }
        return normalizeApiUrl(url);
    }

    /**
     * Strips trailing {@code /} characters from an OpenSearch base URL. Unlike
     * the Ollama sibling there is no {@code /api}-segment stripping - that
     * normalization is Ollama-specific.
     *
     * @param url the raw configured URL
     * @return the normalized URL, or the input unchanged when blank
     */
    static String normalizeApiUrl(final String url) {
        if (url == null) {
            return null;
        }
        String result = url.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * Gets the configured ML Commons model id. Required: a blank value makes
     * {@link #checkAvailabilityNow()} return false and embed calls throw
     * {@link EmbeddingException}. Re-read on every call so the model can be
     * swapped without a restart.
     *
     * @return the configured model id, or blank when not configured
     */
    protected String getModelId() {
        final String modelId = getConfigString(CONFIG_MODEL_ID, StringUtil.EMPTY);
        if (StringUtil.isNotBlank(modelId) && !MODEL_ID_PATTERN.matcher(modelId).matches()) {
            // The id is spliced into the request path; reject anything that could re-route it
            // (ML Commons ids are URL-safe base64-style tokens) and surface a clear error
            // instead of a mangled 404.
            throw new EmbeddingException("Invalid " + getConfigPrefix() + "." + CONFIG_MODEL_ID + ": " + modelId);
        }
        return modelId;
    }

    @Override
    protected int getTimeout() {
        return getConfigInt("timeout", 60000);
    }

    /**
     * Gets the TCP connect timeout in milliseconds, separate from
     * {@link #getTimeout()} (response/read timeout).
     *
     * @return the connect timeout in milliseconds
     */
    protected int getConnectTimeout() {
        return getConfigInt(CONFIG_CONNECT_TIMEOUT, 5000);
    }

    /**
     * Overrides {@link AbstractEmbeddingClient#init()} to apply distinct
     * connect and response timeouts and the preemptive basic-auth header,
     * mirroring {@code OllamaEmbeddingClient.init()}.
     */
    @Override
    public void init() {
        if (!getName().equals(getEmbeddingType())) {
            return;
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.warn("[Embedding:OPENSEARCH] Failed to close prior HTTP client during re-init", e);
            }
        }
        httpClient = buildHttpClient();
        startAvailabilityCheck();
    }

    /**
     * Builds the {@link CloseableHttpClient} with two-tier timeouts (connect vs
     * response/read), the shared proxy configuration, and - when both a
     * username and a password resolve to non-blank values - a preemptive
     * {@code Authorization: Basic ...} default header, matching how fess core
     * authenticates against the same cluster.
     *
     * @return a configured {@link CloseableHttpClient}
     */
    protected CloseableHttpClient buildHttpClient() {
        final int connectTimeout = getConnectTimeout();
        final int responseTimeout = getTimeout();
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
                .build();
        final HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultConnectionConfig(
                                ConnectionConfig.custom().setConnectTimeout(Timeout.ofMilliseconds(connectTimeout)).build())
                        .build())
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries();
        final String username = getUsername();
        final String password = getPassword();
        if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
            final String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            builder.setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + token)));
            // Never log the password (or the token, which encodes it).
            if (logger.isDebugEnabled()) {
                logger.debug("[Embedding:OPENSEARCH] Basic authentication enabled. username={}", username);
            }
        }
        configureProxy(builder);
        return builder.build();
    }

    /**
     * Gets the basic-auth username: the configured
     * {@code content_chunker.embedding.opensearch.username}, falling back to
     * fess core's search-engine username ({@code search_engine.username})
     * since the default URL is the same cluster.
     *
     * @return the resolved username, or blank when none is configured
     */
    protected String getUsername() {
        final String value = getConfigString(CONFIG_USERNAME, StringUtil.EMPTY);
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return ComponentUtil.getFessConfig().getFesenUsername();
    }

    /**
     * Gets the basic-auth password: the configured
     * {@code content_chunker.embedding.opensearch.password}, falling back to
     * fess core's search-engine password ({@code search_engine.password})
     * since the default URL is the same cluster. Never logged.
     *
     * @return the resolved password, or blank when none is configured
     */
    protected String getPassword() {
        final String value = getConfigString(CONFIG_PASSWORD, StringUtil.EMPTY);
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return ComponentUtil.getFessConfig().getFesenPassword();
    }

    @Override
    protected String getConfigPrefix() {
        return "content_chunker.embedding.opensearch";
    }

    /**
     * Gets a String configuration value using the config prefix and key suffix.
     *
     * @param keySuffix the key suffix (appended to {@code getConfigPrefix() + "."})
     * @param defaultValue the default value
     * @return the configured or default value
     */
    protected String getConfigString(final String keySuffix, final String defaultValue) {
        final String key = getConfigPrefix() + "." + keySuffix;
        return ComponentUtil.getFessConfig().getOrDefault(key, defaultValue);
    }

    /**
     * Gets the prefix prepended to document/chunk texts before embedding
     * (see {@link #embedDocuments(List)}). Defaults to empty (pretrained
     * OpenSearch models use no prefix convention); set it for e5-style models
     * that expect e.g. {@code "passage: "}.
     *
     * @return the configured document prefix
     */
    protected String getDocumentPrefix() {
        return getConfigString(CONFIG_DOCUMENT_PREFIX, DEFAULT_DOCUMENT_PREFIX);
    }

    /**
     * Gets the prefix prepended to query texts before embedding (see
     * {@link #embedQuery(List)}). Defaults to empty (pretrained OpenSearch
     * models use no prefix convention); set it for e5-style models that expect
     * e.g. {@code "query: "}.
     *
     * @return the configured query prefix
     */
    protected String getQueryPrefix() {
        return getConfigString(CONFIG_QUERY_PREFIX, DEFAULT_QUERY_PREFIX);
    }

    @Override
    protected int getAvailabilityCheckInterval() {
        return getConfigInt("availability.check.interval", 60);
    }

    @Override
    protected boolean isContentChunkerEnabled() {
        return Boolean.parseBoolean(ComponentUtil.getFessConfig().getSystemProperty(CONTENT_CHUNKER_ENABLED_PROPERTY, "false"));
    }

    /**
     * Functional interface for the retryable HTTP call body executed by
     * {@link #executeWithRetry(String, HttpCall)}.
     *
     * @param <T> the call result type
     */
    @FunctionalInterface
    interface HttpCall<T> {
        /**
         * Executes the HTTP call body.
         *
         * @return the call result
         * @throws IOException if the call fails at the transport level
         */
        T call() throws IOException;
    }

    /**
     * Internal signaling exception thrown by the HTTP call body when the
     * response status code is retryable. Caught by
     * {@link #executeWithRetry(String, HttpCall)}; never escapes the client.
     */
    static final class RetryableHttpException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /** The retryable HTTP status code. */
        final int statusCode;

        /** The HTTP reason phrase accompanying the status code. */
        final String reason;

        /**
         * Creates a new instance.
         *
         * @param statusCode the retryable HTTP status code
         * @param reason the HTTP reason phrase
         */
        RetryableHttpException(final int statusCode, final String reason) {
            super("retryable http error: " + statusCode + " " + reason);
            this.statusCode = statusCode;
            this.reason = reason;
        }
    }

    /**
     * Returns whether the given HTTP status code should be retried:
     * {@code 429}, {@code 500}, {@code 502}, {@code 503}, {@code 504}.
     * 429 is the primary transient failure mode here - the ML memory circuit
     * breaker returns 429 with {@code "durability":"TRANSIENT"} on a
     * heap-pressured cluster; ML Commons wraps unexpected engine exceptions as
     * 500 (retry is safe because predict is idempotent); 502/503/504 cover
     * proxies/load balancers and node-rejoin windows. 400/404 are
     * configuration/usage errors ({@code Model not ready yet},
     * {@code Failed to find model}) and are never retried.
     *
     * @param statusCode the HTTP status code
     * @return true when the status is retryable
     */
    static boolean isRetryableStatus(final int statusCode) {
        return statusCode == 429 || statusCode == 500 || statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    /**
     * Maximum total attempts (including the first) for a retryable call.
     *
     * @return the value of {@code content_chunker.embedding.opensearch.retry.max} (default 3)
     */
    protected int getRetryMaxAttempts() {
        return getConfigInt(CONFIG_RETRY_MAX, 3);
    }

    /**
     * Base delay in milliseconds for exponential backoff between retries.
     *
     * @return the value of {@code content_chunker.embedding.opensearch.retry.base.delay.ms} (default 2000)
     */
    protected long getRetryBaseDelayMs() {
        final String raw = ComponentUtil.getFessConfig().getOrDefault(getConfigPrefix() + "." + CONFIG_RETRY_BASE_DELAY_MS, "2000");
        try {
            return Long.parseLong(raw);
        } catch (final NumberFormatException e) {
            logger.warn("[Embedding:OPENSEARCH] Invalid {}.{}='{}', using default 2000ms", getConfigPrefix(), CONFIG_RETRY_BASE_DELAY_MS,
                    raw);
            return 2000L;
        }
    }

    /**
     * Executes {@code call} with retry on {@link RetryableHttpException} and
     * on transient connect-time {@link IOException}s. {@link EmbeddingException}
     * (RuntimeException) is not caught here and propagates immediately.
     * Backoff is exponential ({@code base * 2^(attempt-1)}) with +/-20% jitter.
     *
     * @param operation log label, e.g. {@code "embedDocuments"}
     * @param call the HTTP call body
     * @param <T> the call result type
     * @return the call result on success
     * @throws IOException if the call throws a non-retryable {@link IOException} or the retry budget is exhausted
     */
    <T> T executeWithRetry(final String operation, final HttpCall<T> call) throws IOException {
        final int maxAttempts = Math.max(1, getRetryMaxAttempts());
        final long baseDelay = Math.max(0L, getRetryBaseDelayMs());
        IOException lastIo = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return call.call();
            } catch (final RetryableHttpException e) {
                if (attempt == maxAttempts) {
                    logger.warn("[Embedding:OPENSEARCH] {} retry exhausted. attempts={}, lastStatus={}", operation, attempt, e.statusCode);
                    throw new IOException("OpenSearch ML predict API retryable error: " + e.statusCode + " " + e.reason, e);
                }
                sleepBackoff(operation, attempt, maxAttempts, baseDelay, "status", e.statusCode);
            } catch (final IOException e) {
                if (attempt == maxAttempts) {
                    lastIo = e;
                    break;
                }
                sleepBackoff(operation, attempt, maxAttempts, baseDelay, "exception", e.getClass().getSimpleName());
            }
        }
        if (lastIo == null) {
            throw new IllegalStateException("executeWithRetry exited without exception or success");
        }
        throw lastIo;
    }

    /**
     * Sleeps an exponential-backoff interval with +/-20% jitter and a hard cap.
     *
     * @param operation log label
     * @param attempt 1-based current attempt index
     * @param maxAttempts total attempts including the first
     * @param baseDelay base delay in milliseconds (already clamped to >=0)
     * @param logFieldKey log field name carrying the cause ("status" or "exception")
     * @param logFieldValue log field value for the cause
     * @throws IOException if the sleep is interrupted
     */
    private void sleepBackoff(final String operation, final int attempt, final int maxAttempts, final long baseDelay,
            final String logFieldKey, final Object logFieldValue) throws IOException {
        final long jitter = (long) (baseDelay * 0.2 * ThreadLocalRandom.current().nextDouble(-1.0, 1.0));
        final long delay = Math.min(MAX_BACKOFF_MS, (long) (baseDelay * Math.pow(2, attempt - 1)) + jitter);
        final long sleepMs = Math.max(0, delay);
        logger.info("[Embedding:OPENSEARCH] {} retrying. attempt={}/{}, {}={}, sleepMs={}", operation, attempt, maxAttempts, logFieldKey,
                logFieldValue, sleepMs);
        try {
            Thread.sleep(sleepMs);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IOException("Retry interrupted", ie);
        }
    }
}
