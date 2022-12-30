package com.example.consumer.service;

import org.springframework.stereotype.Service;

/**
 * @ClassName TestServiceImpl
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/12/29 10:36
 */
@Service
public class TestServiceImpl implements TestService{

	@Override
	public String getRes() {
		return "测试本地服务";
	}
}
