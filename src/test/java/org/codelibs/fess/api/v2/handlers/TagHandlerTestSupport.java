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
package org.codelibs.fess.api.v2.handlers;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

/**
 * In-memory tag store, tag helper, configuration and servlet stubs shared by {@link TagsHandlerTest} and
 * {@link DocumentTagsHandlerTest}.
 *
 * <p>{@link FakeTagTypeService} keeps the tag types in memory with the same contract as the real service: listings
 * leave out the paths, an insert of an existing id and an update with a stale sequence number throw
 * {@link TagTypeConflictException}. {@link FakeTagTypeHelper} is the real helper (value encoding, name rules,
 * permissions, visibility) reading that store, with the change queue recorded.</p>
 */
final class TagHandlerTestSupport {

    private TagHandlerTestSupport() {
        // utility
    }

    /** The test environment: configuration, store, helper and caller. */
    static class Env {
        final TagFessConfig fessConfig = new TagFessConfig();
        final FakeTagTypeService service = new FakeTagTypeService();
        final FakeTagTypeHelper helper = new FakeTagTypeHelper(this);
        /** The roles of the caller besides its own user role and the sharing role. */
        final Set<String> roles = new LinkedHashSet<>();
        /** The waits a handler took before retrying an update, in milliseconds; the handlers record them instead of sleeping. */
        final List<Long> pauses = new ArrayList<>();
        String virtualHostKey = "";
        OptionalThing<FessUserBean> user = OptionalThing.empty();

        Env() {
            ComponentUtil.setFessConfig(fessConfig);
            ComponentUtil.register(new SystemHelper(), "systemHelper");
            ComponentUtil.register(new VirtualHostHelper() {
                @Override
                public String getVirtualHostKey() {
                    return virtualHostKey;
                }
            }, "virtualHostHelper");
            ComponentUtil.register(helper, "tagTypeHelper");
        }

        Env user(final String name) {
            user = name == null ? OptionalThing.empty() : OptionalThing.of(new FessUserBean(new StubFessUser(name)));
            return this;
        }

        /** Stores a tag and returns it. */
        TagType put(final String name, final String owner, final boolean shared, final String... paths) {
            final TagType tagType = new TagType();
            tagType.setName(name);
            tagType.setOwner(owner);
            tagType.setId(helper.toId(tagType.getTagValue()));
            tagType.setPaths(paths);
            tagType.setPermissions(shared ? new String[] { "1" + owner, "Rguest" } : new String[] { "1" + owner });
            tagType.setVirtualHost("");
            tagType.setSortOrder(0);
            service.store.put(tagType.getId(), copy(tagType, true));
            service.seqNoMap.put(tagType.getId(), 0L);
            return tagType;
        }

        TagType stored(final String name, final String owner) {
            return service.store.get(helper.toId(TagType.toTagValue(name, owner)));
        }
    }

    static TagType copy(final TagType src, final boolean withPaths) {
        final TagType dest = new TagType();
        dest.setId(src.getId());
        dest.setName(src.getName());
        dest.setOwner(src.getOwner());
        if (withPaths && src.getPaths() != null) {
            dest.setPaths(src.getPaths().clone());
        }
        dest.setPermissions(src.getPermissions() == null ? null : src.getPermissions().clone());
        dest.setVirtualHost(src.getVirtualHost());
        dest.setSortOrder(src.getSortOrder());
        dest.setCreatedBy(src.getCreatedBy());
        dest.setCreatedTime(src.getCreatedTime());
        dest.setUpdatedBy(src.getUpdatedBy());
        dest.setUpdatedTime(src.getUpdatedTime());
        return dest;
    }

    /** Keeps the tag types in memory with the contract of {@link TagTypeService}. */
    static class FakeTagTypeService extends TagTypeService {
        final Map<String, TagType> store = new LinkedHashMap<>();
        final Map<String, Long> seqNoMap = new HashMap<>();
        final List<String> calls = new ArrayList<>();
        /** The number of the next reads of a tag that do not find it, as a read of an index that is not refreshed yet. */
        int hiddenReads;
        /** The number of the next updates that lose a race with another writer. */
        int updateConflicts;
        /** The change the other writer makes to the stored tag when an update loses the race. */
        java.util.function.Consumer<TagType> onUpdateConflict;
        /** The number of the next deletes that lose a race with another writer. */
        int deleteConflicts;
        /** The tags whose deletes lose the race; all when null. */
        java.util.function.Predicate<TagType> deleteConflictOn;
        /** The change the other writer makes to the stored tag when a delete loses the race. */
        java.util.function.Consumer<TagType> onDeleteConflict;
        /** The deletes that fail with an error other than a conflict. */
        java.util.function.Predicate<TagType> deleteFailure;

