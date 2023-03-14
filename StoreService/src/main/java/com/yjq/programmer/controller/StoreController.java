package com.yjq.programmer.controller;

import com.yjq.programmer.dto.*;
import com.yjq.programmer.service.IStoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-06 15:44
 */
@RestController
@RequestMapping("/store")
public class StoreController {

    @Resource
    private IStoreService storeService;

    /**
     * 前台保存店铺信息
     * @param storeDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveStore(@RequestBody StoreDTO storeDTO){
        return storeService.saveStore(storeDTO);
    }

    /**
     * 根据登录用户获取店铺信息
     * @param storeDTO
     * @return
     */
    @PostMapping("/login/get")
    public ResponseDTO<StoreDTO> getByLoginUser(@RequestBody StoreDTO storeDTO){
        return storeService.getByLoginUser(storeDTO);
    }

    /**
     * 根据id获取店铺信息
     * @param storeDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<StoreDTO> getById(@RequestBody StoreDTO storeDTO){
        return storeService.getById(storeDTO);
    }

    /**
     * 根据状态获取店铺信息
     * @param storeDTO
     * @return
     */
    @PostMapping("/state/all")
    public ResponseDTO<List<StoreDTO>> getByState(@RequestBody StoreDTO storeDTO){
        return storeService.getByState(storeDTO);
    }

    /**
     * 结算页面订单展示
     * @param orderDTO
     * @return
     */
    @PostMapping("/order/generate")
    public ResponseDTO<List<CartDTO>> generateOrder(@RequestBody OrderDTO orderDTO){
        return storeService.generateOrder(orderDTO);
    }

    /**
     * 修改订单状态
     * @param orderDTO
     * @return
     */
    @PostMapping("/order/update")
    public ResponseDTO<Boolean> updateOrderState(@RequestBody OrderDTO orderDTO){
        return storeService.updateOrderState(orderDTO);
    }

    /**
     * 使用沙箱支付宝支付订单
     * @param orderDTO
     * @return
     */
    @PostMapping("/order/aliPay")
    public ResponseDTO<String> aliPayOrder(@RequestBody OrderDTO orderDTO){
        return storeService.aliPayOrder(orderDTO);
    }

    /**
     * 沙箱支付宝成功支付回调接口
     * @param orderDTO
     * @return
     */
    @PostMapping("/order/aliPay/success")
    public ResponseDTO<Boolean> aliPayOrderSuccess(@RequestBody OrderDTO orderDTO){
        return storeService.aliPayOrderSuccess(orderDTO);
    }

    /**
     * 获取所有订单总数
     * @return
     */
    @PostMapping("/order/total/all")
    public ResponseDTO<Integer> getAllOrderTotal(){
        return storeService.getAllOrderTotal();
    }

    /**
     * 获取某个用户的所有订单
     * @param orderDTO
     * @return
     */
    @PostMapping("/order/user")
    public ResponseDTO<List<OrderDTO>> getOrderByUser(@RequestBody OrderDTO orderDTO){
        return storeService.getOrderByUser(orderDTO);
    }


    /**
     * 修改某个订单详情的状态
     * @param orderItemDTO
     * @return
     */
    @PostMapping("/order/item/update")
    public ResponseDTO<Boolean> updateOrderItemState(@RequestBody OrderItemDTO orderItemDTO){
        return storeService.updateOrderItemState(orderItemDTO);
    }

    /**
     * 获取某个店铺的订单数据
     * @param storeDTO
     * @return
     */
    @PostMapping("/order")
    public ResponseDTO<List<OrderDTO>> getOrderByStore(@RequestBody StoreDTO storeDTO){
        return storeService.getOrderByStore(storeDTO);
    }

    /**
     * 获取今日订单成交金额
     * @return
     */
    @PostMapping("/today-price/all")
    public ResponseDTO<BigDecimal> getTodayPrice(){
        return storeService.getTodayPrice();
    }

    /**
     * 获取本周订单成交金额
     * @return
     */
    @PostMapping("/week-price/all")
    public ResponseDTO<BigDecimal> getWeekPrice(){
        return storeService.getWeekPrice();
    }

    /**
     * 获取本月订单成交金额
     * @return
     */
    @PostMapping("/month-price/all")
    public ResponseDTO<BigDecimal> getMonthPrice(){
        return storeService.getMonthPrice();
    }

    /**
     * 根据时间范围和订单状态获取所有交易的订单总数
     * @return
     */
    @PostMapping("/count-state-date")
    public ResponseDTO<List<Integer>> getOrderCountByDateAndState(){
        return storeService.getOrderCountByDateAndState();
    }

    /**
     * 后台分页获取店铺数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/admin/list")
    public ResponseDTO<PageDTO<StoreDTO>> getStoreListByPage(@RequestBody PageDTO<StoreDTO> pageDTO){
        return storeService.getStoreListByPage(pageDTO);
    }

    /**
     * 后台审核店铺数据
     * @param storeDTO
     * @return
     */
    @PostMapping("/admin/edit-state")
    public ResponseDTO<Boolean> editStoreState(@RequestBody StoreDTO storeDTO){
        return storeService.editStoreState(storeDTO);
    }

    /**
     * 后台删除店铺数据
     * @param storeDTO
     * @return
     */
    @PostMapping("/admin/remove")
    public ResponseDTO<Boolean> removeStore(@RequestBody StoreDTO storeDTO){
        return storeService.removeStore(storeDTO);
    }

    /**
     * 根据用户id删除店铺数据
     * @param storeDTO
     * @return
     */
    @PostMapping("/remove/user_id")
    public ResponseDTO<Boolean> removeStoreByUserId(@RequestBody StoreDTO storeDTO) {
        return storeService.removeStoreByUserId(storeDTO);
    }

    /**
     * 后台分页获取订单数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/admin/order/list")
    public ResponseDTO<PageDTO<OrderDTO>> getOrderListByPage(@RequestBody PageDTO<OrderDTO> pageDTO){
        return storeService.getOrderListByPage(pageDTO);
    }

    /**
     * 后台修改订单状态
     * @param orderDTO
     * @return
     */
    @PostMapping("/admin/order/edit-state")
    public ResponseDTO<Boolean> editOrderState(@RequestBody OrderDTO orderDTO){
        return storeService.updateOrderState(orderDTO);
    }

    /**
     * 后台删除订单数据
     * @param orderDTO
     * @return
     */
    @PostMapping("/admin/order/remove")
    public ResponseDTO<Boolean> removeOrder(@RequestBody OrderDTO orderDTO){
        return storeService.removeOrder(orderDTO);
    }
}
