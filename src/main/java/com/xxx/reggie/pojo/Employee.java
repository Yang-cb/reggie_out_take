package com.xxx.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工
 *
 * @author yang_
 * @TableName Employee
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;


    // TableField(fill = ???) : 设置公共字段的填充策略

    // 账号创建时间
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;

    // 创建该账号的人的id
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 账号最后更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private LocalDateTime updateTime;

    // 最后更新该账号的人的id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