        @Override
        public OptionalEntity<TagType> getTagType(final String id) {
            final TagType stored = store.get(id);
            if (stored == null) {
                return OptionalEntity.empty();
            }
            if (hiddenReads > 0) {
                hiddenReads--;
                return OptionalEntity.empty();
            }
            final TagType tagType = copy(stored, true);
            tagType.setSeqNo(seqNoMap.get(id));
            tagType.setPrimaryTerm(1L);
            return OptionalEntity.of(tagType);
        }

        @Override
        public List<TagType> getTagTypeListByOwner(final String owner) {
            return store.values()
                    .stream()
                    .filter(t -> owner.equals(t.getOwner()))
                    .sorted(Comparator.comparing(TagType::getSortOrder).thenComparing(TagType::getName))
                    .map(t -> copy(t, false))
                    .toList();
        }

        @Override
        public long countByOwner(final String owner) {
            return store.values().stream().filter(t -> owner.equals(t.getOwner())).count();
        }

        @Override
        public Map<String, Long> getPathCountMapByOwner(final String owner) {
            final Map<String, Long> map = new LinkedHashMap<>();
            store.values()
                    .stream()
                    .filter(t -> owner.equals(t.getOwner()))
                    .forEach(t -> map.put(t.getName(), t.getPaths() == null ? 0L : (long) t.getPaths().length));
            return map;
        }

        @Override
        public void insert(final TagType tagType) {
            calls.add("insert " + tagType.getName() + "/" + tagType.getOwner());
            if (tagType.getId() == null) {
                throw new IllegalArgumentException("no id");
            }
            if (store.containsKey(tagType.getId())) {
                throw new TagTypeConflictException("exists: " + tagType.getId(), null);
            }
            store.put(tagType.getId(), copy(tagType, true));
            seqNoMap.put(tagType.getId(), 0L);
        }

        @Override
        public void update(final TagType tagType) {
            calls.add("update " + tagType.getName() + "/" + tagType.getOwner());
            final String id = tagType.getId();
            if (tagType.getSeqNo() == null || tagType.getPrimaryTerm() == null) {
                throw new IllegalArgumentException("no seqNo");
            }
            if (updateConflicts > 0) {
                updateConflicts--;
                final TagType stored = store.get(id);
                if (stored != null && onUpdateConflict != null) {
                    onUpdateConflict.accept(stored);
                }
                seqNoMap.merge(id, 1L, Long::sum);
                throw new TagTypeConflictException("changed: " + id, null);
            }
            if (!store.containsKey(id) || !tagType.getSeqNo().equals(seqNoMap.get(id))) {
                throw new TagTypeConflictException("changed: " + id, null);
            }
            store.put(id, copy(tagType, true));
            seqNoMap.merge(id, 1L, Long::sum);
        }

        @Override
        public void delete(final TagType tagType) {
            calls.add("delete " + tagType.getName() + "/" + tagType.getOwner());
            final String id = tagType.getId();
            if (tagType.getSeqNo() == null || tagType.getPrimaryTerm() == null) {
                throw new IllegalArgumentException("no seqNo");
            }
            if (deleteFailure != null && deleteFailure.test(tagType)) {
                throw new IllegalStateException("delete failed: " + id);
            }
            if (deleteConflicts > 0 && (deleteConflictOn == null || deleteConflictOn.test(tagType))) {
                deleteConflicts--;
                // another writer changes the tag between the read and the delete
                final TagType stored = store.get(id);
                if (stored != null && onDeleteConflict != null) {
                    onDeleteConflict.accept(stored);
                }
                seqNoMap.merge(id, 1L, Long::sum);
                throw new TagTypeConflictException("changed: " + id, null);
            }
            if (!store.containsKey(id) || !tagType.getSeqNo().equals(seqNoMap.get(id))) {
                throw new TagTypeConflictException("changed: " + id, null);
            }
            store.remove(id);
        }
    }

