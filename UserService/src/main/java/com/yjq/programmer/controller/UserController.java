package com.yjq.programmer.controller;

import com.yjq.programmer.dto.*;
import com.yjq.programmer.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-06-26 22:10
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 注册操作处理
     * @param userDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseDTO<Boolean> register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    /**
     * 获取在线用户
     * @return
     */
    @PostMapping("/online")
    public ResponseDTO<List<UserDTO>> getOnlineUser(){
        return userService.getOnlineUser();
    }

    /**
     * 获取用户总数
     * @return
     */
    @PostMapping("/total")
    public ResponseDTO<Integer> getUserTotal(){
        return userService.getUserTotal();
    }

    /**
     * 后台用户登录
     * @param userDTO
     * @return
     */
    @PostMapping("/admin/login")
    public ResponseDTO<UserDTO> adminLogin(@RequestBody UserDTO userDTO){
        return userService.adminLogin(userDTO);
    }

    /**
     * 获取登录用户信息
     * @param userDTO
     * @return
     */
    @PostMapping("/login/get")
    public ResponseDTO<UserDTO> getLoginUser(@RequestBody UserDTO userDTO){
        return userService.getLoginUser(userDTO);
    }


    /**
     * 后台分页获取用户数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/admin/list")
    public ResponseDTO<PageDTO<UserDTO>> getUserListByPage(@RequestBody PageDTO<UserDTO> pageDTO){
        return userService.getUserListByPage(pageDTO);
    }

    /**
     * 后台保存用户数据(添加、修改)
     * @param userDTO
     * @return
     */
    @PostMapping("/admin/save")
    public ResponseDTO<Boolean> saveUserByAdmin(@RequestBody UserDTO userDTO){
        return userService.saveUser(userDTO);
    }

    /**
     * 前台修改用户数据
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveUser(@RequestBody UserDTO userDTO){
        return userService.saveUser(userDTO);
    }

    /**
     * 后台删除用户数据
     * @param userDTO
     * @return
     */
    @PostMapping("/admin/remove")
    public ResponseDTO<Boolean> removeUser(@RequestBody UserDTO userDTO){
        return userService.removeUser(userDTO);
    }

    /**
     * 根据id获取用户信息
     * @param userDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<UserDTO> getById(@RequestBody UserDTO userDTO) {
        return userService.getById(userDTO);
    }

    /**
     * 获取所有用户信息
     * @return
     */
    @PostMapping("/all")
    public ResponseDTO<List<UserDTO>> getAllUserList() {
        return userService.getAllUserList();
    }

    /**
     * 前台用户登录
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseDTO<UserDTO> homeLogin(@RequestBody UserDTO userDTO){
        return userService.homeLogin(userDTO);
    }

    /**
     * 用户退出登录
     * @return
     */
    @PostMapping("/logout")
    public ResponseDTO<Boolean> logout(@RequestBody UserDTO userDTO){
        return userService.logout(userDTO);
    }

    /**
     * 保存用户收货地址信息
     * @param addressDTO
     * @return
     */
    @PostMapping("/address/save")
    public ResponseDTO<Boolean> saveAddress(@RequestBody AddressDTO addressDTO){
        return userService.saveAddress(addressDTO);
    }

    /**
     * 删除用户收货地址
     * @param addressDTO
     * @return
     */
    @PostMapping("/address/remove")
    public ResponseDTO<Boolean> removeAddress(@RequestBody AddressDTO addressDTO){
        return userService.removeAddress(addressDTO);
    }

    /**
     * 根据用户id获取地址信息
     * @param addressDTO
     * @return
     */
    @PostMapping("/address/get")
    public ResponseDTO<List<AddressDTO>> getAddressByUserId(@RequestBody AddressDTO addressDTO){
        return userService.getAddressByUserId(addressDTO);
    }

    /**
     * 保存用户购物车信息
     * @param cartDTO
     * @return
     */
    @PostMapping("/cart/save")
    public ResponseDTO<Boolean> saveCart(@RequestBody CartDTO cartDTO){
        return userService.saveCart(cartDTO);
    }

    /**
     * 查询用户购物车信息
     * @param cartDTO
     * @return
     */
    @PostMapping("/cart/get")
    public ResponseDTO<List<CartDTO>> getCartByUserId(@RequestBody CartDTO cartDTO) {
        return userService.getCartByUserId(cartDTO);
    }

    /**
     * 根据购物车id查询购物车信息
     * @param cartDTO
     * @return
     */
    @PostMapping("/cart/id")
    public ResponseDTO<CartDTO> getCartById(@RequestBody CartDTO cartDTO) {
        return userService.getCartById(cartDTO);
    }

    /**
     * 根据购物车id删除购物车信息
     * @param cartDTO
     * @return
     */
    @PostMapping("/cart/remove")
    public ResponseDTO<Boolean> removeCart(@RequestBody CartDTO cartDTO) {
        return userService.removeCart(cartDTO);
    }

    /**
     * 根据商品id删除购物车信息
     * @param cartDTO
     * @return
     */
    @PostMapping("/cart/remove/product_id")
    public ResponseDTO<Boolean> removeCartByProductId(@RequestBody CartDTO cartDTO) {
        return userService.removeCartByProductId(cartDTO);
    }
}
