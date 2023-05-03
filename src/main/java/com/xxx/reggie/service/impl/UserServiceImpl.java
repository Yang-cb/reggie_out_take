package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.dto.UserDto;
import com.xxx.reggie.mapper.UserMapper;
import com.xxx.reggie.pojo.User;
import com.xxx.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * reggie_take_out.com.xxx.reggie.service.impl
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/1 13:33
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 登录：如果是新用户会注册账户
     *
     * @param userDto userDto对象，包含手机号和验证码
     * @param request 取出保存的验证码
     * @return 登录成功，将用户信息发送给前端，保存到浏览器
     */
    @Override
    public User login(UserDto userDto, HttpServletRequest request) {
        //1，获取登录手机号
        String phone = userDto.getPhone();
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        //2，获取用户填写的验证码
        String userCode = userDto.getCode();
        if (!StringUtils.hasText(userCode)) {
            throw new CustomException("验证码为空");
        }
        //3，获取正确的验证码
        String rightCode = (String) request.getSession().getAttribute(phone);
        //4，比较验证码
        if (!rightCode.equals(userCode)) {
            throw new CustomException("验证码错误");
        }
        //5，判断该用户是否是新用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = this.getOne(queryWrapper);
        //新用户，自动注册一个账户
        if (user == null) {
            user = new User();
            user.setPhone(phone);   //设置手机号：为当前登录手机号
            user.setStatus(1);      //设置状态：启用
            //保存到数据库
            this.save(user);
        } else {
            //6，判断用户是否被禁用
            if (user.getStatus() == 0) {
                throw new CustomException("该用户已被禁用");
            }
        }
        return user;
    }
}