    /** The real helper reading {@link FakeTagTypeService}, with the change queue recorded. */
    /**
     * A store that answers a count a moment after it counted, as a search engine does under load, so that requests that
     * arrive together all see the count from before the others stored their tags. Safe to use from several threads.
     */
    static class SlowCountTagTypeService extends FakeTagTypeService {
        @Override
        public long countByOwner(final String owner) {
            final long count;
            synchronized (this) {
                count = super.countByOwner(owner);
            }
            try {
                Thread.sleep(20L);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return count;
        }

        @Override
        public synchronized void insert(final TagType tagType) {
            super.insert(tagType);
        }
    }

    /**
     * Runs one request per thread, all started together.
     *
     * @param requests the number of requests
     * @param request the request of the thread of the given number; returns the response
     * @return the responses by thread number
     */
    static Response[] runTogether(final int requests, final java.util.function.IntFunction<Response> request) throws InterruptedException {
        final Response[] responses = new Response[requests];
        final java.util.concurrent.CyclicBarrier start = new java.util.concurrent.CyclicBarrier(requests);
        final List<Throwable> failures = new java.util.concurrent.CopyOnWriteArrayList<>();
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < requests; i++) {
            final int number = i;
            final Thread thread = new Thread(() -> {
                try {
                    start.await();
                    responses[number] = request.apply(number);
                } catch (final Throwable t) {
                    failures.add(t);
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }
        if (!failures.isEmpty()) {
            throw new AssertionError("a request failed: " + failures.get(0), failures.get(0));
        }
        return responses;
    }

    static class FakeTagTypeHelper extends TagTypeHelper {
        private final Env env;
        final List<TagChange> changes = new ArrayList<>();

        FakeTagTypeHelper(final Env env) {
            this.env = env;
        }

        @Override
        public boolean enqueue(final TagChange change) {
            changes.add(change);
            return true;
        }

        @Override
        protected Map<String, TagType> fetchTagTypes(final Collection<String> values) {
            final Map<String, TagType> map = new LinkedHashMap<>();
            for (final String value : values) {
                final TagType stored = env.service.store.get(toId(value));
                if (stored != null) {
                    map.put(value, copy(stored, false));
                }
            }
            return map;
        }

        @Override
        public Map<String, Set<String>> findTagValuesByUrls(final Collection<String> urls) {
            final Map<String, Set<String>> map = new HashMap<>();
            for (final TagType tagType : env.service.store.values()) {
                if (tagType.getPaths() != null) {
                    for (final String path : tagType.getPaths()) {
                        if (urls.contains(path)) {
                            map.computeIfAbsent(path, k -> new LinkedHashSet<>()).add(tagType.getTagValue());
                        }
                    }
                }
            }
            return map;
        }

        @Override
        protected Set<String> getViewerRoles(final SearchRequestType type) {
            final Set<String> roles = new LinkedHashSet<>(env.roles);
            env.user.ifPresent(u -> roles.add("1" + u.getUserId()));
            roles.add("Rguest");
            return roles;
        }

        @Override
        protected List<String> getSharedRoleList() {
            return List.of("Rguest");
        }

        @Override
        protected OptionalThing<FessUserBean> getSavedUserBean() {
            return env.user;
        }
    }

    /** A configuration whose user tag settings are set by the test. */
    static class TagFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        boolean enabled = true;
        int nameMaxLength = 50;
        int maxTags = 1000;
        int maxPaths = 10000;

        @Override
        public boolean isUserTagEnabled() {
            return enabled;
        }

        @Override
        public Integer getUserTagNameMaxLengthAsInteger() {
            return nameMaxLength;
        }

        @Override
        public Integer getUserTagMaxTagsAsInteger() {
            return maxTags;
        }

        @Override
        public Integer getUserTagMaxPathsAsInteger() {
            return maxPaths;
        }

        @Override
        public String getRoleSearchUserPrefix() {
            return "1";
        }

        @Override
        public List<String> getSearchGuestRoleList() {
            return List.of("Rguest", "1guest");
        }

        @Override
        public String getIndexFieldUrl() {
            return "url";
        }

        @Override
        public String getIndexFieldTag() {
            return "tag";
        }
    }

    /** Minimal {@link FessUser} with a name. */
    static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final String name;

        StubFessUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return new String[0];
        }
    }

