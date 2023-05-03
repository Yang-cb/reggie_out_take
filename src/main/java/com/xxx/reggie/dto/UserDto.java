package com.xxx.reggie.dto;

import com.xxx.reggie.pojo.User;
import lombok.Data;

/**
 * reggie_take_out.com.xxx.reggie.dto
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/1 14:38
 */
@Data
public class UserDto extends User {
    //用户填写的验证码
    private String code;
}
