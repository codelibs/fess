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

package jp.sf.fess.db.exentity;

import java.text.SimpleDateFormat;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsFailureUrl;
import jp.sf.fess.util.ComponentUtil;

import org.codelibs.core.util.StringUtil;

/**
 * The entity of FAILURE_URL.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class FailureUrl extends BsFailureUrl {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public String getLastAccessTimeForList() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.DEFAULT_DATETIME_FORMAT);
        if (getLastAccessTime() != null) {
            return sdf.format(getLastAccessTime());
        }
        return null;
    }

    public String getWebConfigName() {
        final CrawlingConfig crawlingConfig = ComponentUtil
                .getCrawlingConfigHelper().getCrawlingConfig(getConfigId());
        if (crawlingConfig != null) {
            return crawlingConfig.getName();
        }
        return StringUtil.EMPTY;
    }

    public String getFileConfigName() {
        final CrawlingConfig crawlingConfig = ComponentUtil
                .getCrawlingConfigHelper().getCrawlingConfig(getConfigId());
        if (crawlingConfig != null) {
            return crawlingConfig.getName();
        }
        return StringUtil.EMPTY;
    }

    public String getDataConfigName() {
        final CrawlingConfig crawlingConfig = ComponentUtil
                .getCrawlingConfigHelper().getCrawlingConfig(getConfigId());
        if (crawlingConfig != null) {
            return crawlingConfig.getName();
        }
        return StringUtil.EMPTY;
    }
}
