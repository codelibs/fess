/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import java.io.Serializable;

import org.mobylet.core.Carrier;
import org.mobylet.core.Mobylet;
import org.mobylet.core.MobyletFactory;
import org.mobylet.core.util.RequestUtils;

public class BrowserTypeHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PC = "pc";

    public static final String DOCOMO = "docomo";

    public static final String AU = "au";

    public static final String SOFTBANK = "softbank";

    public String getBrowserType() {
        if (RequestUtils.get() != null) {
            final Mobylet mobylet = MobyletFactory.getInstance();
            final Carrier carrier = mobylet.getCarrier();
            switch (carrier) {
            case DOCOMO:
                return DOCOMO;
            case AU:
                return AU;
            case SOFTBANK:
                return SOFTBANK;
            default:
                return PC;
            }
        }
        return PC;
    }

    public boolean isMobile() {
        if (PC.equals(getBrowserType())) {
            return false;
        }
        return true;
    }
}
