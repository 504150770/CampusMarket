package com.yjq.programmer.feignclient;

import com.yjq.programmer.dto.CartDTO;
import com.yjq.programmer.dto.ResponseDTO;
import com.yjq.programmer.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-08 8:53
 */

/**
 * 调用用户接口
 */
@FeignClient("UserService")
public interface UserClient {

    @PostMapping("/user/login/get")
    ResponseDTO<UserDTO> getLoginUser(@RequestBody UserDTO userDTO);

    @PostMapping("/user/cart/id")
    ResponseDTO<CartDTO> getCartById(@RequestBody CartDTO cartDTO);

    @PostMapping("/user/cart/remove")
    ResponseDTO<Boolean> removeCart(@RequestBody CartDTO cartDTO);

    @PostMapping("/user/get")
    ResponseDTO<UserDTO> getById(@RequestBody UserDTO userDTO);

}
