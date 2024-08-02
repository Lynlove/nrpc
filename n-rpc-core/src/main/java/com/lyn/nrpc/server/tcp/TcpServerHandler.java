package com.lyn.nrpc.server.tcp;

import com.lyn.nrpc.model.RpcRequest;
import com.lyn.nrpc.model.RpcResponse;
import com.lyn.nrpc.protocol.ProtocolMessage;
import com.lyn.nrpc.protocol.ProtocolMessageDecoder;
import com.lyn.nrpc.protocol.ProtocolMessageEncoder;
import com.lyn.nrpc.protocol.ProtocolMessageTypeEnum;
import com.lyn.nrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        // 使用装饰器模式TcpBufferHandlerWrapper解决半包粘包问题
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            System.out.println(protocolMessage);

            // 处理请求
            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                System.out.println(rpcRequest);
                // 获取要调用服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);

//        // 处理连接
//        netSocket.handler(buffer -> {
//           // 接受请求，解码
//            ProtocolMessage<RpcRequest> protocolMessage;
//            try {
//                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
//            } catch (IOException e) {
//                throw new RuntimeException("协议消息解码错误");
//            }
//            RpcRequest rpcRequest = protocolMessage.getBody();
//
//            // 处理请求
//            // 构造响应结果对象
//            RpcResponse rpcResponse = new RpcResponse();
//            try {
//                // 获取要调用服务实现类，通过反射调用
//                Class<?> impClass = LocalRegistry.get(rpcRequest.getServiceName());
//                Method method = impClass.getMethod(rpcRequest.getMethodName());
//                Object result = method.invoke(impClass.newInstance(), rpcRequest.getArgs());
//                // 封装返回结果
//                rpcResponse.setData(result);
//                rpcResponse.setDataType(method.getReturnType());
//                rpcResponse.setMessage("OK");
//            } catch (Exception e){
//                e.printStackTrace();
//                rpcResponse.setMessage(e.getMessage());
//                rpcResponse.setException(e);
//            }
//
//            // 发送响应，编码
//            ProtocolMessage.Header header = protocolMessage.getHeader();
//            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
//            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
//            try {
//                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
//                netSocket.write(encode);
//            } catch (IOException e) {
//                throw new RuntimeException("协议消息编码错误");
//            }
//        });
    }
}
