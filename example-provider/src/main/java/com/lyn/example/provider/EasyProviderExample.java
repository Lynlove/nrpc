package com.lyn.example.provider;

import com.lyn.example.common.service.UserService;
import com.lyn.nrpc.registry.LocalRegistry;
import com.lyn.nrpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}