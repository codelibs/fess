/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.validation;

import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;

public class UriTypeValidator implements ConstraintValidator<UriType, String> {
    private String[] protocols;

    @Override
    public void initialize(final UriType uriType) {
        protocols = switch (uriType.protocolType()) {
        case WEB -> ComponentUtil.getFessConfig().getCrawlerWebProtocolsAsArray();
        case FILE -> ComponentUtil.getFessConfig().getCrawlerFileProtocolsAsArray();
        default -> throw new ConstraintDefinitionException("protocolType is emtpy.");
        };
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (StringUtil.isNotBlank(value)) {
            return check(protocols, value);
        }
        return true;
    }

    protected static boolean check(final String[] protocols, final String value) {
        final String[] paths = value.split("[\r\n]");
        for (final String path : paths) {
            if (StringUtil.isNotBlank(path) && !path.trim().startsWith("#")) {
                boolean flag = false;
                for (final String protocol : protocols) {
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

    public enum ProtocolType {
        WEB, FILE;
    }
}
