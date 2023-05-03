package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.common.ThreadContextId;
import com.xxx.reggie.mapper.OrdersMapper;
import com.xxx.reggie.pojo.*;
import com.xxx.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author yang_
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2023-05-02 23:13:01
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {

    //购物车
    @Autowired
    private ShoppingCartService shoppingCartService;
    //用户
    @Autowired
    private UserService userService;
    //地址
    @Autowired
    private AddressBookService addressBookService;
    //订单明细
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 支付 功能
     *
     * @param orders json封装了备注、支付方式、地址id
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (userId == null) {
            throw new CustomException("用户数据异常");
        }
        //2，查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(qw);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空");
        }
        //3，查询用户信息
        User user = userService.getById(userId);
        //4，查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //5，向 订单明细 中填充空白数据
        //随机订单号
        long orderId = IdWorker.getId();

        // AtomicInteger 原子类型：保证java运算操作的原子性
        AtomicInteger amount = new AtomicInteger(0);

        //遍历购物车数据，填充要保存”订单明细表“的数据，同时计算总金额
        List<OrderDetail> orderDetails = shoppingCartList.stream()
                .map((shoppingCart) -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderId(orderId);
                    orderDetail.setNumber(shoppingCart.getNumber());
                    orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
                    orderDetail.setDishId(shoppingCart.getDishId());
                    orderDetail.setSetmealId(shoppingCart.getSetmealId());
                    orderDetail.setName(shoppingCart.getName());
                    orderDetail.setImage(shoppingCart.getImage());
                    orderDetail.setAmount(shoppingCart.getAmount());

                    //计算总金额
                    //单个金额
                    BigDecimal singleAmount = shoppingCart.getAmount();
                    //数量
                    BigDecimal number = new BigDecimal(shoppingCart.getNumber());
                    //单个购物车的总金额 = 单个金额 * 数量
                    BigDecimal totalAmount = singleAmount.multiply(number);
                    //总金额 = 所有购物车的总金额之和
                    amount.addAndGet(totalAmount.intValue());
                    return orderDetail;
                })
                .collect(Collectors.toList());

        //6，向 订单 中填充空白数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //总金额：第5步 遍历计算得到
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        //拼接地址
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //7，向订单表插入数据（一条数据）
        this.save(orders);
        //8，向订单明细表插入数据（多条数据）
        orderDetailService.saveBatch(orderDetails);
        //9，清空购物车
        shoppingCartService.remove(qw);
    }
}




