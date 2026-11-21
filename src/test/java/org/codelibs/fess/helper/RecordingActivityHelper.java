/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.mylasta.action.FessUserBean;
import org.dbflute.optional.OptionalThing;

/**
 * An {@link ActivityHelper} that remembers the sessions that were ended instead of writing the audit log.
 */
public class RecordingActivityHelper extends ActivityHelper {

    /** One entry, {@code user:reason}, for each session that was ended. */
    public final List<String> sessionEnds = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void sessionEnd(final OptionalThing<FessUserBean> user, final String reason) {
        sessionEnds.add(user.map(FessUserBean::getUserId).orElse("-") + ":" + reason);
    }
}
