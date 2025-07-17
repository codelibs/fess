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
package org.codelibs.fess.opensearch.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.lucene.search.Query;
import org.opensearch.core.ParseField;
import org.opensearch.core.common.io.stream.NamedWriteable;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.ObjectParser;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.index.query.AbstractQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryShardContext;

/**
 * A query builder for a stored LTR (Learning to Rank) query.
 * This builder constructs a query that uses a pre-trained LTR model
 * to re-rank search results based on a given set of features.
 */
public class StoredLtrQueryBuilder extends AbstractQueryBuilder<StoredLtrQueryBuilder> implements NamedWriteable {
    /** The name of the query. */
    public static final String NAME = "sltr";

    /** The parse field for the model name. */
    public static final ParseField MODEL_NAME = new ParseField("model");
    /** The parse field for the featureset name. */
    public static final ParseField FEATURESET_NAME = new ParseField("featureset");
    /** The parse field for the store name. */
    public static final ParseField STORE_NAME = new ParseField("store");
    /** The parse field for the query parameters. */
    public static final ParseField PARAMS = new ParseField("params");
    /** The parse field for the active features. */
    public static final ParseField ACTIVE_FEATURES = new ParseField("active_features");
    private static final ObjectParser<StoredLtrQueryBuilder, Void> PARSER;

    static {
        PARSER = new ObjectParser<>(NAME);
        PARSER.declareString(StoredLtrQueryBuilder::modelName, MODEL_NAME);
        PARSER.declareString(StoredLtrQueryBuilder::featureSetName, FEATURESET_NAME);
        PARSER.declareString(StoredLtrQueryBuilder::storeName, STORE_NAME);
        PARSER.declareField(StoredLtrQueryBuilder::params, XContentParser::map, PARAMS, ObjectParser.ValueType.OBJECT);
        PARSER.declareStringArray(StoredLtrQueryBuilder::activeFeatures, ACTIVE_FEATURES);
        PARSER.declareFloat(QueryBuilder::boost, AbstractQueryBuilder.BOOST_FIELD);
        PARSER.declareString(QueryBuilder::queryName, AbstractQueryBuilder.NAME_FIELD);
    }

    private String modelName;
    private String featureSetName;
    private String storeName;
    private Map<String, Object> params;
    private List<String> activeFeatures;

    /**
     * Constructs a new stored LTR query builder.
     */
    public StoredLtrQueryBuilder() {
        // do nothing
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    protected void doWriteTo(final StreamOutput out) throws IOException {
        out.writeOptionalString(modelName);
        out.writeOptionalString(featureSetName);
        out.writeMap(params);
        out.writeOptionalStringArray(activeFeatures != null ? activeFeatures.toArray(new String[0]) : null);
        out.writeOptionalString(storeName);
    }

    @Override
    protected void doXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject(NAME);
        if (modelName != null) {
            builder.field(MODEL_NAME.getPreferredName(), modelName);
        }
        if (featureSetName != null) {
            builder.field(FEATURESET_NAME.getPreferredName(), featureSetName);
        }
        if (storeName != null) {
            builder.field(STORE_NAME.getPreferredName(), storeName);
        }
        if (this.params != null && !this.params.isEmpty()) {
            builder.field(PARAMS.getPreferredName(), this.params);
        }
        if (activeFeatures != null && !activeFeatures.isEmpty()) {
            builder.field(ACTIVE_FEATURES.getPreferredName(), activeFeatures);
        }
        printBoostAndQueryName(builder);
        builder.endObject();
    }

    @Override
    protected Query doToQuery(final QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("Query processing is not supported.");
    }

    @Override
    protected boolean doEquals(final StoredLtrQueryBuilder other) {
        return Objects.equals(modelName, other.modelName) && Objects.equals(featureSetName, other.featureSetName)
                && Objects.equals(storeName, other.storeName) && Objects.equals(params, other.params)
                && Objects.equals(activeFeatures, other.activeFeatures);
    }

    @Override
    protected int doHashCode() {
        return Objects.hash(modelName, featureSetName, storeName, params, activeFeatures);
    }

    /**
     * Gets the name of the LTR model.
     *
     * @return The model name.
     */
    public String modelName() {
        return modelName;
    }

    /**
     * Sets the name of the LTR model.
     *
     * @param modelName The model name.
     * @return This query builder.
     */
    public StoredLtrQueryBuilder modelName(final String modelName) {
        this.modelName = Objects.requireNonNull(modelName);
        return this;
    }

    /**
     * Gets the name of the featureset.
     *
     * @return The featureset name.
     */
    public String featureSetName() {
        return featureSetName;
    }

    /**
     * Sets the name of the featureset.
     *
     * @param featureSetName The featureset name.
     * @return This query builder.
     */
    public StoredLtrQueryBuilder featureSetName(final String featureSetName) {
        this.featureSetName = featureSetName;
        return this;
    }

    /**
     * Gets the name of the feature store.
     *
     * @return The store name.
     */
    public String storeName() {
        return storeName;
    }

    /**
     * Sets the name of the feature store.
     *
     * @param storeName The store name.
     * @return This query builder.
     */
    public StoredLtrQueryBuilder storeName(final String storeName) {
        this.storeName = storeName;
        return this;
    }

    /**
     * Gets the parameters for the LTR query.
     *
     * @return A map of query parameters.
     */
    public Map<String, Object> params() {
        return params;
    }

    /**
     * Sets the parameters for the LTR query.
     *
     * @param params A map of query parameters.
     * @return This query builder.
     */
    public StoredLtrQueryBuilder params(final Map<String, Object> params) {
        this.params = Objects.requireNonNull(params);
        return this;
    }

    /**
     * Gets the list of active features.
     *
     * @return A list of active features.
     */
    public List<String> activeFeatures() {
        return activeFeatures;
    }

    /**
     * Sets the list of active features.
     *
     * @param activeFeatures A list of active features.
     * @return This query builder.
     */
    public StoredLtrQueryBuilder activeFeatures(final List<String> activeFeatures) {
        this.activeFeatures = Objects.requireNonNull(activeFeatures);
        return this;
    }
}