package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.reggie.common.R;
import com.xxx.reggie.dto.UserDto;
import com.xxx.reggie.pojo.User;
import com.xxx.reggie.service.UserService;
import com.xxx.reggie.util.SMSUtils;
import com.xxx.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 移动端用户controller，主要用于发送验证码、验证登录
 * @date 2023/5/1 13:34
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码到指定手机号
     *
     * @param user    带有手机号的json
     * @param request 保存验证码
     * @return msg
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        //1，获取手机号
        String phone = user.getPhone();

        //手机号不为空
        if (StringUtils.hasText(phone)) {
            //2，生成4位随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //3，将验证码通过短信发送到手机
            // SMSUtils.sendMessage("瑞吉外卖", "", phone, code);
            log.info("本次登录验证码为 : {}", code);
            //4，将验证码保存，便于后续比较
            request.getSession().setAttribute(phone, code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 登录：如果是新用户会注册账户
     *
     * @param userDto userDto对象，包含手机号和验证码
     * @param request 取出保存的验证码
     * @return 登录成功，将用户信息发送给前端，保存到浏览器
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpServletRequest request) {
        User user = userService.login(userDto, request);
        if (user == null) {
            return R.error("登陆失败");
        }
        //登陆成功，将用户的id放到session
        request.getSession().setAttribute("user", user.getId());
        return R.success(user);
    }

    /**
     * 退出登录
     *
     * @return msg
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        //清除session中保存的数据
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
