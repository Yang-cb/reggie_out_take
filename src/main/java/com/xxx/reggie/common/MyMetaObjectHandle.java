package com.xxx.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * reggie_take_out.com.xxx.reggie.common
 *
 * @author yang_
 * @description MetaObjectHandle 设置自动填充字段规则
 * @date 2023/4/19 19:05
 */
@Component
@Slf4j
public class MyMetaObjectHandle implements MetaObjectHandler {
    @Autowired
    private HttpServletRequest request;

    /**
     * 插入时自动填充字段规则
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("metaObject => {}", metaObject.toString());
        // 设置创建时间，更新时间，创建者，更新者
        log.info(LocalDateTime.now().toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        // ThreadLocal 并不是一种 Thread ，而是Thread的一个局部变量。
        // ThreadLocal : 为每个线程提供一份单独的存储空间，具有线程隔离效果，各个线程空间互不影响。
        Long empId = ThreadContextId.getContextId();

        metaObject.setValue("createUser", empId);
        metaObject.setValue("updateUser", empId);
    }

    /**
     * 更新时自动填充字段规则
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
//        log.info("metaObject => {}", metaObject.toString());

        // 设置创建时间，创建者
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long id = ThreadContextId.getContextId();
//        log.info("设置后id ==> {}", id);

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        log.info("注入HttpServletRequest获取id => {}", id);

        metaObject.setValue("updateUser", id);
    }
}
