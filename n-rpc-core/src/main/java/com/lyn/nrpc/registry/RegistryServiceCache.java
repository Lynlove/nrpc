package com.lyn.nrpc.registry;

import com.lyn.nrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     * @param key
     * @param newServiceCache
     */
    void writeCache(String key, List<ServiceMetaInfo> newServiceCache){
        this.serviceCache.put(key, newServiceCache);
    }

    /**
     * 读缓存
     * @param key
     * @return
     */
    List<ServiceMetaInfo> readCache(String key){
        return this.serviceCache.get(key);
    }

    /**
     * 清空缓存
     * @param key
     */
    void clearCache(String key){
        this.serviceCache.remove(key);
    }
}
