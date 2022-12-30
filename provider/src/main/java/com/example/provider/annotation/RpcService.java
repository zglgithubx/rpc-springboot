package com.example.provider.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Description 将带有此注解的类，纳入Spring容器的管理
 * @Date 2022/12/30 09:10
 * @Param
 * @return
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

}
