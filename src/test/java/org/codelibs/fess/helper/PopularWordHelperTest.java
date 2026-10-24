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

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.cache.CacheBuilder;

public class PopularWordHelperTest extends UnitFessTestCase {

    public PopularWordHelper popularWordHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        popularWordHelper = new PopularWordHelper() {
            @Override
            public void init() {
                fessConfig = new FessConfig.SimpleImpl() {
                    @Override
                    public Integer getSuggestPopularWordCacheSizeAsInteger() {
                        return 1000;
                    }

                    @Override
                    public Integer getSuggestPopularWordCacheExpireAsInteger() {
                        return 10;
                    }

                    @Override
                    public String getSuggestPopularWordSeed() {
                        return "default_seed";
                    }

                    @Override
                    public String[] getSuggestPopularWordTagsAsArray() {
                        return new String[] { "tag1", "tag2" };
                    }

                    @Override
                    public String[] getSuggestPopularWordFieldsAsArray() {
                        return new String[] { "field1", "field2" };
                    }

                    @Override
                    public String[] getSuggestPopularWordExcludesAsArray() {
                        return new String[] { "exclude1", "exclude2" };
                    }

                    @Override
                    public Integer getSuggestPopularWordSizeAsInteger() {
                        return 10;
                    }

                    @Override
                    public Integer getSuggestPopularWordWindowSizeAsInteger() {
                        return 5;
                    }

                    @Override
                    public Integer getSuggestPopularWordQueryFreqAsInteger() {
                        return 1;
                    }
                };
                cache = CacheBuilder.newBuilder()
                        .maximumSize(fessConfig.getSuggestPopularWordCacheSizeAsInteger().longValue())
                        .expireAfterWrite(fessConfig.getSuggestPopularWordCacheExpireAsInteger().longValue(), TimeUnit.SECONDS)
                        .build();
            }
        };
        popularWordHelper.init();
    }

    @Test
    public void test_getCacheKey_allParameters() {
        String seed = "test_seed";
        String[] tags = { "tag2", "tag1" };
        String[] roles = { "role2", "role1" };
        String[] fields = { "field2", "field1" };
        String[] excludes = { "exclude2", "exclude1" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test_seed"));
        assertTrue(result.contains("tag1\u001ftag2"));
        assertTrue(result.contains("role1\u001frole2"));
        assertTrue(result.contains("field1\u001ffield2"));
        assertTrue(result.contains("exclude1\u001fexclude2"));

        // Count separators
        long separatorCount = result.chars().filter(ch -> ch == '\n').count();
        assertEquals(4, separatorCount);
    }

    @Test
    public void test_getCacheKey_nullParameters() {
        String seed = "test_seed";
        String[] tags = null;
        String[] roles = null;
        String[] fields = null;
        String[] excludes = null;

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test_seed"));

        // Should contain 4 separators even with null arrays
        long separatorCount = result.chars().filter(ch -> ch == '\n').count();
        assertEquals(4, separatorCount);
    }

    @Test
    public void test_getCacheKey_emptyArrays() {
        String seed = "test_seed";
        String[] tags = new String[0];
        String[] roles = new String[0];
        String[] fields = new String[0];
        String[] excludes = new String[0];

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test_seed"));

        // Should contain 4 separators
        long separatorCount = result.chars().filter(ch -> ch == '\n').count();
        assertEquals(4, separatorCount);
    }

    @Test
    public void test_getCacheKey_singleItems() {
        String seed = "test_seed";
        String[] tags = { "tag1" };
        String[] roles = { "role1" };
        String[] fields = { "field1" };
        String[] excludes = { "exclude1" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test_seed"));
        assertTrue(result.contains("tag1"));
        assertTrue(result.contains("role1"));
        assertTrue(result.contains("field1"));
        assertTrue(result.contains("exclude1"));
    }

    @Test
    public void test_getCacheKey_sorting() {
        String seed = "test_seed";
        String[] tags = { "zzz", "aaa", "mmm" };
        String[] roles = { "role3", "role1", "role2" };
        String[] fields = { "field3", "field1", "field2" };
        String[] excludes = { "exclude3", "exclude1", "exclude2" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        // Check for sorted concatenated strings
        assertTrue("Result should contain sorted tags", result.contains("aaa\u001fmmm\u001fzzz"));
        assertTrue("Result should contain sorted roles", result.contains("role1\u001frole2\u001frole3"));
        assertTrue("Result should contain sorted fields", result.contains("field1\u001ffield2\u001ffield3"));
        assertTrue("Result should contain sorted excludes", result.contains("exclude1\u001fexclude2\u001fexclude3"));
    }

    @Test
    public void test_getCacheKey_elementsDoNotRunTogether() {
        final String[] none = new String[0];

        // {Ra, Rb} and {RaRb} are different role sets and must not share a cache entry
        assertNotEquals(popularWordHelper.getCacheKey("s", none, new String[] { "Ra", "Rb" }, none, none),
                popularWordHelper.getCacheKey("s", none, new String[] { "RaRb" }, none, none));
        assertNotEquals(popularWordHelper.getCacheKey("s", new String[] { "a", "b" }, none, none, none),
                popularWordHelper.getCacheKey("s", new String[] { "ab" }, none, none, none));
        assertNotEquals(popularWordHelper.getCacheKey("s", none, none, new String[] { "a", "b" }, none),
                popularWordHelper.getCacheKey("s", none, none, new String[] { "ab" }, none));
        assertNotEquals(popularWordHelper.getCacheKey("s", none, none, none, new String[] { "a", "b" }),
                popularWordHelper.getCacheKey("s", none, none, none, new String[] { "ab" }));
    }

    @Test
    public void test_getCacheKey_orderIndependent() {
        final String[] none = new String[0];

        assertEquals(popularWordHelper.getCacheKey("s", none, new String[] { "Rb", "Ra", "Rc" }, none, none),
                popularWordHelper.getCacheKey("s", none, new String[] { "Rc", "Ra", "Rb" }, none, none));
    }

    @Test
    public void test_getCacheKey_consistency() {
        String seed = "test_seed";
        String[] tags = { "tag2", "tag1" };
        String[] roles = { "role2", "role1" };
        String[] fields = { "field2", "field1" };
        String[] excludes = { "exclude2", "exclude1" };

        String result1 = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);
        String result2 = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertEquals(result1, result2);
    }

    @Test
    public void test_getCacheKey_differentOrder() {
        String seed = "test_seed";
        String[] tags1 = { "tag1", "tag2" };
        String[] tags2 = { "tag2", "tag1" };
        String[] roles = { "role1" };
        String[] fields = { "field1" };
        String[] excludes = { "exclude1" };

        String result1 = popularWordHelper.getCacheKey(seed, tags1, roles, fields, excludes);
        String result2 = popularWordHelper.getCacheKey(seed, tags2, roles, fields, excludes);

        assertEquals(result1, result2);
    }

    @Test
    public void test_getCacheKey_nullSeed() {
        String seed = null;
        String[] tags = { "tag1" };
        String[] roles = { "role1" };
        String[] fields = { "field1" };
        String[] excludes = { "exclude1" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.startsWith("null"));
    }

    @Test
    public void test_getCacheKey_specialCharacters() {
        String seed = "test@seed#with$special%chars";
        String[] tags = { "tag@1", "tag#2" };
        String[] roles = { "role$1", "role%2" };
        String[] fields = { "field&1", "field*2" };
        String[] excludes = { "exclude^1", "exclude(2)" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test@seed#with$special%chars"));
    }

    @Test
    public void test_clearCache() {
        // Test that clearCache doesn't throw exception
        popularWordHelper.clearCache();

        // Verify cache is cleared (we can't directly test this without accessing the cache)
        // But we can test that it doesn't throw exceptions
        assertTrue(true);
    }

    @Test
    public void test_init_configuration() {
        PopularWordHelper testHelper = new PopularWordHelper();
        testHelper.init();

        assertNotNull(testHelper.fessConfig);
        assertNotNull(testHelper.cache);
    }

    @Test
    public void test_init_cacheExpireIsInSeconds() throws Exception {
        // suggest.popular.word.cache.expire is documented in seconds
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public Integer getSuggestPopularWordCacheSizeAsInteger() {
                return 10;
            }

            @Override
            public Integer getSuggestPopularWordCacheExpireAsInteger() {
                return 1;
            }
        });
        final PopularWordHelper testHelper = new PopularWordHelper();
        testHelper.init();

        testHelper.cache.put("key", List.of("word"));
        Thread.sleep(1500);

        assertNull(testHelper.cache.getIfPresent("key"));
    }

    @Test
    public void test_CACHE_KEY_SPLITTER() {
        assertEquals('\n', PopularWordHelper.CACHE_KEY_SPLITTER);
    }

    @Test
    public void test_getCacheKey_longStrings() {
        String seed = "very_long_seed_" + "x".repeat(100);
        String[] tags = { "long_tag_" + "y".repeat(50), "another_long_tag_" + "z".repeat(50) };
        String[] roles = { "long_role_" + "a".repeat(50) };
        String[] fields = { "long_field_" + "b".repeat(50) };
        String[] excludes = { "long_exclude_" + "c".repeat(50) };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.length() > 100);
        assertTrue(result.contains(seed));
    }

    @Test
    public void test_getCacheKey_manyItems() {
        String seed = "test_seed";
        String[] tags = new String[20];
        String[] roles = new String[20];
        String[] fields = new String[20];
        String[] excludes = new String[20];

        for (int i = 0; i < 20; i++) {
            tags[i] = "tag" + i;
            roles[i] = "role" + i;
            fields[i] = "field" + i;
            excludes[i] = "exclude" + i;
        }

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("test_seed"));
        assertTrue(result.contains("tag0"));
        assertTrue(result.contains("tag19"));
    }

    @Test
    public void test_getCacheKey_unicodeCharacters() {
        String seed = "テスト_シード";
        String[] tags = { "タグ１", "タグ２" };
        String[] roles = { "ロール１", "ロール２" };
        String[] fields = { "フィールド１", "フィールド２" };
        String[] excludes = { "除外１", "除外２" };

        String result = popularWordHelper.getCacheKey(seed, tags, roles, fields, excludes);

        assertNotNull(result);
        assertTrue(result.contains("テスト_シード"));
        assertTrue(result.contains("タグ１"));
        assertTrue(result.contains("タグ２"));
    }

}