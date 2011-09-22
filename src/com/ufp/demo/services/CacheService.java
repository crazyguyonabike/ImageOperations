package com.ufp.demo.services;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.terracotta.modules.annotations.AutolockRead;
import org.terracotta.modules.annotations.AutolockWrite;
import org.terracotta.modules.annotations.InstrumentedClass;
import org.terracotta.modules.annotations.Root;
import org.terracotta.cache.CacheConfigFactory;
import org.terracotta.cache.DistributedCache;

import org.apache.log4j.Logger;

public class CacheService {
    private static Logger logger = Logger.getLogger(CacheService.class);

    @Root
    private final DistributedCache<String, String> cache = CacheConfigFactory.newConfig()
	.setMaxTTLSeconds(0)
	.setMaxTTISeconds(0)
	.setLoggingEnabled(false)
	.setOrphanEvictionEnabled(false)
	.newCache();

    @AutolockRead
    public synchronized String get(String key) {
	String value = null;

	if (cache.containsKey(key)) {
	    value = cache.get(key);
	    logger.debug("found value: " + value + " with key: " + key);
	} else
	    logger.debug("no key named: " + key);

	return value;
    }

    @AutolockWrite
    public synchronized void remove(String key) {
	if (cache.containsKey(key)) {
	    logger.debug("removing key: " +  key);
	    cache.remove(key);
	}
    }
    
    @AutolockWrite
    public synchronized void put(String key, String value) {
	logger.debug("adding key: " + key + ", value: " + value);
	cache.put(key, value);
    }

    public void shutdown() {
	logger.debug("shutting down cache");
	cache.shutdown();
    }
}

