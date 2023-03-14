package com.yjq.programmer.constant;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-07 15:32
 */

import java.util.Arrays;
import java.util.List;

/**
 * 白名单配置
 */
public class WhiteList {

    //用户访问需要拦截但无需验证的url      Arrays.asList：字符串数组转化为List
    public static List<String> userNotNeedConfirmUrl = Arrays.asList(
            "/user/login",
            "/user/admin/login",
            "/user/register",
            "/system/view_photo",
            "/system/upload_photo",
            "/user/login/get",
            "/product/category/all",
            "/store/get",
            "/product/search",
            "/product/list",
            "/product/detail",
            "/product/get",
            "/product/similar"
    );
}
