package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.pojo.AddressBook;

/**
* @author yang_
* @description 针对表【address_book(地址管理)】的数据库操作Service
* @createDate 2023-05-02 16:23:35
*/
public interface AddressBookService extends IService<AddressBook> {

    /**
     * 将当前地址更改为默认地址
     *
     * @param addressBook json封装了需要更改的地址
     * @return msg
     */
    boolean isDefault(AddressBook addressBook);
}
