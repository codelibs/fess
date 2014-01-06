package jp.sf.fess.helper;

import javax.annotation.Resource;

import jp.sf.fess.FessSystemException;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.policy.QueryType;

public class DocumentHelper {

    @Resource
    protected SystemHelper systemHelper;

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
        doc.setField(systemHelper.idField, "none");
        doc.setField(systemHelper.urlField, "none");
        doc.setField(systemHelper.docIdField, docId);
        doc.setField(fieldName, num);

        final UpdateRequest req = new UpdateRequest();
        req.add(doc);
        req.setParam("excmd", "update");
        req.setParam("term", systemHelper.docIdField);
        solrGroup.request(req);
        solrGroup.commit(false, false, true);
    }

}
