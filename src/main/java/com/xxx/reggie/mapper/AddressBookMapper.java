package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yang_
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2023-05-02 16:23:35
* @Entity com/xxx/reggie.pojo.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




