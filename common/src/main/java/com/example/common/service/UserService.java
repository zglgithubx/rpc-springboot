package com.example.common.service;

import com.example.common.entity.User;


/**
 * @ClassName UserService
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/16 09:22
 */
public interface UserService {
	public User findUserById(Integer id);
}
