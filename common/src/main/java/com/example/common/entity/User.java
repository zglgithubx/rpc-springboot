package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName User
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/09/16 09:21
 */
@Data
@AllArgsConstructor
public class User implements Serializable {
	private Integer id;
	private String name;
}
