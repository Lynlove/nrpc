package com.lyn.example.consumer;

import com.lyn.example.common.model.User;
import com.lyn.example.common.service.UserService;
import com.lyn.nrpc.RpcApplication;
import com.lyn.nrpc.config.RpcConfig;
import com.lyn.nrpc.proxy.ServiceProxyFactory;
import com.lyn.nrpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 */
public class ConsumerExample {
    public static void main(String[] args) {
        // RPC服务初始化
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        RpcApplication.init(rpc);

//        System.out.println(rpc);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("user == null");
        }

    }
}
