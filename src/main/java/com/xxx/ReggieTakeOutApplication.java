package com.xxx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author yang_
 */
@Slf4j
@SpringBootApplication
//使 Servlet（控制器）、Filter（过滤器）、Listener（监听器）
// 可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册到Spring容器中
@ServletComponentScan
//开启事务
@EnableTransactionManagement
//开启缓存
@EnableCaching
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class, args);
        log.info("reggie running ...");
    }

}
