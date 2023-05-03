package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * reggie_take_out.com.xxx.reggie.mapper
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/1 13:30
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
