package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.mapper.AddressBookMapper;
import com.xxx.reggie.pojo.AddressBook;
import com.xxx.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author yang_
 * @description 针对表【address_book(地址管理)】的数据库操作Service实现
 * @createDate 2023-05-02 16:23:35
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {

    /**
     * 将当前地址更改为默认地址
     *
     * @param address json封装了需要更改的地址
     * @return msg
     */
    @Override
    public boolean isDefault(AddressBook address) {
        if (address == null) {
            return false;
        }
        Long id = address.getId();
        //1，查询当前地址信息
        AddressBook addressBook = this.getById(id);

        //2，查询数据库，看是否已经有默认地址
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook isDefault = this.getOne(qw);
        if (isDefault != null) {
            //有默认地址：先将原有的默认地址取消
            isDefault.setIsDefault(0);
            this.updateById(isDefault);
        }
        //设置当前地址为默认地址
        addressBook.setIsDefault(1);
        this.updateById(addressBook);

        return true;
    }
}




