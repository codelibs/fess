package org.codelibs.fess.es.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.lucene.search.Query;
import org.elasticsearch.Version;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.io.stream.NamedWriteable;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;

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
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeOptionalString(modelName);
        out.writeOptionalString(featureSetName);
        out.writeMap(params);
        if (out.getVersion().onOrAfter(Version.V_6_2_4)) {
            out.writeOptionalStringArray(activeFeatures != null ? activeFeatures.toArray(new String[0]) : null);
        }
        out.writeOptionalString(storeName);
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
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
    protected Query doToQuery(QueryShardContext context) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean doEquals(StoredLtrQueryBuilder other) {
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

    public StoredLtrQueryBuilder modelName(String modelName) {
        this.modelName = Objects.requireNonNull(modelName);
        return this;
    }

    public String featureSetName() {
        return featureSetName;
    }

    public StoredLtrQueryBuilder featureSetName(String featureSetName) {
        this.featureSetName = featureSetName;
        return this;
    }

    public String storeName() {
        return storeName;
    }

    public StoredLtrQueryBuilder storeName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public Map<String, Object> params() {
        return params;
    }

    public StoredLtrQueryBuilder params(Map<String, Object> params) {
        this.params = Objects.requireNonNull(params);
        return this;
    }

    public List<String> activeFeatures() {
        return activeFeatures;
    }

    public StoredLtrQueryBuilder activeFeatures(List<String> activeFeatures) {
        this.activeFeatures = Objects.requireNonNull(activeFeatures);
        return this;
    }
}
