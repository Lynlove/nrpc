package com.lyn.example.provider;

import com.lyn.example.common.service.UserService;
import com.lyn.nrpc.RpcApplication;
import com.lyn.nrpc.config.RegistryConfig;
import com.lyn.nrpc.config.RpcConfig;
import com.lyn.nrpc.constant.RpcConstant;
import com.lyn.nrpc.model.ServiceMetaInfo;
import com.lyn.nrpc.registry.LocalRegistry;
import com.lyn.nrpc.registry.RegisterFactory;
import com.lyn.nrpc.registry.Registry;
import com.lyn.nrpc.server.VertxHttpServer;
import com.lyn.nrpc.utils.ConfigUtils;

/**
 * 简易服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // RPC服务初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegisterFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动web服务
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
