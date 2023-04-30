package com.xxx.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * reggie_take_out.com.xxx.reggie.common
 *
 * @author yang_
 * @description 工具类：通过线程设置、获取 session 中的值 ： 如id
 * @date 2023/4/19 19:48
 */
@Component
@Slf4j
public class ThreadContextId {
    // id 为 long
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置id
     *
     * @param id
     */
    public static void setContextId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取id
     *
     * @return
     */
    public static Long getContextId() {
        return threadLocal.get();
    }

    private ThreadContextId() {
    }
}
