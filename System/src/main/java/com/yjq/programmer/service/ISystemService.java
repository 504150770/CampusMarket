package com.yjq.programmer.service;

import com.yjq.programmer.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-01 8:28
 */
public interface ISystemService {

    ResponseDTO<String> uploadPhoto(MultipartFile photo);

    ResponseEntity<?> viewPhoto(String filename);
}
