package org.codelibs.fess.app.service;

import javax.annotation.Resource;

import org.codelibs.fess.es.log.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.helper.SystemHelper;

public class SearchLogService {

    @Resource
    private SearchLogBhv searchLogBhv;

    @Resource
    private SearchFieldLogBhv searchFieldLogBhv;

    @Resource
    private SystemHelper systemHelper;

    public void deleteBefore(int days) {
        searchLogBhv.selectCursor(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        }, entity -> {
            searchFieldLogBhv.queryDelete(subCb -> {
                subCb.query().setSearchLogId_Equal(entity.getId());
            });
        });
        searchLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

}
