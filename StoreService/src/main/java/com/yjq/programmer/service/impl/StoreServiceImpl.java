package com.yjq.programmer.service.impl;

import com.alipay.api.AlipayApiException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjq.programmer.bean.CodeMsg;
import com.yjq.programmer.dao.OrderItemMapper;
import com.yjq.programmer.dao.OrdersMapper;
import com.yjq.programmer.dao.StoreMapper;
import com.yjq.programmer.dao.my.MyOrderMapper;
import com.yjq.programmer.domain.*;
import com.yjq.programmer.dto.*;
import com.yjq.programmer.enums.OrderStateEnum;
import com.yjq.programmer.enums.ProductStateEnum;
import com.yjq.programmer.feignclient.ProductClient;
import com.yjq.programmer.feignclient.UserClient;
import com.yjq.programmer.service.IStoreService;
import com.yjq.programmer.util.AliPayUtil;
import com.yjq.programmer.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-06 15:25
 */
@Service
@Transactional
public class StoreServiceImpl implements IStoreService {

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private UserClient userClient;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private ProductClient productClient;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private AliPayUtil aliPayUtil;

    @Resource
    private MyOrderMapper myOrderMapper;

    /**
     * 保存店铺信息
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveStore(StoreDTO storeDTO) {
        if(storeDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 获取当前登录用户信息
        UserDTO userDTO = new UserDTO();
        userDTO.setToken(storeDTO.getToken());
        ResponseDTO<UserDTO> responseDTO = userClient.getLoginUser(userDTO);
        if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(storeDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Store store = CopyUtil.copy(storeDTO, Store.class);
        if(CommonUtil.isEmpty(store.getId())){
            // id为空 说明是添加数据
            // 判断店铺名称是否存在
            if(isStoreNameExist(store, "")){
                return ResponseDTO.errorByMsg(CodeMsg.STORE_NAME_EXIST);
            }
            // 生产8位id
            store.setId(UuidUtil.getShortUuid());
            store.setCreateTime(new Date());
            store.setUserId(responseDTO.getData().getId());
            // 添加数据到数据库
            if(storeMapper.insertSelective(store) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.STORE_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断店铺名称是否存在
            if(isStoreNameExist(store, store.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.STORE_NAME_EXIST);
            }
            // 修改数据库中数据
            if(storeMapper.updateByPrimaryKeySelective(store) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.STORE_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 根据登录用户获取店铺信息
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<StoreDTO> getByLoginUser(StoreDTO storeDTO) {
        // 获取当前登录用户信息
        UserDTO userDTO = new UserDTO();
        userDTO.setToken(storeDTO.getToken());
        ResponseDTO<UserDTO> responseUserDTO = userClient.getLoginUser(userDTO);
        if(!responseUserDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        StoreExample storeExample = new StoreExample();
        storeExample.createCriteria().andUserIdEqualTo(responseUserDTO.getData().getId());
        List<Store> storeList = storeMapper.selectByExample(storeExample);
        if(storeList == null || storeList.size() == 0) {
            return ResponseDTO.success(new StoreDTO());
        }
        List<StoreDTO> storeDTOList = CopyUtil.copyList(storeList, StoreDTO.class);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setStoreId(storeDTOList.get(0).getId());
        // 调用商品服务  获取店铺下的商品信息
        ResponseDTO<List<ProductDTO>> responseProductDTO = productClient.getByStore(productDTO);
//        ResponseDTO<List<ProductDTO>> responseProductDTO = JSONObject.parseObject(responseProduct, new TypeReference<ResponseDTO<List<ProductDTO>>>(){});
        if(!responseProductDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            storeDTOList.get(0).setProductDTOList(new ArrayList<>());
        }
        storeDTOList.get(0).setProductDTOList(responseProductDTO.getData());
        return ResponseDTO.success(storeDTOList.get(0));
    }

    /**
     * 根据id获取店铺信息
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<StoreDTO> getById(StoreDTO storeDTO) {
        if(CommonUtil.isEmpty(storeDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Store store = storeMapper.selectByPrimaryKey(storeDTO.getId());
        StoreDTO responseStoreDTO = CopyUtil.copy(store, StoreDTO.class);
        ProductDTO productDTO = new ProductDTO();
        if(storeDTO.getSearchProductDTO() != null) {
            productDTO.setName(storeDTO.getSearchProductDTO().getName());
            productDTO.setState(storeDTO.getSearchProductDTO().getState());
        }
        productDTO.setStoreId(responseStoreDTO.getId());
        // 调用商品服务  获取店铺下的商品信息
        ResponseDTO<List<ProductDTO>> responseDTO = productClient.getByStore(productDTO);
        if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.success(new StoreDTO());
        }
        responseStoreDTO.setProductDTOList(responseDTO.getData());
        return ResponseDTO.success(responseStoreDTO);
    }

    /**
     * 根据状态获取店铺信息
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<List<StoreDTO>> getByState(StoreDTO storeDTO) {
        if(storeDTO.getState() == null) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        StoreExample storeExample = new StoreExample();
        storeExample.createCriteria().andStateEqualTo(storeDTO.getState());
        List<Store> storeList = storeMapper.selectByExample(storeExample);
        return ResponseDTO.success(CopyUtil.copyList(storeList, StoreDTO.class));
    }

    /**
     * 结算页面订单展示
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<List<CartDTO>> generateOrder(OrderDTO orderDTO) {
        if(orderDTO.getCartIdList() == null || orderDTO.getCartIdList().size() == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        List<CartDTO> cartDTOList = new ArrayList<>();
        for(String cartId : orderDTO.getCartIdList()) {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setId(cartId);
            ResponseDTO<CartDTO> responseDTO = userClient.getCartById(cartDTO);
            if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                continue;
            }
            cartDTOList.add(responseDTO.getData());
        }
        return ResponseDTO.success(cartDTOList);
    }

    /**
     * 使用沙箱支付宝支付订单
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<String> aliPayOrder(OrderDTO orderDTO) {
        // 判断是否进行继续支付
        if(!CommonUtil.isEmpty(orderDTO.getId())) {
            Orders orders = ordersMapper.selectByPrimaryKey(orderDTO.getId());
            return initAliPay(orders.getNo(),  orders.getTotalPrice());
        }
        if(orderDTO.getCartIdList() == null || orderDTO.getCartIdList().size() == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(CommonUtil.isEmpty(orderDTO.getUserId())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        BigDecimal totalPrice = new BigDecimal("0.00");
        String orderNo = String.valueOf(new SnowFlake(2, 3).nextId());
        String orderId = UuidUtil.getShortUuid();
        // 获取用户挑选的商品信息
        for(String cartId : orderDTO.getCartIdList()) {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setId(cartId);
            ResponseDTO<CartDTO> responseDTO = userClient.getCartById(cartDTO);
            if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                return ResponseDTO.errorByMsg(CodeMsg.ORDER_PRODUCT_ERROR);
            }
            cartDTO = responseDTO.getData();
            // 判断商品库存是否足够
            if(cartDTO.getQuantity() > cartDTO.getProductDTO().getStock()) {
                CodeMsg codeMsg = CodeMsg.PRODUCT_STOCK_ERROR;
                codeMsg.setMsg("商品(" + cartDTO.getProductDTO().getName() + ")库存不足！" );
                return ResponseDTO.errorByMsg(codeMsg);
            }
            // 判断商品状态
            if(!ProductStateEnum.SUCCESS.getCode().equals(cartDTO.getProductDTO().getState())) {
                CodeMsg codeMsg = CodeMsg.PRODUCT_STATE_ERROR;
                codeMsg.setMsg("商品(" + cartDTO.getProductDTO().getName() + ")状态异常！" );
                return ResponseDTO.errorByMsg(codeMsg);
            }
            // 封装订单详情数据
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UuidUtil.getShortUuid());
            orderItem.setProductName(cartDTO.getProductDTO().getName());
            orderItem.setProductId(cartDTO.getProductDTO().getId());
            orderItem.setProductPhoto(cartDTO.getProductDTO().getPhoto());
            orderItem.setProductPrice(cartDTO.getProductDTO().getPrice());
            orderItem.setOrderId(orderId);
            orderItem.setState(OrderStateEnum.WAIT.getCode());
            orderItem.setStoreId(cartDTO.getProductDTO().getStoreId());
            orderItem.setQuantity(cartDTO.getQuantity());
            orderItem.setSumPrice(new BigDecimal(orderItem.getQuantity()).multiply(orderItem.getProductPrice()));
            orderItemList.add(orderItem);
            totalPrice = totalPrice.add(orderItem.getSumPrice());
            // 移除购物车数据
            userClient.removeCart(cartDTO);
        }
        // 订单详情数据入库
        for(OrderItem orderItem : orderItemList){
            if(orderItemMapper.insertSelective(orderItem) == 0) {
                throw new RuntimeException(CodeMsg.ORDER_ADD_ERROR.getMsg());
            }
        }
        // 封装订单数据  并入库
        Orders orders = CopyUtil.copy(orderDTO, Orders.class);
        orders.setId(orderId);
        orders.setCreateTime(new Date());
        orders.setNo(orderNo);
        orders.setTotalPrice(totalPrice);
        orders.setState(OrderStateEnum.WAIT.getCode());
        if(ordersMapper.insertSelective(orders) == 0) {
            throw new RuntimeException(CodeMsg.ORDER_ADD_ERROR.getMsg());
        }

        return initAliPay(orderNo, totalPrice);
    }

    /**
     * 修改订单状态
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> updateOrderState(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(orderDTO.getState() == null) {
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_STATE_EMPTY);
        }
        // 获取订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(orderDTO.getId());
        Integer oldState = orders.getState();
        orders.setState(orderDTO.getState());
        if(ordersMapper.updateByPrimaryKeySelective(orders) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_STATE_EDIT_ERROR);
        }
        orders.setState(oldState);
        // 获取订单详情数据
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orders.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        for(OrderItem orderItem : orderItemList) {
            // 如果是取消订单，且原先订单是已支付的订单，要回退库存
            // 如果是未支付订单 且原先订单是已支付订单 要回退库存
            if((OrderStateEnum.CANCEL.getCode().equals(orderDTO.getState()) && OrderStateEnum.PAY.getCode().equals(orders.getState()))
                || (OrderStateEnum.WAIT.getCode().equals(orderDTO.getState()) && OrderStateEnum.PAY.getCode().equals(orders.getState()))) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(orderItem.getProductId());
                ResponseDTO<ProductDTO> responseDTO = productClient.getById(productDTO);
                if(!CodeMsg.SUCCESS.getCode().equals(responseDTO.getCode())) {
                    continue;
                }
                productDTO = responseDTO.getData();
                productDTO.setStock(productDTO.getStock() + orderItem.getQuantity());
                productClient.saveProduct(productDTO);
            }
            // 如果是支付订单 且原先订单是未支付或取消订单
            if(OrderStateEnum.PAY.getCode().equals(orderDTO.getState()) && (OrderStateEnum.WAIT.getCode().equals(orders.getState())
                || OrderStateEnum.CANCEL.getCode().equals(orders.getState()))) {
                aliPayOrderSuccess(CopyUtil.copy(orders, OrderDTO.class));
            }
            orderItem.setState(orderDTO.getState());
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }

        return ResponseDTO.success(true);
    }

    /**
     * 初始化支付信息
     * @param orderNo
     * @param totalPrice
     * @return
     */
    public ResponseDTO<String> initAliPay(String orderNo, BigDecimal totalPrice) {
        // 封装沙箱支付宝支付信息
        AliPayBean alipayBean = new AliPayBean();
        alipayBean.setOut_trade_no(orderNo);
        alipayBean.setSubject("校园二手市场沙箱支付宝支付");
        alipayBean.setTotal_amount(String.valueOf(totalPrice));
        alipayBean.setBody("欢迎您在校园二手市场上下单！！");
        String pay = null;
        try {
            pay = aliPayUtil.pay(alipayBean);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return ResponseDTO.success(pay);
    }

    /**
     * 沙箱支付宝成功支付回调接口
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> aliPayOrderSuccess(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getNo())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andNoEqualTo(orderDTO.getNo());
        List<Orders> orderList = ordersMapper.selectByExample(ordersExample);
        if(orderList == null || orderList.size() == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        Orders orders = orderList.get(0);
        if(!OrderStateEnum.WAIT.getCode().equals(orders.getState()) && orderDTO.getState() == null) {
            // 不是待支付的状态，不执行后续逻辑
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_NOT_WAIT_PAY);
        }
        orders.setState(OrderStateEnum.PAY.getCode());
        ordersMapper.updateByPrimaryKeySelective(orders);
        // 减少商品对应的库存  并修改订单详情状态
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orders.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        for(OrderItem orderItem : orderItemList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(orderItem.getProductId());
            ResponseDTO<ProductDTO> responseDTO = productClient.getById(productDTO);
            if(!CodeMsg.SUCCESS.getCode().equals(responseDTO.getCode())) {
                continue;
            }
            productDTO = responseDTO.getData();
            productDTO.setStock(productDTO.getStock() - orderItem.getQuantity());
            productClient.saveProduct(productDTO);
            orderItem.setState(OrderStateEnum.PAY.getCode());
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }
        return ResponseDTO.successByMsg(true, "支付成功！");
    }

    /**
     * 获取所有订单总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getAllOrderTotal() {
        int total = ordersMapper.countByExample(new OrdersExample());
        return ResponseDTO.success(total);
    }

    /**
     * 根据用户获取订单数据
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<List<OrderDTO>> getOrderByUser(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getUserId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        OrdersExample ordersExample = new OrdersExample();
        ordersExample.createCriteria().andUserIdEqualTo(orderDTO.getUserId());
        ordersExample.setOrderByClause("create_time desc");
        List<Orders> ordersList = ordersMapper.selectByExample(ordersExample);
        List<OrderDTO> orderDTOList = CopyUtil.copyList(ordersList, OrderDTO.class);
        // 封装订单详情数据
        for(OrderDTO item : orderDTOList) {
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(item.getId());
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            List<OrderItemDTO> orderItemDTOList = CopyUtil.copyList(orderItemList, OrderItemDTO.class);
            for(OrderItemDTO orderItemDTO : orderItemDTOList) {
                Store store = storeMapper.selectByPrimaryKey(orderItemDTO.getStoreId());
                StoreDTO storeDTO = CopyUtil.copy(store, StoreDTO.class);
                if(storeDTO == null) {
                    orderItemDTO.setStoreDTO(new StoreDTO());
                } else {
                    orderItemDTO.setStoreDTO(storeDTO);
                }
            }
            item.setOrderItemDTOList(orderItemDTOList);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(item.getUserId());
            ResponseDTO<UserDTO> responseUserDTO = userClient.getById(userDTO);
            if(CodeMsg.SUCCESS.getCode().equals(responseUserDTO.getCode())) {
                item.setUserDTO(responseUserDTO.getData());
            } else {
                item.setUserDTO(userDTO);
            }
        }
        return ResponseDTO.success(orderDTOList);
    }

    /**
     * 获取某个店铺的订单数据
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<List<OrderDTO>> getOrderByStore(StoreDTO storeDTO) {
        if(CommonUtil.isEmpty(storeDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Map<String, OrderDTO> map = new HashMap<>();
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andStoreIdEqualTo(storeDTO.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        for(OrderItem orderItem : orderItemList) {
            OrderDTO value = map.get(orderItem.getOrderId());
            if(value == null) {
                Orders orders = ordersMapper.selectByPrimaryKey(orderItem.getOrderId());
                OrderDTO orderDTO = CopyUtil.copy(orders, OrderDTO.class);
                List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItemDTOList();
                if(orderItemDTOList == null) {
                    orderItemDTOList = new ArrayList<>();
                }
                orderItemDTOList.add(CopyUtil.copy(orderItem, OrderItemDTO.class));
                orderDTO.setOrderItemDTOList(orderItemDTOList);
                map.put(orders.getId(), orderDTO);
            } else {
                List<OrderItemDTO> orderItemDTOList = value.getOrderItemDTOList();
                if(orderItemDTOList == null) {
                    orderItemDTOList = new ArrayList<>();
                }
                orderItemDTOList.add(CopyUtil.copy(orderItem, OrderItemDTO.class));
                value.setOrderItemDTOList(orderItemDTOList);
                map.put(orderItem.getOrderId(), value);
            }
        }
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (String key : map.keySet()) {
            orderDTOList.add(map.get(key));
        }

        orderDTOList = orderDTOList.stream().sorted(Comparator.comparing(OrderDTO::getCreateTime).
                        reversed()).collect(Collectors.toList());

        for(OrderDTO orderDTO : orderDTOList) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(orderDTO.getUserId());
            ResponseDTO<UserDTO> responseUserDTO = userClient.getById(userDTO);
            if(CodeMsg.SUCCESS.getCode().equals(responseUserDTO.getCode())) {
                orderDTO.setUserDTO(responseUserDTO.getData());
            } else {
                orderDTO.setUserDTO(userDTO);
            }
            BigDecimal totalPrice = new BigDecimal("0");
            for(OrderItemDTO orderItemDTO : orderDTO.getOrderItemDTOList()) {
                totalPrice = totalPrice.add(orderItemDTO.getSumPrice());
                Store store = storeMapper.selectByPrimaryKey(orderItemDTO.getStoreId());
                StoreDTO copyStoreDTO = CopyUtil.copy(store, StoreDTO.class);
                if(copyStoreDTO == null) {
                    orderItemDTO.setStoreDTO(new StoreDTO());
                } else {
                    orderItemDTO.setStoreDTO(copyStoreDTO);
                }
            }
            orderDTO.setTotalPrice(totalPrice);
        }
        return ResponseDTO.success(orderDTOList);
    }

    /**
     * 修改某个订单详情的状态
     * @param orderItemDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> updateOrderItemState(OrderItemDTO orderItemDTO) {
        if(CommonUtil.isEmpty(orderItemDTO.getId()) || orderItemDTO.getState() == null) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 如果是取消，回退库存
        if(OrderStateEnum.CANCEL.getCode().equals(orderItemDTO.getState())) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(orderItemDTO.getProductId());
            ResponseDTO<ProductDTO> responseDTO = productClient.getById(productDTO);
            if(!CodeMsg.SUCCESS.getCode().equals(responseDTO.getCode())) {
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NOT_EXIST);
            }
            productDTO = responseDTO.getData();
            productDTO.setStock(productDTO.getStock() + orderItemDTO.getQuantity());
            productClient.saveProduct(productDTO);
        }
        OrderItem orderItem = CopyUtil.copy(orderItemDTO, OrderItem.class);
        if(orderItemMapper.updateByPrimaryKeySelective(orderItem) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_STATE_EDIT_ERROR);
        }
        return ResponseDTO.success(true);
    }

    /**
     * 获取今天订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getTodayPrice() {
        return ResponseDTO.success(myOrderMapper.todayTotalPrice());
    }

    /**
     * 获取本周订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getWeekPrice() {
        return ResponseDTO.success(myOrderMapper.weekTotalPrice());
    }

    /**
     * 获取本月订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getMonthPrice() {
        return ResponseDTO.success(myOrderMapper.monthTotalPrice());
    }

    @Override
    public ResponseDTO<List<Integer>> getOrderCountByDateAndState() {
        List<Integer> totalList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        List<Integer> finishStateList = new ArrayList<>();
        finishStateList.add(2);
        finishStateList.add(3);
        finishStateList.add(4);
        List<Integer> failStateList = new ArrayList<>();
        failStateList.add(1);
        failStateList.add(5);
        // 获取当天已完成的收益次数
        queryMap.put("start", 0);
        queryMap.put("end", -1);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取昨天已完成的收益次数
        queryMap.put("start", 1);
        queryMap.put("end", 0);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取前天已完成的收益次数
        queryMap.put("start", 2);
        queryMap.put("end", 1);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取大前天已完成的收益次数
        queryMap.put("start", 3);
        queryMap.put("end", 2);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取大大前天已完成的收益次数
        queryMap.put("start", 4);
        queryMap.put("end", 3);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        return ResponseDTO.success(totalList);
    }

    /**
     * 分页获取店铺数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<StoreDTO>> getStoreListByPage(PageDTO<StoreDTO> pageDTO) {
        StoreExample storeExample = new StoreExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            storeExample.createCriteria().andNameLike("%"+pageDTO.getSearchContent()+"%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        storeExample.setOrderByClause("create_time desc");
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出店铺数据
        List<Store> storeList = storeMapper.selectByExample(storeExample);
        PageInfo<Store> pageInfo = new PageInfo<>(storeList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<StoreDTO> storeDTOList = CopyUtil.copyList(storeList, StoreDTO.class);
        for(StoreDTO storeDTO : storeDTOList) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(storeDTO.getUserId());
            ResponseDTO<UserDTO> responseDTO = userClient.getById(userDTO);
            if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                storeDTO.setUserDTO(userDTO);
            } else {
                storeDTO.setUserDTO(responseDTO.getData());
            }
        }
        pageDTO.setList(storeDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 审核店铺数据
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> editStoreState(StoreDTO storeDTO) {
        if(CommonUtil.isEmpty(storeDTO.getId()) || storeDTO.getState() == null) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Store store = CopyUtil.copy(storeDTO, Store.class);
        if(storeMapper.updateByPrimaryKeySelective(store) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.STORE_EDIT_STATE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "审核成功！");
    }

    /**
     * 删除店铺数据
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeStore(StoreDTO storeDTO) {
        if(CommonUtil.isEmpty(storeDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除店铺数据
        if(storeMapper.deleteByPrimaryKey(storeDTO.getId()) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.STORE_DELETE_ERROR);
        }
        // 删除该店铺下所有商品数据
        ProductDTO productDTO = new ProductDTO();
        productDTO.setStoreId(storeDTO.getId());
        productClient.removeProductByStoreId(productDTO);

        return ResponseDTO.successByMsg(true, "删除店铺成功！");
    }

    /**
     * 分页获取订单数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<OrderDTO>> getOrderListByPage(PageDTO<OrderDTO> pageDTO) {
        OrdersExample orderExample = new OrdersExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            orderExample.createCriteria().andNoLike("%"+pageDTO.getSearchContent()+"%");
        }
        orderExample.setOrderByClause("create_time desc");
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出订单数据
        List<Orders> orderList = ordersMapper.selectByExample(orderExample);
        PageInfo<Orders> pageInfo = new PageInfo<>(orderList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<OrderDTO> orderDTOList = CopyUtil.copyList(orderList, OrderDTO.class);
        for(OrderDTO orderDTO : orderDTOList){
            UserDTO userDTO = new UserDTO();
            userDTO.setId(orderDTO.getUserId());
            ResponseDTO<UserDTO> responseDTO = userClient.getById(userDTO);
            if(!responseDTO.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                orderDTO.setUserDTO(userDTO);
            } else {
                orderDTO.setUserDTO(responseDTO.getData());
            }
            // 获取订单详情数据
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderDTO.getId());
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            List<OrderItemDTO> orderItemDTOList = CopyUtil.copyList(orderItemList, OrderItemDTO.class);
            for(OrderItemDTO orderItemDTO : orderItemDTOList) {
                Store store = storeMapper.selectByPrimaryKey(orderItemDTO.getStoreId());
                if(store == null) {
                    orderItemDTO.setStoreDTO(CopyUtil.copy(new Store(),StoreDTO.class));
                } else {
                    orderItemDTO.setStoreDTO(CopyUtil.copy(store, StoreDTO.class));
                }
            }
            orderDTO.setOrderItemDTOList(orderItemDTOList);
        }
        pageDTO.setList(orderDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 删除订单数据
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeOrder(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除订单数据
        if(ordersMapper.deleteByPrimaryKey(orderDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_DELETE_ERROR);
        }
        // 删除订单详情数据
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderDTO.getId());
        orderItemMapper.deleteByExample(orderItemExample);
        return ResponseDTO.successByMsg(true, "删除成功");
    }

    /**
     * 根据用户id删除店铺数据
     * @param storeDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeStoreByUserId(StoreDTO storeDTO) {
        StoreExample storeExample = new StoreExample();
        storeExample.createCriteria().andUserIdEqualTo(storeDTO.getUserId());
        List<Store> storeList = storeMapper.selectByExample(storeExample);
        for(Store store : storeList) {
            removeStore(CopyUtil.copy(store, StoreDTO.class));
        }
        return ResponseDTO.success(true);
    }

    /**
     * 判断店铺名称是否重复
     * @param store
     * @param id
     * @return
     */
    public Boolean isStoreNameExist(Store store, String id) {
        StoreExample storeExample = new StoreExample();
        storeExample.createCriteria().andNameEqualTo(store.getName());
        List<Store> selectedStoreList = storeMapper.selectByExample(storeExample);
        if(selectedStoreList != null && selectedStoreList.size() > 0) {
            if(selectedStoreList.size() > 1){
                return true; //出现重名
            }
            if(!selectedStoreList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }
}
