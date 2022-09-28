package com.example.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcRequest
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/27 09:22
 */
@Data
public class RpcRequest implements Serializable {
	/**
	 * 请求ID 用来标识本次请求以匹配RPC服务器的响应
	 */
	private String requestId;
	/**
	 * 调用的类(接口)权限定名称
	 */
	private String className;
	/**
	 * 调用的方法名
	 */
	private String methodName;
	/**
	 * 方法参类型列表
	 */
	private Class<?>[] parameterTypes;
	/**
	 * 方法参数
	 */
	private Object[] parameters;
}
