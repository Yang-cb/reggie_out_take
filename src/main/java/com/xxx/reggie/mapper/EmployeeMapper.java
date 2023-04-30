package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yang_
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
