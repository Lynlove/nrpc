package com.lyn.nrpc.loadbalancer;

import com.lyn.nrpc.spi.SpiLoader;

/**
 * 负载均衡器工厂
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 根据key获取负载均衡器
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}
