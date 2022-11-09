package com.example.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.entity.RpcRequest;
import com.example.common.entity.RpcResponse;
import com.example.common.entity.User;
import com.example.common.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ClassName TestController
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/26 10:46
 */
@RestController
@RequiredArgsConstructor
public class TestController {
	private final UserService userService;

	@GetMapping("/test")
	public String testRpc(){
		long start=System.currentTimeMillis();
		userService.findUserById(123);
		long end=System.currentTimeMillis();
		System.out.println("远程调用耗时："+(end-start));

		return "success";
	}
}
