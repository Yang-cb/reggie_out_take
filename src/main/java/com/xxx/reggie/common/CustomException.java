package com.xxx.reggie.common;

/**
 * reggie_take_out.com.xxx.reggie.common
 *
 * @author yang_
 * @description 自定义业务异常
 * @date 2023/4/19 22:45
 */
public class CustomException extends RuntimeException {
    public CustomException(String msg) {
        super(msg);
    }
}
