package com.xxx.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * reggie_take_out.com.xxx.reggie.common
 *
 * @author : yang_
 * @description : 统一的异常处理方法
 * @date : 2023/4/17 21:40
 */

//对使用 RestController Controller 注解的类提供异常处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获 SQLIntegrityConstraintViolationException 异常
     * @param e
     * @return
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> exceptionHandeler(SQLIntegrityConstraintViolationException e) {
        log.info("捕获异常 ：{}", e.getMessage());

        // 注册账户冲突提示： Duplicate entry
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + " 已存在";
            return R.error(msg);
        }

        return R.error("系统繁忙");
    }

    /**
     * 捕获自定义业务异常
     * @param e
     * @return
     */
    @ExceptionHandler({CustomException.class})
    public R<String> exceptionHandle(CustomException e) {
        return R.error(e.getMessage());
    }
}
