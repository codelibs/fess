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

package org.codelibs.fess.validator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.FessSystemException;
import org.seasar.struts.validator.S2FieldChecks;

public class UriTypeChecks extends S2FieldChecks {

    private static final long serialVersionUID = 1L;

    public static boolean validateUriType(final Object bean,
            final ValidatorAction validatorAction, final Field field,
            final ActionMessages errors, final Validator validator,
            final HttpServletRequest request) {
        final String value = getValueAsString(bean, field);
        if (StringUtil.isNotBlank(value)) {
            final String protocols = field.getVarValue("protocols");
            if (StringUtil.isEmpty(protocols)) {
                throw new FessSystemException("protocols is empty.");
            }
            if (!check(protocols, value)) {
                addError(errors, field, validator, validatorAction, request);
                return false;
            }
        }
        return true;
    }

    protected static boolean check(final String protocols, final String values) {
        final String[] prtcls = protocols.split(",");
        final String[] paths = values.split("[\r\n]");
        for (final String path : paths) {
            if (StringUtil.isNotBlank(path) && !path.trim().startsWith("#")) {
                boolean flag = false;
                for (final String protocol : prtcls) {
                    if (path.trim().startsWith(protocol.trim())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
        }
        return true;
    }
}
