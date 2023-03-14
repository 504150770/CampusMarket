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
 * @create 2022-07-02 11:51
 */

/**
 * 调用用户接口
 */
@FeignClient("UserService")
public interface UserClient {

    @PostMapping("/user/get")
    ResponseDTO<UserDTO> getById(@RequestBody UserDTO userDTO);

    @PostMapping("/user/cart/remove/product_id")
    ResponseDTO<Boolean> removeCartByProductId(@RequestBody CartDTO cartDTO);
}
