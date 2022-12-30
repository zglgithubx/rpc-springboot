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
        //获取某个类的代理对象
        System.out.println("4.getObject");
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, rpcFactory);
    }

    @Override
    public Class<?> getObjectType() {
        System.out.println("3.getObjectType");
        return interfaceClass;
    }
}
