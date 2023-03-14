package com.yjq.programmer.controller;

import com.yjq.programmer.dto.CategoryDTO;
import com.yjq.programmer.dto.PageDTO;
import com.yjq.programmer.dto.ProductDTO;
import com.yjq.programmer.dto.ResponseDTO;
import com.yjq.programmer.service.IProductService;
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
 * @create 2022-07-01 16:57
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private IProductService productService;

    /**
     * 后台保存商品分类数据(添加、修改)
     * @param categoryDTO
     * @return
     */
    @PostMapping("/category/admin/save")
    public ResponseDTO<Boolean> saveCategory(@RequestBody CategoryDTO categoryDTO){
        return productService.saveCategory(categoryDTO);
    }

    /**
     * 获取商品总数
     * @return
     */
    @PostMapping("/total")
    public ResponseDTO<Integer> getProductTotal(){
        return productService.getProductTotal();
    }

    /**
     * 后台分页获取商品分类数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/category/admin/list")
    public ResponseDTO<PageDTO<CategoryDTO>> getCategoryListByPage(@RequestBody PageDTO<CategoryDTO> pageDTO){
        return productService.getCategoryListByPage(pageDTO);
    }

    /**
     * 后台删除商品分类数据
     * @param categoryDTO
     * @return
     */
    @PostMapping("/category/admin/remove")
    public ResponseDTO<Boolean> removeCategory(@RequestBody CategoryDTO categoryDTO){
        return productService.removeCategory(categoryDTO);
    }


    /**
     * 后台保存商品数据(添加、修改)
     * @param productDTO
     * @return
     */
    @PostMapping("/admin/save")
    public ResponseDTO<Boolean> saveProductByAdmin(@RequestBody ProductDTO productDTO){
        return productService.saveProduct(productDTO);
    }

    /**
     * 前台保存商品数据(添加、修改)
     * @param productDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveProduct(@RequestBody ProductDTO productDTO){
        return productService.saveProduct(productDTO);
    }

    /**
     * 删除商品数据
     * @param productDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeProduct(@RequestBody ProductDTO productDTO){
        return productService.removeProduct(productDTO);
    }

    /**
     * 前台获取商品数据
     * @param productDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<List<ProductDTO>> getProductList(@RequestBody ProductDTO productDTO){
        return productService.getProductList(productDTO);
    }

    /**
     * 后台分页获取商品数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/admin/list")
    public ResponseDTO<PageDTO<ProductDTO>> getProductListByPage(@RequestBody PageDTO<ProductDTO> pageDTO){
        return productService.getProductListByPage(pageDTO);
    }

    /**
     * 后台删除商品数据
     * @param productDTO
     * @return
     */
    @PostMapping("/admin/remove")
    public ResponseDTO<Boolean> removeProductByAdmin(@RequestBody ProductDTO productDTO){
        return productService.removeProduct(productDTO);
    }

    /**
     * 获取所有商品分类数据
     * @return
     */
    @PostMapping("/category/all")
    public ResponseDTO<List<CategoryDTO>> getAllCategoryList(){
        return productService.getAllCategoryList();
    }


    /**
     * 根据id获取商品数据
     * @param productDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<ProductDTO> getById(@RequestBody ProductDTO productDTO){
        return productService.getById(productDTO);
    }

    /**
     * 多条件搜索商品数据
     * @param productDTO
     * @return
     */
    @PostMapping("/search")
    public ResponseDTO<List<ProductDTO>> searchProductList(@RequestBody ProductDTO productDTO){
        return productService.searchProductList(productDTO);
    }

    /**
     * 根据店铺id获取商品信息
     * @param productDTO
     * @return
     */
    @PostMapping("/store/get")
    public ResponseDTO<List<ProductDTO>> getByStore(@RequestBody ProductDTO productDTO){
        return productService.getByStore(productDTO);
    }

    /**
     * 获取五个成交额最高的商品分类
     * @return
     */
    @PostMapping("/category/total-price")
    public ResponseDTO<List<CategoryDTO>> getCategoryListByPrice(){
        return productService.getCategoryListByPrice();
    }

    /**
     * 根据店铺id删除商品信息
     * @param productDTO
     * @return
     */
    @PostMapping("/remove/store_id")
    public ResponseDTO<Boolean> removeProductByStoreId(@RequestBody ProductDTO productDTO) {
        return productService.removeProductByStoreId(productDTO);
    }

    /**
     * 获取同类好货商品
     * @param productDTO
     * @return
     */
    @PostMapping("/similar")
    public ResponseDTO<List<ProductDTO>> getSimilarProduct(@RequestBody ProductDTO productDTO) {
        return productService.getSimilarProduct(productDTO);
    }
}
