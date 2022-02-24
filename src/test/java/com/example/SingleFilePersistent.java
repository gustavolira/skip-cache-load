package com.example;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.jdbc.configuration.JdbcStringBasedStoreConfigurationBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SingleFilePersistent {

    private EmbeddedCacheManager cm;
    private static final String CACHE_NAME = "jdbc";

//    @Before
//    public void setup() {
//        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
//        configurationBuilder.memory().maxCount(2);
//        configurationBuilder.clustering().cacheMode(CacheMode.LOCAL).hash().numOwners(1)
//                .persistence()
//                .passivation(false)
//                .addStore(JdbcStringBasedStoreConfigurationBuilder.class)
//                .segmented(false)
//                .shared(false)
//                .preload(false)
//                .fetchPersistentState(true)
//                .table()
//                .dropOnExit(false)
//                .createOnStart(true)
//                .tableNamePrefix("tbl")
//                .idColumnName("ID_COLUMN").idColumnType("VARCHAR")
//                .dataColumnName("DATA_COLUMN").dataColumnType("BLOB")
//                .timestampColumnName("TIMESTAMP_COLUMN").timestampColumnType("BIGINT")
//                .connectionPool()
//                .driverClass("org.h2.Driver")
//                .connectionUrl("jdbc:h2:mem:test")
//                .username("sa")
//                .password("sa");
//
//        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder().clusteredDefault().transport().defaultCacheName(CACHE_NAME);
//        cm = new DefaultCacheManager(gcb.build(), configurationBuilder.build());
//    }

    @Before
    public void setupPostgres() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.memory().maxCount(2);
        configurationBuilder.clustering().cacheMode(CacheMode.LOCAL).hash().numOwners(1)
                .persistence()
                .passivation(false)
                .addStore(SingleFileStoreConfigurationBuilder.class)
                .location("/tmp")
                .segmented(false)
                .shared(false)
                .preload(true)
                .fetchPersistentState(true);

        GlobalConfigurationBuilder gcb = new GlobalConfigurationBuilder().clusteredDefault().transport().defaultCacheName(CACHE_NAME);
        cm = new DefaultCacheManager(gcb.build(), configurationBuilder.build());
    }

    @After
    public void closeCacheManager() {
        if(cm != null)
            cm.stop();
    }

//    @Test
//    public void testCacheStore() {
//        Cache<Integer, Integer> cache = cm.getCache(CACHE_NAME);
//        //EVICTION = 2
//        cache.put(1, 1);
//        cache.put(2, 2);
//        cache.put(3, 3);
//        //now some key is evicted and stored in store
//        assertEquals(2, cache.getAdvancedCache().withFlags(Flag.SKIP_CACHE_LOAD).size());
//    }

    @Test
    public void testWithoutPreload() {
        Cache<String, String> cache = cm.getCache(CACHE_NAME);
        cache.put("k1", "v1");
        cache.put("k2", "v2");

        cache.stop();
        cache.start();

        assertEquals(0, cache.size());
        assertEquals(null, cache.get("k1"));
    }

}
