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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Locale;

import org.dbflute.jdbc.ClassificationMeta;
import org.dbflute.optional.OptionalObject;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.dbflute.exception.ProvidedClassificationNotFoundException;

/**
 * @author jflute
 */
public class FessListedClassificationProvider implements ListedClassificationProvider {

    @Override
    public ClassificationMeta provide(final String classificationName) throws ProvidedClassificationNotFoundException {
        final ClassificationMeta onMainSchema = findOnMainSchema(classificationName);
        if (onMainSchema == null) {
            final String msg = "Not found the classification: " + classificationName;
            throw new ProvidedClassificationNotFoundException(msg);
        }
        return onMainSchema;
    }

    protected ClassificationMeta findOnMainSchema(final String classificationName) throws ProvidedClassificationNotFoundException {
        // *no use DBFlute classification
        return null;
        //String searchName = classificationName;
        //if (classificationName.contains(".")) {
        //    final String dbName = Srl.substringFirstFront(classificationName, ".");
        //    if (dbName.equals(DBCurrent.getInstance().projectName())) {
        //        searchName = Srl.substringFirstRear(classificationName, ".");
        //    } else {
        //        return null;
        //    }
        //}
        //try {
        //    return CDef.DefMeta.valueOf(searchName);
        //} catch (IllegalArgumentException ignored) { // not found
        //    return null; // handled later
        //}
    }

    @Override
    public OptionalThing<String> determineAlias(final Locale locale) {
        return OptionalObject.empty();
    }
}
