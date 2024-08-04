package com.lyn.example.provider;

import com.lyn.example.common.service.UserService;
import com.lyn.nrpc.RpcApplication;
import com.lyn.nrpc.bootstrap.ProviderBootstrap;
import com.lyn.nrpc.config.RegistryConfig;
import com.lyn.nrpc.config.RpcConfig;
import com.lyn.nrpc.model.ServiceMetaInfo;
import com.lyn.nrpc.model.ServiceRegisterInfo;
import com.lyn.nrpc.registry.LocalRegistry;
import com.lyn.nrpc.registry.RegisterFactory;
import com.lyn.nrpc.registry.Registry;
import com.lyn.nrpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 简易服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);


//        // RPC服务初始化
//        RpcApplication.init();
//
//        // 注册服务
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        // 注册服务到注册中心
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegisterFactory.getInstance(registryConfig.getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        try {
//            registry.register(serviceMetaInfo);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        // 启动web服务
////        VertxHttpServer httpServer = new VertxHttpServer();
////        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
//        // 启动TCP服务
//        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
