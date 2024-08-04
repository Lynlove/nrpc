package com.lyn.nrpc.bootstrap;

import com.lyn.nrpc.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 */
public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init(){
        // Rpc 框架初始化(配置和注册中心)
        RpcApplication.init();
    }
}
