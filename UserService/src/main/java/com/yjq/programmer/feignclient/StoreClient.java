package com.yjq.programmer.feignclient;

import com.yjq.programmer.dto.ResponseDTO;
import com.yjq.programmer.dto.StoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-08-07 12:05
 */
@FeignClient("StoreService")
public interface StoreClient {

    @PostMapping("/store/remove/user_id")
    ResponseDTO<Boolean> removeStoreByUserId(@RequestBody StoreDTO storeDTO);
}
