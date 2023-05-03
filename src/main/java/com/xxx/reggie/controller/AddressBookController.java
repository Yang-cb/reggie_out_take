package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.reggie.common.R;
import com.xxx.reggie.common.ThreadContextId;
import com.xxx.reggie.pojo.AddressBook;
import com.xxx.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/2 16:26
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前用户的所有收货地址
     *
     * @return 地址集合
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (userId == null) {
            return R.error("用户状态异常");
        }
        //2，查表
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId);
        List<AddressBook> addressBookList = addressBookService.list(qw);
        return R.success(addressBookList);
    }

    /**
     * 添加地址
     *
     * @param addressBook json封装了收货人consignee、手机号phone、性别sex、详细地址detail、标签label
     * @return msg
     */
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook) {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (addressBook == null || userId == null) {
            return R.error("保存失败");
        }
        //2，设置用户id
        addressBook.setUserId(userId);
        //3，查询该用户是否有默认收获地址
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook hasDefault = addressBookService.getOne(qw);
        if (hasDefault == null) {
            //没有默认收货地址：设置当前地址为默认收货地址
            addressBook.setIsDefault(1);
        }
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }

    /**
     * 根据地址id查询地址
     *
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        if (id == null) {
            return null;
        }
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 修改地址信息
     *
     * @param addressBook json封装的信息（不含用户id）
     * @return msg
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        if (addressBook == null) {
            return R.error("修改失败");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 将当前地址更改为默认地址
     *
     * @param addressBook json封装了需要更改的地址
     * @return msg
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        boolean flag = addressBookService.isDefault(addressBook);
        if (flag) {
            return R.success("更改默认地址成功");
        } else {
            return R.error("更改默认地址失败");
        }
    }

    /**
     * 查询默认收货地址
     *
     * @return 地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(qw);
        if (one == null) {
            return R.error("未设置默认收货地址！");
        }
        return R.success(one);
    }
}
