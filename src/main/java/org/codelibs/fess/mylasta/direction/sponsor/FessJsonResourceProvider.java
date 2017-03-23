/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Collections;
import java.util.List;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.json.JsonMappingOption;
import org.lastaflute.core.json.JsonResourceProvider;
import org.lastaflute.core.json.bind.JsonYourCollectionResource;
import org.lastaflute.core.json.engine.GsonJsonEngine;
import org.lastaflute.core.json.engine.RealJsonEngine;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.LaYourCollectionTypeAdapterFactory;

public class FessJsonResourceProvider implements JsonResourceProvider {
    public boolean isNullsSuppressed() {
        return true;
    }

    public boolean isPrettyPrintSuppressed() {
        return false;
    }

    public JsonMappingOption provideOption() {
        return null;
    }

    public List<JsonYourCollectionResource> provideYourCollections() {
        return Collections.emptyList();
    }

    public RealJsonEngine swtichJsonEngine() {
        final boolean serializeNulls = !isNullsSuppressed();
        final boolean prettyPrinting = !isPrettyPrintSuppressed();
        final OptionalThing<JsonMappingOption> mappingOption =
                provideOption() != null ? OptionalThing.of(provideOption()) : OptionalThing.empty();
        return new GsonJsonEngine(builder -> {
            setupSerializeNullsSettings(builder, serializeNulls);
            setupPrettyPrintingSettings(builder, prettyPrinting);
            setupYourCollectionSettings(builder);
            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        }, op -> {
            mappingOption.ifPresent(another -> op.acceptAnother(another));
        });
    }

    protected void setupSerializeNullsSettings(GsonBuilder builder, boolean serializeNulls) {
        if (serializeNulls) {
            builder.serializeNulls();
        }
    }

    protected void setupPrettyPrintingSettings(GsonBuilder builder, boolean prettyPrinting) {
        if (prettyPrinting) {
            builder.setPrettyPrinting();
        }
    }

    protected void setupYourCollectionSettings(GsonBuilder builder) {
        for (JsonYourCollectionResource resource : provideYourCollections()) {
            builder.registerTypeAdapterFactory(createYourCollectionTypeAdapterFactory(resource));
        }
    }

    protected LaYourCollectionTypeAdapterFactory createYourCollectionTypeAdapterFactory(JsonYourCollectionResource resource) {
        return new LaYourCollectionTypeAdapterFactory(resource.getYourType(), resource.getYourCollectionCreator());
    }
}
