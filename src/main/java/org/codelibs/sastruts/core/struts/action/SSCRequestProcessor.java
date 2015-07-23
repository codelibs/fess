/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.codelibs.sastruts.core.struts.action;

import javax.servlet.ServletException;

import org.lastaflute.web.ruts.config.ModuleConfig;
import org.lastaflute.web.ruts.process.ActionFormMapper.IndexParsedResult;

/**
 * @author shinsuke
 *
 */
public class SSCRequestProcessor extends S2RequestProcessor {
    protected static final char NESTED_DELIM = '.';

    protected static final char INDEXED_DELIM = '[';

    protected static final char INDEXED_DELIM2 = ']';

    protected int maxReqParamSize = 100;

    @Override
    public void init(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
        super.init(servlet, moduleConfig);
        final Object value = servlet.getServletContext().getAttribute("sastruts.MAX_REQUEST_PARAMETER_SIZE");
        if (value != null) {
            maxReqParamSize = Integer.parseInt(value.toString());
        }
    }

    @Override
    protected IndexParsedResult parseIndex(String name) {
        final IndexParsedResult result = new SSCIndexParsedResult();
        while (true) {
            final int index = name.indexOf(INDEXED_DELIM2);
            if (index < 0) {
                throw new IllegalArgumentException(INDEXED_DELIM2 + " is not found in " + name);
            }
            final int size = Integer.valueOf(name.substring(0, index)).intValue();
            if (size > maxReqParamSize) {
                throw new IllegalArgumentException("The array size exceeds " + maxReqParamSize);
            }
            result.indexes = ArrayUtil.add(result.indexes, size);
            name = name.substring(index + 1);
            if (name.length() == 0) {
                break;
            } else if (name.charAt(0) == INDEXED_DELIM) {
                name = name.substring(1);
            } else if (name.charAt(0) == NESTED_DELIM) {
                name = name.substring(1);
                break;
            } else {
                throw new IllegalArgumentException(name);
            }
        }
        result.name = name;
        return result;
    }

    /**
     *
     */
    protected static class SSCIndexParsedResult extends IndexParsedResult {
    }

}
