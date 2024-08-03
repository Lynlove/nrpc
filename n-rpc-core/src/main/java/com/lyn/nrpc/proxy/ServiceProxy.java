package com.lyn.nrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lyn.nrpc.RpcApplication;
import com.lyn.nrpc.config.RpcConfig;
import com.lyn.nrpc.constant.RpcConstant;
import com.lyn.nrpc.fault.retry.RetryStrategy;
import com.lyn.nrpc.fault.retry.RetryStrategyFactory;
import com.lyn.nrpc.fault.tolerant.TolerantStrategy;
import com.lyn.nrpc.fault.tolerant.TolerantStrategyFactory;
import com.lyn.nrpc.loadbalancer.LoadBalancer;
import com.lyn.nrpc.loadbalancer.LoadBalancerFactory;
import com.lyn.nrpc.model.RpcRequest;
import com.lyn.nrpc.model.RpcResponse;
import com.lyn.nrpc.model.ServiceMetaInfo;
import com.lyn.nrpc.protocol.*;
import com.lyn.nrpc.registry.RegisterFactory;
import com.lyn.nrpc.registry.Registry;
import com.lyn.nrpc.serializer.JdkSerializer;
import com.lyn.nrpc.serializer.Serializer;
import com.lyn.nrpc.serializer.SerializerFactory;
import com.lyn.nrpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
//        System.out.println(serializer);
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegisterFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", method.getName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);


            // 发送HTTP请求
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

            // 发送TCP请求

            // 使用重试机制
            RpcResponse rpcResponse = null;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("调用失败");
        }
    }
}