    /** A request built through a dynamic proxy: method, headers and an optional JSON body. */
    static class StubRequest {
        private final String method;
        private final Map<String, String> headers = new HashMap<>();
        private byte[] body = new byte[0];
        private String contentType;

        StubRequest(final String method) {
            this.method = method;
        }

        StubRequest header(final String name, final String value) {
            headers.put(name, value);
            return this;
        }

        StubRequest json(final String json) {
            contentType = "application/json";
            body = json.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        HttpServletRequest proxy() {
            final ByteArrayInputStream in = new ByteArrayInputStream(body);
            final ServletInputStream stream = new ServletInputStream() {
                @Override
                public int read() {
                    return in.read();
                }

                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener listener) {
                    // not used
                }
            };
            final InvocationHandler h = (proxy, m, args) -> switch (m.getName()) {
            case "getMethod" -> method;
            case "getHeader" -> headers.get(args[0]);
            case "getContentType" -> contentType;
            case "getContentLength" -> body.length;
            case "getContentLengthLong" -> (long) body.length;
            case "getInputStream" -> stream;
            case "getLocale" -> java.util.Locale.ENGLISH;
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            case "toString" -> "StubRequest[" + method + "]";
            default -> defaultValue(m.getReturnType());
            };
            return (HttpServletRequest) Proxy.newProxyInstance(TagHandlerTestSupport.class.getClassLoader(),
                    new Class<?>[] { HttpServletRequest.class }, h);
        }
    }

    /** Captures the status, the headers and the body written through a dynamic proxy. */
    static class Response {
        int status = 200;
        final Map<String, String> headers = new HashMap<>();
        private final StringWriter sw = new StringWriter();
        private final PrintWriter writer = new PrintWriter(sw);

        String body() {
            writer.flush();
            return sw.toString();
        }

        HttpServletResponse proxy() {
            final InvocationHandler h = (proxy, m, args) -> switch (m.getName()) {
            case "setStatus" -> {
                status = (Integer) args[0];
                yield null;
            }
            case "getStatus" -> status;
            case "setHeader", "addHeader" -> {
                headers.put((String) args[0], (String) args[1]);
                yield null;
            }
            case "getHeader" -> headers.get(args[0]);
            case "getWriter" -> writer;
            case "resetBuffer" -> {
                writer.flush();
                sw.getBuffer().setLength(0);
                yield null;
            }
            case "getCharacterEncoding" -> "UTF-8";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            case "toString" -> "Response[" + status + "]";
            default -> defaultValue(m.getReturnType());
            };
            return (HttpServletResponse) Proxy.newProxyInstance(TagHandlerTestSupport.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class }, h);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> envelope() {
            final Map<String, Object> root = JsonMapper.builder().build().readValue(body(), Map.class);
            return (Map<String, Object>) root.get("response");
        }

        Map<String, Object> payload() {
            final Map<String, Object> envelope = envelope();
            if (!Integer.valueOf(0).equals(envelope.get("status"))) {
                throw new AssertionError("not a success: " + body());
            }
            return envelope;
        }

        @SuppressWarnings("unchecked")
        String errorCode() {
            return (String) ((Map<String, Object>) envelope().get("error")).get("code");
        }

        @SuppressWarnings("unchecked")
        String errorMessage() {
            return (String) ((Map<String, Object>) envelope().get("error")).get("message");
        }
    }

    /** The JSON of a tag on a document or in a search hit list. */
    static Map<String, Object> docTag(final TagType tagType, final boolean mine, final boolean shared) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", tagType.getId());
        map.put("value", tagType.getTagValue());
        map.put("name", tagType.getName());
        map.put("owner", tagType.getOwner());
        map.put("mine", mine);
        map.put("shared", shared);
        return map;
    }

    static List<String> list(final String... values) {
        return Arrays.asList(values);
    }

    private static Object defaultValue(final Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        return null;
    }
}
