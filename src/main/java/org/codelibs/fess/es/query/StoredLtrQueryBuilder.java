/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.query;

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

public class StoredLtrQueryBuilder extends AbstractQueryBuilder<StoredLtrQueryBuilder> implements NamedWriteable {
    public static final String NAME = "sltr";

    public static final ParseField MODEL_NAME = new ParseField("model");
    public static final ParseField FEATURESET_NAME = new ParseField("featureset");
    public static final ParseField STORE_NAME = new ParseField("store");
    public static final ParseField PARAMS = new ParseField("params");
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
        if (this.activeFeatures != null && !this.activeFeatures.isEmpty()) {
            builder.field(ACTIVE_FEATURES.getPreferredName(), this.activeFeatures);
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

    public String modelName() {
        return modelName;
    }

    public StoredLtrQueryBuilder modelName(final String modelName) {
        this.modelName = Objects.requireNonNull(modelName);
        return this;
    }

    public String featureSetName() {
        return featureSetName;
    }

    public StoredLtrQueryBuilder featureSetName(final String featureSetName) {
        this.featureSetName = featureSetName;
        return this;
    }

    public String storeName() {
        return storeName;
    }

    public StoredLtrQueryBuilder storeName(final String storeName) {
        this.storeName = storeName;
        return this;
    }

    public Map<String, Object> params() {
        return params;
    }

    public StoredLtrQueryBuilder params(final Map<String, Object> params) {
        this.params = Objects.requireNonNull(params);
        return this;
    }

    public List<String> activeFeatures() {
        return activeFeatures;
    }

    public StoredLtrQueryBuilder activeFeatures(final List<String> activeFeatures) {
        this.activeFeatures = Objects.requireNonNull(activeFeatures);
        return this;
    }
}
