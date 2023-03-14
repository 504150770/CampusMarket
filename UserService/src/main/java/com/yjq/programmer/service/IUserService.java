package com.yjq.programmer.service;

import com.yjq.programmer.dto.*;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-06-29 10:55
 */
public interface IUserService {

    // 注册操作处理
    ResponseDTO<Boolean> register(UserDTO userDTO);

    // 后台登录操作
    ResponseDTO<UserDTO> adminLogin(UserDTO userDTO);

    // 获取登录用户信息
    ResponseDTO<UserDTO> getLoginUser(UserDTO userDTO);

    // 分页获取用户数据
    ResponseDTO<PageDTO<UserDTO>> getUserListByPage(PageDTO<UserDTO> pageDTO);

    // 保存用户数据(添加、修改)
    ResponseDTO<Boolean> saveUser(UserDTO userDTO);

    // 删除用户数据
    ResponseDTO<Boolean> removeUser(UserDTO userDTO);

    // 根据id获取用户信息
    ResponseDTO<UserDTO> getById(UserDTO userDTO);

    // 获取所有用户信息
    ResponseDTO<List<UserDTO>> getAllUserList();

    // 前台用户登录
    ResponseDTO<UserDTO> homeLogin(UserDTO userDTO);

    // 退出登录操作
    ResponseDTO<Boolean> logout(UserDTO userDTO);

    // 保存用户收货地址
    ResponseDTO<Boolean> saveAddress(AddressDTO addressDTO);

    // 删除用户收货地址
    ResponseDTO<Boolean> removeAddress(AddressDTO addressDTO);

    // 根据用户id获取地址信息
    ResponseDTO<List<AddressDTO>> getAddressByUserId(AddressDTO addressDTO);

    // 保存用户购物车信息
    ResponseDTO<Boolean> saveCart(CartDTO cartDTO);

    // 查询用户购物车信息
    ResponseDTO<List<CartDTO>> getCartByUserId(CartDTO cartDTO);

    // 根据购物车id查询购物车信息
    ResponseDTO<CartDTO> getCartById(CartDTO cartDTO);

    // 根据购物车id删除购物车信息
    ResponseDTO<Boolean> removeCart(CartDTO cartDTO);

    // 获取在线用户
    ResponseDTO<List<UserDTO>> getOnlineUser();

    // 获取用户总数
    ResponseDTO<Integer> getUserTotal();

    // 根据商品id删除购物车信息
    ResponseDTO<Boolean> removeCartByProductId(CartDTO cartDTO);
}
