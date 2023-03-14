package com.yjq.programmer.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjq.programmer.bean.CodeMsg;
import com.yjq.programmer.dao.AddressMapper;
import com.yjq.programmer.dao.CartMapper;
import com.yjq.programmer.dao.UserMapper;
import com.yjq.programmer.domain.*;
import com.yjq.programmer.dto.*;
import com.yjq.programmer.enums.AddressSelectEnum;
import com.yjq.programmer.enums.ProductStateEnum;
import com.yjq.programmer.enums.RoleEnum;
import com.yjq.programmer.feignclient.ProductClient;
import com.yjq.programmer.feignclient.StoreClient;
import com.yjq.programmer.service.IUserService;
import com.yjq.programmer.utils.CommonUtil;
import com.yjq.programmer.utils.CopyUtil;
import com.yjq.programmer.utils.UuidUtil;
import com.yjq.programmer.utils.ValidateEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-06-29 10:55
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProductClient productClient;

    @Resource
    private StoreClient storeClient;

    @Resource
    private CartMapper cartMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 注册操作处理
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> register(UserDTO userDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(userDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        // 验证确认密码
        if(CommonUtil.isEmpty(userDTO.getCheckPassword())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_CHECK_PASSWORD_EMPTY);
        }
        if(!userDTO.getPassword().equals(userDTO.getCheckPassword())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_CHECK_PASSWORD_ERROR);
        }
        User user = CopyUtil.copy(userDTO, User.class);
        // 判断用户昵称是否重复
        if(isUsernameExist(user, "")){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
        }
        user.setId(UuidUtil.getShortUuid());
        user.setRoleId(RoleEnum.USER.getCode());
        if(userMapper.insertSelective(user) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_REGISTER_ERROR);
        }
        return ResponseDTO.successByMsg(true, "注册成功！");
    }

    /**
     * 后台用户登录
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> adminLogin(UserDTO userDTO) {
        // 进行是否为空判断
        if(CommonUtil.isEmpty(userDTO.getUsername())){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EMPTY);
        }
        if(CommonUtil.isEmpty(userDTO.getPassword())){
            return ResponseDTO.errorByMsg(CodeMsg.PASSWORD_EMPTY);
        }
        // 对比昵称和密码是否正确
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userDTO.getUsername()).andPasswordEqualTo(userDTO.getPassword());
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList == null || userList.size() != 1){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_PASSWORD_ERROR);
        }
        // 生成登录token并存入Redis中
        UserDTO selectedUserDto = CopyUtil.copy(userList.get(0), UserDTO.class);
        // 判断是不是管理员角色
        if(!selectedUserDto.getRoleId().equals(RoleEnum.ADMIN.getCode())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_NOT_IS_ADMIN);
        }
        String token = UuidUtil.getShortUuid();
        selectedUserDto.setToken(token);
        //把token存入redis中 有效期1小时
        stringRedisTemplate.opsForValue().set("USER_" + token, JSON.toJSONString(selectedUserDto), 3600, TimeUnit.SECONDS);
        return ResponseDTO.successByMsg(selectedUserDto, "登录成功！");
    }

    /**
     * 获取登录用户信息
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> getLoginUser(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getToken())){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseDTO<UserDTO> responseDTO = getLoginUser(userDTO.getToken());
        if(responseDTO.getCode() != 0){
            return responseDTO;
        }
        logger.info("获取到的登录信息={}", responseDTO.getData());
        return ResponseDTO.success(responseDTO.getData());
    }

    /**
     * 分页获取用户数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<UserDTO>> getUserListByPage(PageDTO<UserDTO> pageDTO) {
        UserExample userExample = new UserExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            userExample.createCriteria().andUsernameLike("%"+pageDTO.getSearchContent()+"%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出用户数据
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<UserDTO> userDTOList = CopyUtil.copyList(userList, UserDTO.class);
        pageDTO.setList(userDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存用户数据(添加、修改)
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveUser(UserDTO userDTO) {
        if(userDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(userDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        // 获取当前登录用户
        ResponseDTO<UserDTO> response = getLoginUser(userDTO.getToken());
        if(!response.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        User user = CopyUtil.copy(userDTO, User.class);
        if(CommonUtil.isEmpty(user.getId())){
            // id为空 说明是添加数据
            // 判断用户昵称是否存在
            if(isUsernameExist(user, "")){
                return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
            }
            // 生产8位id
            user.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(userMapper.insertSelective(user) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.USER_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断用户昵称是否存在
            if(isUsernameExist(user, user.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EXIST);
            }
            // 修改数据库中数据
            if(userMapper.updateByPrimaryKeySelective(user) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.USER_EDIT_ERROR);
            }
            User newUser = userMapper.selectByPrimaryKey(user.getId());
            // 判断修改的用户是否是当前登录用户  若是  更新redis中的用户数据
            UserDTO loginUserDTO = response.getData();
            if(user.getId().equals(loginUserDTO.getId())) {
                UserDTO newUserDTO = CopyUtil.copy(newUser, UserDTO.class);
                newUserDTO.setToken(loginUserDTO.getToken());
                //把token存入redis中 有效期1小时
                stringRedisTemplate.opsForValue().set("USER_" + loginUserDTO.getToken(), JSON.toJSONString(newUserDTO), 3600, TimeUnit.SECONDS);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除用户数据
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeUser(UserDTO userDTO) {
        if(userDTO == null || CommonUtil.isEmpty(userDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除用户信息
        if(userMapper.deleteByPrimaryKey(userDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.USER_DELETE_ERROR);
        }
        // 删除与该用户有关的购物车信息
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(userDTO.getId());
        cartMapper.deleteByExample(cartExample);
        // 删除与该用户有关的地址信息
        AddressExample addressExample = new AddressExample();
        addressExample.createCriteria().andUserIdEqualTo(userDTO.getId());
        addressMapper.deleteByExample(addressExample);
        // 删除与该用户有关的店铺信息
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setUserId(userDTO.getId());
        storeClient.removeStoreByUserId(storeDTO);
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 根据id获取用户信息
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> getById(UserDTO userDTO) {
        if(CommonUtil.isEmpty(userDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        User user = userMapper.selectByPrimaryKey(userDTO.getId());
        return ResponseDTO.success(CopyUtil.copy(user, UserDTO.class));
    }

    /**
     * 获取所有用户信息
     * @return
     */
    @Override
    public ResponseDTO<List<UserDTO>> getAllUserList() {
        List<User> userList = userMapper.selectByExample(new UserExample());
        return ResponseDTO.success(CopyUtil.copyList(userList, UserDTO.class));
    }

    /**
     * 前台用户登录
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<UserDTO> homeLogin(UserDTO userDTO) {
        // 进行是否为空判断
        if(CommonUtil.isEmpty(userDTO.getUsername())){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_EMPTY);
        }
        if(CommonUtil.isEmpty(userDTO.getPassword())){
            return ResponseDTO.errorByMsg(CodeMsg.PASSWORD_EMPTY);
        }
        // 对比昵称和密码是否正确
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userDTO.getUsername()).andPasswordEqualTo(userDTO.getPassword());
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList == null || userList.size() != 1){
            return ResponseDTO.errorByMsg(CodeMsg.USERNAME_PASSWORD_ERROR);
        }
        // 生成登录token并存入Redis中
        UserDTO selectedUserDto = CopyUtil.copy(userList.get(0), UserDTO.class);
        String token = UuidUtil.getShortUuid();
        selectedUserDto.setToken(token);
        //把token存入redis中 有效期1小时
        stringRedisTemplate.opsForValue().set("USER_" + token, JSON.toJSONString(selectedUserDto), 3600, TimeUnit.SECONDS);
        return ResponseDTO.successByMsg(selectedUserDto, "登录成功！");
    }

    /**
     * 退出登录操作处理
     * @param userDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> logout(UserDTO userDTO) {
        if(!CommonUtil.isEmpty(userDTO.getToken())){
            // token不为空  清除redis中数据
            stringRedisTemplate.delete("USER_" + userDTO.getToken());
        }
        return ResponseDTO.successByMsg(true, "退出登录成功！");
    }

    /**
     * 保存用户收货地址
     * @param addressDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveAddress(AddressDTO addressDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(addressDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Address address = CopyUtil.copy(addressDTO, Address.class);
        if(AddressSelectEnum.YES.getCode().equals(address.getIsSelect())) {
            // 先取消其他已经设为首选的地址
            AddressExample addressExample = new AddressExample();
            addressExample.createCriteria().andIsSelectEqualTo(AddressSelectEnum.YES.getCode());
            List<Address> addressList = addressMapper.selectByExample(addressExample);
            addressList.forEach(e -> {
                e.setIsSelect(AddressSelectEnum.NO.getCode());
                addressMapper.updateByPrimaryKeySelective(e);
            });
        }
        if(CommonUtil.isEmpty(addressDTO.getId())) {
            // 添加操作
            // 判断已经添加了几个地址了  是否超过三个
            AddressExample addressExample = new AddressExample();
            addressExample.createCriteria().andUserIdEqualTo(address.getUserId());
            List<Address> addressList = addressMapper.selectByExample(addressExample);
            if(addressList != null && addressList.size() >= 3) {
                return ResponseDTO.errorByMsg(CodeMsg.ADDRESS_OVER_NUM);
            }
            address.setId(UuidUtil.getShortUuid());
            if(addressMapper.insertSelective(address) == 0) {
                return ResponseDTO.errorByMsg(CodeMsg.ADDRESS_ADD_ERROR);
            }
        } else {
            // 修改操作
            if(addressMapper.updateByPrimaryKeySelective(address) == 0) {
                return ResponseDTO.errorByMsg(CodeMsg.ADDRESS_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除用户收货地址
     * @param addressDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeAddress(AddressDTO addressDTO) {
        if(CommonUtil.isEmpty(addressDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(addressMapper.deleteByPrimaryKey(addressDTO.getId()) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.ADDRESS_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 根据用户id获取地址信息
     * @param addressDTO
     * @return
     */
    @Override
    public ResponseDTO<List<AddressDTO>> getAddressByUserId(AddressDTO addressDTO) {
        if(CommonUtil.isEmpty(addressDTO.getUserId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        AddressExample addressExample = new AddressExample();
        AddressExample.Criteria criteria = addressExample.createCriteria();
        criteria.andUserIdEqualTo(addressDTO.getUserId());
        if(addressDTO.getIsSelect() != null) {
            criteria.andIsSelectEqualTo(addressDTO.getIsSelect());
        }
        List<Address> addressList = addressMapper.selectByExample(addressExample);
        return ResponseDTO.success(CopyUtil.copyList(addressList, AddressDTO.class));
    }

    /**
     * 保存用户购物车信息
     * @param cartDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveCart(CartDTO cartDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(cartDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(cartDTO.getProductId());
        ResponseDTO<ProductDTO> response = productClient.getById(productDTO);
        if(!response.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STATE_ERROR);
        }
        // 判断商品状态
        if(!ProductStateEnum.SUCCESS.getCode().equals(response.getData().getState())) {
            return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STATE_ERROR);
        }
        Cart cart = CopyUtil.copy(cartDTO, Cart.class);
        if(CommonUtil.isEmpty(cart.getId())) {
            // 添加操作
            // 判断购物车中是否存在此商品，若存在，直接在原数量上增加
            CartExample cartExample = new CartExample();
            cartExample.createCriteria().andUserIdEqualTo(cart.getUserId()).andProductIdEqualTo(cart.getProductId());
            List<Cart> cartList = cartMapper.selectByExample(cartExample);
            if(cartList != null && cartList.size() > 0) {
                // 判断库存是否足够
                Cart cartDB = cartList.get(0);
                if(response.getData().getStock() < (cartDB.getQuantity() +  cart.getQuantity())) {
                    return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STOCK_ERROR);
                }
                cartDB.setQuantity(cartDB.getQuantity() + cart.getQuantity());
                if(cartMapper.updateByPrimaryKeySelective(cartDB) == 0) {
                    return ResponseDTO.errorByMsg(CodeMsg.CART_ADD_ERROR);
                }
                return ResponseDTO.success(true);
            }
            // 没有则直接添加
            // 判断库存是否足够
            if(response.getData().getStock() < cart.getQuantity()) {
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STOCK_ERROR);
            }
            cart.setId(UuidUtil.getShortUuid());
            if(cartMapper.insertSelective(cart) == 0) {
                return ResponseDTO.errorByMsg(CodeMsg.CART_ADD_ERROR);
            }
        } else {
            // 修改操作
            // 判断库存是否足够
            if(response.getData().getStock() < cart.getQuantity()) {
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STOCK_ERROR);
            }
            if(cartMapper.updateByPrimaryKeySelective(cart) == 0) {
                return ResponseDTO.errorByMsg(CodeMsg.CART_EDIT_ERROR);
            }
        }
        return ResponseDTO.success(true);
    }

    /**
     * 查询用户购物车信息
     * @param cartDTO
     * @return
     */
    @Override
    public ResponseDTO<List<CartDTO>> getCartByUserId(CartDTO cartDTO) {
        if(CommonUtil.isEmpty(cartDTO.getUserId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andUserIdEqualTo(cartDTO.getUserId());
        List<Cart> cartList = cartMapper.selectByExample(cartExample);
        List<CartDTO> cartDTOList = CopyUtil.copyList(cartList, CartDTO.class);
        for(CartDTO cart : cartDTOList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(cart.getProductId());
            ResponseDTO<ProductDTO> response = productClient.getById(productDTO);
            if(!response.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                cart.setProductDTO(null);
            } else {
                cart.setProductDTO(response.getData());
            }
        }
        cartDTOList = cartDTOList.stream().filter(e -> e.getProductDTO() != null).collect(Collectors.toList());
        return ResponseDTO.success(cartDTOList);
    }

    /**
     * 根据购物车id查询购物车信息
     * @param cartDTO
     * @return
     */
    @Override
    public ResponseDTO<CartDTO> getCartById(CartDTO cartDTO) {
        if(CommonUtil.isEmpty(cartDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Cart cart = cartMapper.selectByPrimaryKey(cartDTO.getId());
        if(cart == null) {
            return ResponseDTO.errorByMsg(CodeMsg.CART_NOT_EXIST);
        }
        cartDTO = CopyUtil.copy(cart, CartDTO.class);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(cartDTO.getProductId());
        ResponseDTO<ProductDTO> response = productClient.getById(productDTO);
        if(!response.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseDTO.errorByMsg(CodeMsg.CART_NOT_EXIST);
        }
        cartDTO.setProductDTO(response.getData());
        return ResponseDTO.success(cartDTO);
    }

    /**
     * 根据购物车id删除购物车信息
     * @param cartDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeCart(CartDTO cartDTO) {
        if(CommonUtil.isEmpty(cartDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if(cartMapper.deleteByPrimaryKey(cartDTO.getId()) == 0) {
            return ResponseDTO.errorByMsg(CodeMsg.CART_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取在线用户
     * @return
     */
    @Override
    public ResponseDTO<List<UserDTO>> getOnlineUser() {
        List<UserDTO> userDTOList = new ArrayList<>();
        Set<String> keys = stringRedisTemplate.keys("USER_"  + "*");
        if(keys == null || keys.size() == 0){
            return ResponseDTO.success(userDTOList);
        }
        List<String> valueList = stringRedisTemplate.opsForValue().multiGet(keys);
        if(valueList == null || valueList.size() == 0){
            return ResponseDTO.success(userDTOList);
        }
        for(String value : valueList){
            if(!CommonUtil.isEmpty(value)){
                UserDTO userDTO = JSON.parseObject(value, UserDTO.class);
                userDTOList.add(userDTO);
            }
        }
        return ResponseDTO.success(userDTOList);
    }

    /**
     * 获取用户总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getUserTotal() {
        Integer total = userMapper.countByExample(new UserExample());
        return ResponseDTO.success(total);
    }

    /**
     * 根据商品id删除购物车信息
     * @param cartDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeCartByProductId(CartDTO cartDTO) {
        if(CommonUtil.isEmpty(cartDTO.getProductId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CartExample cartExample = new CartExample();
        cartExample.createCriteria().andProductIdEqualTo(cartDTO.getProductId());
        cartMapper.deleteByExample(cartExample);
        return ResponseDTO.successByMsg(true, "删除成功！");
    }


    /**
     * 获取当前登录用户
     * @return
     */
    public ResponseDTO<UserDTO> getLoginUser(String token){
        String value = stringRedisTemplate.opsForValue().get("USER_" + token);
        if(CommonUtil.isEmpty(value)){
            return ResponseDTO.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        UserDTO selectedUserDTO = JSON.parseObject(value, UserDTO.class);
        return ResponseDTO.success(selectedUserDTO);
    }


    /**
     * 判断用户昵称是否重复
     * @param user
     * @param id
     * @return
     */
    public Boolean isUsernameExist(User user, String id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> selectedUserList = userMapper.selectByExample(userExample);
        if(selectedUserList != null && selectedUserList.size() > 0) {
            if(selectedUserList.size() > 1){
                return true; //出现重名
            }
            if(!selectedUserList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }
}
