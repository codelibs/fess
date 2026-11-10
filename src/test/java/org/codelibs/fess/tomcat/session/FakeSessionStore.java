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
package org.codelibs.fess.tomcat.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A JDK-only stand-in for the web application's session store bridge, shared by the managers of several test nodes.
 */
class FakeSessionStore {

    final Map<String, Map<String, Object>> docs = new ConcurrentHashMap<>();
    final List<String> calls = new java.util.concurrent.CopyOnWriteArrayList<>();
    volatile boolean down;
    volatile long touchIntervalMs = 60_000L;
    volatile int fullWrites;
    volatile int touches;
    volatile int purges;

    Map<String, Function<Map<String, Object>, Object>> bridge() {
        final Map<String, Function<Map<String, Object>, Object>> ops = new HashMap<>();
        ops.put(FessSessionManager.OP_LOAD, this::load);
        ops.put(FessSessionManager.OP_SAVE, this::save);
        ops.put(FessSessionManager.OP_DELETE, this::delete);
        ops.put(FessSessionManager.OP_PURGE, this::purge);
        return ops;
    }

    private void check(final String op) {
        calls.add(op);
        if (down) {
            throw new IllegalStateException("store down");
        }
    }

    @SuppressWarnings("unchecked")
    private Object load(final Map<String, Object> args) {
        check("load");
        final Map<String, Object> doc = docs.get(args.get("id"));
        if (doc == null || (Long) doc.get("expiredTime") < System.currentTimeMillis()) {
            return null;
        }
        final Map<String, Object> result = new HashMap<>(doc);
        result.put("attributes", new HashMap<>((Map<String, Object>) doc.get("attributes")));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object save(final Map<String, Object> args) {
        check("save");
        final String id = (String) args.get("id");
        final Map<String, Object> attributes = new HashMap<>((Map<String, Object>) args.get("attributes"));
        // like the real store: attributes it cannot keep (here: names starting with "skip") are reported, not stored
        final java.util.Set<String> skipped = new java.util.TreeSet<>();
        attributes.keySet().removeIf(n -> n.startsWith("skip") && skipped.add(n));
        final String digest = String.valueOf(new java.util.TreeMap<>(attributes));
        final long now = System.currentTimeMillis();
        final Map<String, Object> doc = docs.get(id);
        final Map<String, Object> result = new HashMap<>();
        result.put("digest", digest);
        result.put("skippedNames", skipped);
        if (digest.equals(args.get("digest")) && doc != null && now - (Long) args.get("persistedAt") < touchIntervalMs) {
            result.put("persistedAt", args.get("persistedAt"));
            return result;
        }
        final Map<String, Object> next = new HashMap<>();
        next.put("creationTime", args.get("creationTime"));
        next.put("lastAccessedTime", args.get("lastAccessedTime"));
        next.put("maxInactiveInterval", args.get("maxInactiveInterval"));
        next.put("digest", digest);
        next.put("attributes", new HashMap<>(attributes));
        next.put("expiredTime", (Long) args.get("lastAccessedTime") + ((Integer) args.get("maxInactiveInterval")) * 1000L);
        docs.put(id, next);
        if (digest.equals(args.get("digest"))) {
            touches++;
        } else {
            fullWrites++;
        }
        result.put("persistedAt", now);
        return result;
    }

    private Object delete(final Map<String, Object> args) {
        check("delete");
        docs.remove(args.get("id"));
        return null;
    }

    private Object purge(final Map<String, Object> args) {
        check("purge");
        purges++;
        final long now = System.currentTimeMillis();
        final int before = docs.size();
        docs.values().removeIf(d -> (Long) d.get("expiredTime") < now);
        return (long) (before - docs.size());
    }

    int count(final String op) {
        return (int) calls.stream().filter(op::equals).count();
    }
}
