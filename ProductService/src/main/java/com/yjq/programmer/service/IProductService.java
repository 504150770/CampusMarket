package com.yjq.programmer.service;

import com.yjq.programmer.dto.CategoryDTO;
import com.yjq.programmer.dto.PageDTO;
import com.yjq.programmer.dto.ProductDTO;
import com.yjq.programmer.dto.ResponseDTO;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-01 16:59
 */
public interface IProductService {

    // 分页获取商品分类数据
    ResponseDTO<PageDTO<CategoryDTO>> getCategoryListByPage(PageDTO<CategoryDTO> pageDTO);

    // 保存商品分类数据(添加、修改)
    ResponseDTO<Boolean> saveCategory(CategoryDTO categoryDTO);

    // 删除商品分类数据
    ResponseDTO<Boolean> removeCategory(CategoryDTO categoryDTO);

    // 分页获取商品数据
    ResponseDTO<PageDTO<ProductDTO>> getProductListByPage(PageDTO<ProductDTO> pageDTO);

    // 保存商品数据(添加、修改)
    ResponseDTO<Boolean> saveProduct(ProductDTO productDTO);

    // 获取商品总数
    ResponseDTO<Integer> getProductTotal();

    // 删除商品数据
    ResponseDTO<Boolean> removeProduct(ProductDTO productDTO);

    // 获取所有商品分类数据
    ResponseDTO<List<CategoryDTO>> getAllCategoryList();

    // 前台获取商品数据
    ResponseDTO<List<ProductDTO>> getProductList(ProductDTO productDTO);

    // 根据id获取商品数据
    ResponseDTO<ProductDTO> getById(ProductDTO productDTO);

    // 多条件搜索商品数据
    ResponseDTO<List<ProductDTO>> searchProductList(ProductDTO productDTO);

    // 根据店铺id获取商品信息
    ResponseDTO<List<ProductDTO>> getByStore(ProductDTO productDTO);

    // 获取五个成交额最高的商品分类
    ResponseDTO<List<CategoryDTO>> getCategoryListByPrice();

    // 根据店铺id删除商品信息
    ResponseDTO<Boolean> removeProductByStoreId(ProductDTO productDTO);

    // 获取同类好货商品
    ResponseDTO<List<ProductDTO>> getSimilarProduct(ProductDTO productDTO);
}
