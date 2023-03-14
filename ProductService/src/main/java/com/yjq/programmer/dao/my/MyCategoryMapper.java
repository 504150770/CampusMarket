package com.yjq.programmer.dao.my;

import com.yjq.programmer.dto.CategoryDTO;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-04 17:31
 */
public interface MyCategoryMapper {

    // 根据销售金额获取前五个销售金额最高的商品分类
    List<CategoryDTO> getCategoryListByPrice();
}
