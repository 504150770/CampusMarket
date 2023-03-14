package com.yjq.programmer.service;

import com.yjq.programmer.dto.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-06 15:25
 */
public interface IStoreService {

    // 保存店铺信息
    ResponseDTO<Boolean> saveStore(StoreDTO storeDTO);

    // 根据登录用户获取店铺信息
    ResponseDTO<StoreDTO> getByLoginUser(StoreDTO storeDTO);

    // 根据id获取店铺信息
    ResponseDTO<StoreDTO> getById(StoreDTO storeDTO);

    // 根据状态获取店铺信息
    ResponseDTO<List<StoreDTO>> getByState(StoreDTO storeDTO);

    // 结算页面展示订单信息
    ResponseDTO<List<CartDTO>> generateOrder(OrderDTO orderDTO);

    // 使用沙箱支付宝支付订单
    ResponseDTO<String> aliPayOrder(OrderDTO orderDTO);

    // 修改订单状态
    ResponseDTO<Boolean> updateOrderState(OrderDTO orderDTO);

    // 沙箱支付宝成功支付回调接口
    ResponseDTO<Boolean> aliPayOrderSuccess(OrderDTO orderDTO);

    // 获取所有订单总数
    ResponseDTO<Integer> getAllOrderTotal();

    // 根据用户获取订单数据
    ResponseDTO<List<OrderDTO>> getOrderByUser(OrderDTO orderDTO);

    // 获取某个店铺的订单数据
    ResponseDTO<List<OrderDTO>> getOrderByStore(StoreDTO storeDTO);

    // 修改某个订单详情的状态
    ResponseDTO<Boolean> updateOrderItemState(OrderItemDTO orderItemDTO);

    // 获取今天订单成交金额
    ResponseDTO<BigDecimal> getTodayPrice();

    // 获取本周订单成交金额
    ResponseDTO<BigDecimal> getWeekPrice();

    // 获取本月订单成交金额
    ResponseDTO<BigDecimal> getMonthPrice();

    // 根据时间范围和订单状态获取交易的订单总数
    ResponseDTO<List<Integer>> getOrderCountByDateAndState();

    // 分页获取店铺数据
    ResponseDTO<PageDTO<StoreDTO>> getStoreListByPage(PageDTO<StoreDTO> pageDTO);

    // 审核店铺数据
    ResponseDTO<Boolean> editStoreState(StoreDTO storeDTO);

    // 删除店铺数据
    ResponseDTO<Boolean> removeStore(StoreDTO storeDTO);

    // 分页获取订单数据
    ResponseDTO<PageDTO<OrderDTO>> getOrderListByPage(PageDTO<OrderDTO> pageDTO);

    // 删除订单数据
    ResponseDTO<Boolean> removeOrder(OrderDTO orderDTO);

    // 根据用户id删除店铺数据
    ResponseDTO<Boolean> removeStoreByUserId(StoreDTO storeDTO);
}
