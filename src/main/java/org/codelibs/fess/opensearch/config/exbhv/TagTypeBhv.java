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
package org.codelibs.fess.opensearch.config.exbhv;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.fesen.opensearch.action.get.GetRequest;
import org.codelibs.fesen.opensearch.action.get.GetResponse;
import org.codelibs.fesen.opensearch.common.xcontent.XContentType;
import org.codelibs.fesen.opensearch.common.xcontent.json.JsonXContent;
import org.codelibs.fesen.opensearch.core.xcontent.DeprecationHandler;
import org.codelibs.fesen.opensearch.core.xcontent.NamedXContentRegistry;
import org.codelibs.fesen.opensearch.core.xcontent.XContentParser;
import org.codelibs.fess.opensearch.config.bsbhv.BsTagTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SearchEngineUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;

/**
 * @author FreeGen
 */
public class TagTypeBhv extends BsTagTypeBhv {
    private String indexName = null;

    /**
     * Selects a tag type by its id with the real-time GET API.
     *
     * <p>{@link #selectByPK(String)} is a search, which sees a write only once the index is
     * refreshed. A request that lost a race for a tag type reads the winner's document at once to
     * apply its change on it, and a search then finds nothing, or the version before the winner's.
     * A GET reads the document from the translog when it is not refreshed yet, with the sequence
     * number and primary term a conditional write needs.</p>
     *
     * @param id The id of the tag type.
     * @return The tag type with all of its fields, or empty if there is none.
     */
    public OptionalEntity<TagType> selectRealtimeByPK(final String id) {
        assertObjectNotNull("id", id);
        final GetResponse response = ComponentUtil.getSearchEngineClient().get(new GetRequest(asEsIndex(), id)).actionGet(searchTimeout);
        if (!response.isExists()) {
            return createOptionalEntity(null, id);
        }
        // The response class does not expose the sequence number and the primary term, which its JSON does.
        final Map<String, Object> doc;
        try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION, SearchEngineUtil.getXContentString(response, XContentType.JSON))) {
            doc = parser.map();
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read the tag type: id=" + id, e);
        }
        @SuppressWarnings("unchecked")
        final TagType tagType = createEntity((Map<String, Object>) doc.get("_source"), TagType.class);
        tagType.setId(id);
        tagType.setVersionNo(DfTypeUtil.toLong(doc.get("_version")));
        tagType.setSeqNo(DfTypeUtil.toLong(doc.get("_seq_no")));
        tagType.setPrimaryTerm(DfTypeUtil.toLong(doc.get("_primary_term")));
        return createOptionalEntity(tagType, id);
    }

    @Override
    protected String asEsIndex() {
        if (indexName == null) {
            final String name = ComponentUtil.getFessConfig().getIndexConfigIndex();
            indexName = super.asEsIndex().replaceFirst(Pattern.quote("fess_config"), name);
        }
        return indexName;
    }
}
