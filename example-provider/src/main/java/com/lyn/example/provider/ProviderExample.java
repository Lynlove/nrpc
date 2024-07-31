package com.lyn.example.provider;

import com.lyn.example.common.service.UserService;
import com.lyn.nrpc.RpcApplication;
import com.lyn.nrpc.config.RpcConfig;
import com.lyn.nrpc.registry.LocalRegistry;
import com.lyn.nrpc.server.VertxHttpServer;
import com.lyn.nrpc.utils.ConfigUtils;

/**
 * 简易服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // RPC服务初始化
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        RpcApplication.init(rpc);

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
