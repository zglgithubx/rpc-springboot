package com.example.consumer.proxy;

import com.example.common.entity.RpcRequest;
import com.example.consumer.netty.NettyClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;


@Component
public class RpcFactory<T> implements InvocationHandler {

    @Resource
    private NettyClient nettyClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InterruptedException {
        System.out.println("invoke");
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getSimpleName());
        System.out.println(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(args);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setRequestId(String.valueOf(new Date().getTime()));
        return nettyClient.send(rpcRequest).getResult();
    }
}
