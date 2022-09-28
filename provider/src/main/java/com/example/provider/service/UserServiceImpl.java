package com.example.provider.service;
import com.example.common.entity.User;
import com.example.common.service.UserService;
import com.example.provider.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/16 09:25
 */
@RpcService
public class UserServiceImpl implements UserService {
	@Override
	public User findUserById(Integer id) {
		return new User(id,"张三");
	}
}

