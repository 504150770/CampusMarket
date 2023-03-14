package com.yjq.programmer.service.impl;

import com.yjq.programmer.bean.CodeMsg;
import com.yjq.programmer.dto.ResponseDTO;
import com.yjq.programmer.service.ISystemService;
import com.yjq.programmer.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-01 8:29
 */
@Service
public class SystemServiceImpl implements ISystemService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${yjq.upload.photo.path}")
    private String uploadPhotoPath;//文件保存位置

    private static final Logger logger = LoggerFactory.getLogger(SystemServiceImpl.class);


    /**
     * 自定义上传图片处理
     * @param photo
     * @return
     */
    @Override
    public ResponseDTO<String> uploadPhoto(MultipartFile photo) {
        if(photo == null){
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_EMPTY);
        }
        //检查上传文件大小 不能超过2MB
        if(photo.getSize() > 2*1024*1024) {
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_SURPASS_MAX_SIZE);
        }
        //获取文件后缀
        String suffix = Objects.requireNonNull(photo.getOriginalFilename()).substring(photo.getOriginalFilename().lastIndexOf(".")+1, photo.getOriginalFilename().length());
        if(!CommonUtil.isPhoto(suffix)){
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_FORMAT_NOT_CORRECT);
        }
        String savePath = uploadPhotoPath + CommonUtil.getFormatterDate(new Date(), "yyyyMMdd") + "\\";
        File savePathFile = new File(savePath);
        if(!savePathFile.exists()){
            //若不存在改目录，则创建目录
            savePathFile.mkdir();
        }
        String filename = new Date().getTime()+"."+suffix;
        logger.info("保存图片的路径:{}",savePath + filename);
        try {
            //将文件保存至指定目录
            photo.transferTo(new File(savePath + filename));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.errorByMsg(CodeMsg.SAVE_FILE_EXCEPTION);
        }
        String filepath = CommonUtil.getFormatterDate(new Date(), "yyyyMMdd") + "/" + filename;
        return ResponseDTO.successByMsg(filepath, "图片上传成功！");
    }

    /**
     * 系统统一的图片查看方法
     * @param filename
     * @return
     */
    @Override
    public ResponseEntity<?> viewPhoto(String filename) {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("file:" + uploadPhotoPath + filename);
        try {
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
