package com.example.consumer.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class RpcFactoryBean<T> implements FactoryBean<T> {
    private Class<T> interfaceClass;

    @Autowired
    private RpcFactory<T> rpcFactory;

    public RpcFactoryBean(Class<T> interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject(){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, rpcFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
