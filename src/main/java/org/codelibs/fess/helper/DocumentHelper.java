/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.helper;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.codelibs.fess.FessSystemException;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.policy.QueryType;

public class DocumentHelper {

    @Resource
    protected FieldHelper fieldHelper;

    @Resource
    protected SolrGroupManager solrGroupManager;

    public void update(final String docId, final String fieldName,
            final long num) {

        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.ADD);
        if (!solrGroup.isActive(QueryType.ADD)) {
            throw new FessSystemException("SolrGroup "
                    + solrGroup.getGroupName() + " is not available.");
        }

        final SolrInputDocument doc = new SolrInputDocument();
        doc.setField(fieldHelper.idField, "none");
        doc.setField(fieldHelper.urlField, "none");
        doc.setField(fieldHelper.docIdField, docId);
        doc.setField(fieldName, num);

        final UpdateRequest req = new UpdateRequest();
        req.add(doc);
        req.setParam("excmd", "update");
        req.setParam("term", fieldHelper.docIdField);
        solrGroup.request(req);
        solrGroup.commit(false, false, true);
    }

}
