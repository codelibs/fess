package org.codelibs.fess.app.service;

import javax.annotation.Resource;

import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.helper.SystemHelper;

public class UserInfoService {

    @Resource
    private UserInfoBhv userInfoBhv;

    @Resource
    private SystemHelper systemHelper;

    public void deleteBefore(int days) {
        userInfoBhv.queryDelete(cb -> {
            cb.query().setUpdatedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

}
