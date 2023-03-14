package com.yjq.programmer.dao.my;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-04 13:24
 */
public interface MyOrderMapper {

    // 获取今日订单成交金额
    BigDecimal todayTotalPrice();

    // 获取本周订单成交金额
    BigDecimal weekTotalPrice();

    // 获取本月订单成交金额
    BigDecimal monthTotalPrice();

    // 根据时间范围和订单状态获取交易的订单总数
    Integer getOrderTotalByDateAndState(@Param("queryMap") Map<String, Object> queryMap, @Param("stateList") List<Integer> stateList);


}
