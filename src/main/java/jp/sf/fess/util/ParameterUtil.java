/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.util;

import java.util.HashMap;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;

import org.seasar.framework.util.StringUtil;

public class ParameterUtil {
    protected ParameterUtil() {
        // nothing
    }

    public static Map<String, String> parse(final String value) {
        final Map<String, String> paramMap = new HashMap<String, String>();
        if (value != null) {
            final String[] lines = value.split("[\r\n]");
            for (final String line : lines) {
                if (StringUtil.isNotBlank(line)) {
                    final int pos = line.indexOf('=');
                    if (pos == 0) {
                        throw new FessSystemException(
                                "Invalid parameter. The key is null.");
                    } else if (pos > 0) {
                        if (pos < line.length()) {
                            paramMap.put(line.substring(0, pos).trim(), line
                                    .substring(pos + 1).trim());
                        } else {
                            paramMap.put(line.substring(0, pos).trim(),
                                    Constants.EMPTY_STRING);
                        }
                    } else {
                        paramMap.put(line.trim(), Constants.EMPTY_STRING);
                    }
                }
            }
        }
        return paramMap;
    }
}
