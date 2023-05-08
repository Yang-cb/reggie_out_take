package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.dto.UserDto;
import com.xxx.reggie.pojo.User;


/**
 * reggie_take_out.com.xxx.reggie.service
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/1 13:33
 */
public interface UserService extends IService<User> {
    /**
     * 登录：如果是新用户会注册账户
     *
     * @param userDto userDto对象，包含手机号和验证码
     * @return 登录成功，将用户信息发送给前端，保存到浏览器
     */
    User login(UserDto userDto);
}
