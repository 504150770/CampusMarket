package com.yjq.programmer.controller;

import com.yjq.programmer.dto.ResponseDTO;
import com.yjq.programmer.service.ISystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-01 8:24
 */

@RestController
@RequestMapping("/system")
public class SystemController {

    @Resource
    private ISystemService systemService;

    /**
     * 自定义上传图片处理
     * @param photo
     * @return
     */
    @PostMapping(value="/upload_photo")
    public ResponseDTO<String> uploadPhoto(MultipartFile photo){
        return systemService.uploadPhoto(photo);
    }

    /**
     * 系统统一的图片查看方法
     * @param filename
     * @return
     */
    @RequestMapping(value="/view_photo")
    public ResponseEntity<?> viewPhoto(@RequestParam(name="filename",required=true) String filename){
        return systemService.viewPhoto(filename);
    }

}
