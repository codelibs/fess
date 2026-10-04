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
package org.codelibs.fess.app.web.admin.tagtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * In-memory tag store, recording tag helper and configuration shared by the admin screen and the
 * admin API tests of tag types.
 *
 * <p>{@link FakeTagTypeService} has the contract of the real service: an insert of an existing id
 * and an update or delete with a stale sequence number throw {@link TagTypeConflictException}.</p>
 */
public final class TagTypeAdminTestSupport {

    private TagTypeAdminTestSupport() {
        // utility
    }

    /** The configuration, store and helper of a test. */
    public static class Env {
        public final TestConfig fessConfig = new TestConfig();
        public final FakeTagTypeService service = new FakeTagTypeService();
        public final RecordingTagTypeHelper helper = new RecordingTagTypeHelper();

        public Env() {
            ComponentUtil.setFessConfig(fessConfig);
            final SystemHelper sysHelper = new SystemHelper();
            ComponentUtil.register(sysHelper, "systemHelper");
            ComponentUtil.register(new PermissionHelper() {
                {
                    systemHelper = sysHelper;
                }
            }, "permissionHelper");
            ComponentUtil.register(helper, "tagTypeHelper");
        }

        /** Stores a tag and returns a copy of it as stored. */
        public TagType put(final String name, final String owner, final String... paths) {
            final TagType tagType = new TagType();
            tagType.setName(name);
            tagType.setOwner(owner);
            tagType.setId(helper.toId(tagType.getTagValue()));
            tagType.setPaths(paths);
            tagType.setPermissions(new String[] { "1" + owner });
            tagType.setVirtualHost("");
            tagType.setSortOrder(0);
            tagType.setCreatedBy("admin");
            tagType.setCreatedTime(1L);
            service.store.put(tagType.getId(), copy(tagType));
            service.seqNoMap.put(tagType.getId(), 0L);
            return service.getTagType(tagType.getId()).get();
        }

        /** Returns the stored tag of a name and an owner, or null. */
        public TagType stored(final String name, final String owner) {
            return service.store.get(helper.toId(TagType.toTagValue(name, owner)));
        }

        /** Returns the tag value of a name and an owner. */
        public String value(final String name, final String owner) {
            return TagType.toTagValue(name, owner);
        }
    }

    static TagType copy(final TagType src) {
        final TagType dest = new TagType();
        dest.setId(src.getId());
        dest.setName(src.getName());
        dest.setOwner(src.getOwner());
        dest.setPaths(src.getPaths() == null ? null : src.getPaths().clone());
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
    public static class FakeTagTypeService extends TagTypeService {
        public final Map<String, TagType> store = new LinkedHashMap<>();
        public final Map<String, Long> seqNoMap = new HashMap<>();
        public final List<String> calls = new ArrayList<>();

        @Override
        public OptionalEntity<TagType> getTagType(final String id) {
            final TagType stored = id == null ? null : store.get(id);
            if (stored == null) {
                return OptionalEntity.empty();
            }
            final TagType tagType = copy(stored);
            tagType.setSeqNo(seqNoMap.get(id));
            tagType.setPrimaryTerm(1L);
            return OptionalEntity.of(tagType);
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
            store.put(tagType.getId(), copy(tagType));
            seqNoMap.put(tagType.getId(), 0L);
        }

        @Override
        public void update(final TagType tagType) {
            calls.add("update " + tagType.getName() + "/" + tagType.getOwner());
            checkSeqNo(tagType);
            store.put(tagType.getId(), copy(tagType));
            seqNoMap.merge(tagType.getId(), 1L, Long::sum);
        }

        @Override
        public void delete(final TagType tagType) {
            calls.add("delete " + tagType.getName() + "/" + tagType.getOwner());
            checkSeqNo(tagType);
            store.remove(tagType.getId());
        }

        private void checkSeqNo(final TagType tagType) {
            if (tagType.getSeqNo() == null || tagType.getPrimaryTerm() == null) {
                throw new IllegalArgumentException("no seqNo");
            }
            if (!store.containsKey(tagType.getId()) || !tagType.getSeqNo().equals(seqNoMap.get(tagType.getId()))) {
                throw new TagTypeConflictException("changed: " + tagType.getId(), null);
            }
        }
    }

    /** The real helper with the change queue recorded. */
    public static class RecordingTagTypeHelper extends TagTypeHelper {
        public final List<TagChange> changes = new ArrayList<>();

        @Override
        public boolean enqueue(final TagChange change) {
            changes.add(change);
            return true;
        }
    }

    /** A configuration whose user tag settings are set by the test. */
    public static class TestConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        public boolean enabled = true;
        public int maxPaths = 10000;

        @Override
        public boolean isUserTagEnabled() {
            return enabled;
        }

        @Override
        public Integer getUserTagNameMaxLengthAsInteger() {
            return 50;
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
        public String getRoleSearchGroupPrefix() {
            return "2";
        }

        @Override
        public String getRoleSearchRolePrefix() {
            return "R";
        }

        @Override
        public String getRoleSearchDeniedPrefix() {
            return "D";
        }

        @Override
        public String get(final String propertyKey) {
            if ("form.admin.max.input.size".equals(propertyKey)) {
                return "10000";
            }
            return super.get(propertyKey);
        }

        @Override
        public String getCanonicalLdapName(final String name) {
            return name;
        }

        @Override
        public Integer getPagingPageRangeSizeAsInteger() {
            return 5;
        }
    }
}
