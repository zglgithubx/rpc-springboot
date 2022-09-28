package com.example.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcResponse
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/27 09:21
 */
@Data
public class RpcResponse implements Serializable {
	/**
	 * 响应对应的请求ID
	 */
	private String requestId;
	/**
	 * 是否成功的标识
	 */
	private boolean success = true;
	/**
	 * 调用错误信息
	 */
	private String errorMessage;
	/**
	 * 调用结果
	 */
	private Object result;
}
