package com.yjq.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjq.programmer.bean.CodeMsg;
import com.yjq.programmer.dao.CategoryMapper;
import com.yjq.programmer.dao.ProductMapper;
import com.yjq.programmer.dao.my.MyCategoryMapper;
import com.yjq.programmer.domain.Category;
import com.yjq.programmer.domain.CategoryExample;
import com.yjq.programmer.domain.Product;
import com.yjq.programmer.domain.ProductExample;
import com.yjq.programmer.dto.*;
import com.yjq.programmer.enums.ProductStateEnum;
import com.yjq.programmer.feignclient.StoreClient;
import com.yjq.programmer.feignclient.UserClient;
import com.yjq.programmer.service.IProductService;
import com.yjq.programmer.utils.CommonUtil;
import com.yjq.programmer.utils.CopyUtil;
import com.yjq.programmer.utils.UuidUtil;
import com.yjq.programmer.utils.ValidateEntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-01 17:05
 */
@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private StoreClient storeClient;

    @Resource
    private UserClient userClient;

    @Resource
    private MyCategoryMapper myCategoryMapper;


    /**
     * 分页获取商品分类数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<CategoryDTO>> getCategoryListByPage(PageDTO<CategoryDTO> pageDTO) {
        CategoryExample categoryExample = new CategoryExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            categoryExample.createCriteria().andNameLike("%"+pageDTO.getSearchContent()+"%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        categoryExample.setOrderByClause("sort desc, id desc");
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出商品分类数据
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<CategoryDTO> categoryDTOList = CopyUtil.copyList(categoryList, CategoryDTO.class);
        pageDTO.setList(categoryDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存商品分类数据(添加、修改)
     * @param categoryDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveCategory(CategoryDTO categoryDTO) {
        if(categoryDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(categoryDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Category category = CopyUtil.copy(categoryDTO, Category.class);
        if(CommonUtil.isEmpty(category.getId())){
            // id为空 说明是添加数据
            // 判断商品分类名称是否存在
            if(isCategoryNameExist(category, "")){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_NAME_EXIST);
            }
            // 生产8位id
            category.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(categoryMapper.insertSelective(category) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断商品分类名称是否存在
            if(isCategoryNameExist(category, category.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_NAME_EXIST);
            }
            // 修改数据库中数据
            if(categoryMapper.updateByPrimaryKeySelective(category) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除商品分类数据
     * @param categoryDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeCategory(CategoryDTO categoryDTO) {
        if(categoryDTO == null || CommonUtil.isEmpty(categoryDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除商品分类信息
        if(categoryMapper.deleteByPrimaryKey(categoryDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.CATEGORY_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 分页获取商品数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<ProductDTO>> getProductListByPage(PageDTO<ProductDTO> pageDTO) {
        ProductExample productExample = new ProductExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            productExample.createCriteria().andNameLike("%"+pageDTO.getSearchContent()+"%");
        }
        productExample.setOrderByClause("create_time desc");
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出商品数据
        List<Product> productList = productMapper.selectByExample(productExample);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        for(ProductDTO productDTO : productDTOList){
            // 分装商品分类信息
            Category category = categoryMapper.selectByPrimaryKey(productDTO.getCategoryId());
            CategoryDTO categoryDTO = CopyUtil.copy(category, CategoryDTO.class);
            productDTO.setCategoryDTO(categoryDTO);
            // 分装所属店铺信息
            StoreDTO storeDTO = new StoreDTO();
            if(!CommonUtil.isEmpty(productDTO.getStoreId())) {
                storeDTO.setId(productDTO.getStoreId());
                ResponseDTO<StoreDTO> responseDTO = storeClient.getById(storeDTO);
                productDTO.setStoreDTO(responseDTO.getData());
            } else {
                productDTO.setStoreDTO(storeDTO);
            }
        }
        pageDTO.setList(productDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存商品数据(添加、修改)
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveProduct(ProductDTO productDTO) {
        if(productDTO == null){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(productDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Product product = CopyUtil.copy(productDTO, Product.class);
        if(product.getState() == null) {
            product.setState(ProductStateEnum.WAIT.getCode());
        }
        if(CommonUtil.isEmpty(product.getId())){
            // id为空 说明是添加数据
            // 判断商品名称是否存在
            if(isProductNameExist(product, "")){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NAME_EXIST);
            }
            // 生产8位id
            product.setId(UuidUtil.getShortUuid());
            product.setCreateTime(new Date());
            // 添加数据到数据库
            if(productMapper.insertSelective(product) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 判断商品名称是否存在
            if(isProductNameExist(product, product.getId())){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NAME_EXIST);
            }
            // 修改数据库中数据
            if(productMapper.updateByPrimaryKeySelective(product) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 获取商品总数
     * @return
     */
    @Override
    public ResponseDTO<Integer> getProductTotal() {
        int total = productMapper.countByExample(new ProductExample());
        return ResponseDTO.success(total);
    }

    /**
     * 删除商品数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeProduct(ProductDTO productDTO) {
        if(productDTO == null || CommonUtil.isEmpty(productDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除商品信息
        if(productMapper.deleteByPrimaryKey(productDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_DELETE_ERROR);
        }
        // 删除属于此商品的购物车信息
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(productDTO.getId());
        userClient.removeCartByProductId(cartDTO);
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取所有商品分类数据
     * @return
     */
    @Override
    public ResponseDTO<List<CategoryDTO>> getAllCategoryList() {
        List<Category> categoryList = categoryMapper.selectByExample(new CategoryExample());
        List<CategoryDTO> categoryDTOList = CopyUtil.copyList(categoryList, CategoryDTO.class);
        return ResponseDTO.success(categoryDTOList);
    }

    /**
     * 前台获取商品数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> getProductList(ProductDTO productDTO) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andStateEqualTo(ProductStateEnum.SUCCESS.getCode());
        productExample.setOrderByClause("create_time desc");
        List<Product> productList = productMapper.selectByExample(productExample);
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        for(ProductDTO item : productDTOList) {
            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setId(item.getStoreId());
            ResponseDTO<StoreDTO> responseDTO = storeClient.getById(storeDTO);
            if(!CodeMsg.SUCCESS.getCode().equals(responseDTO.getCode()) ||
                responseDTO.getData() == null || !ProductStateEnum.SUCCESS.getCode().equals(responseDTO.getData().getState())) {
                continue;
            } else {
                item.setStoreDTO(responseDTO.getData());
            }
            Category category = categoryMapper.selectByPrimaryKey(item.getCategoryId());
            item.setCategoryDTO(CopyUtil.copy(category, CategoryDTO.class));
        }
        productDTOList = productDTOList.stream().filter(ProductDTO -> ProductDTO.getStoreDTO() != null).collect(Collectors.toList());
        return ResponseDTO.success(productDTOList);
    }

    /**
     * 根据id获取商品数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<ProductDTO> getById(ProductDTO productDTO) {
        if(CommonUtil.isEmpty(productDTO.getId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Product product = productMapper.selectByPrimaryKey(productDTO.getId());
        if(product == null) {
            return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_NOT_EXIST);
        }
        // 获取商品所属店铺信息
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setId(product.getStoreId());
        ResponseDTO<StoreDTO> responseDTO = storeClient.getById(storeDTO);
        ProductDTO responseProductDTO = CopyUtil.copy(product, ProductDTO.class);
        responseProductDTO.setStoreDTO(responseDTO.getData());
        if (productDTO.getState() == null) {
            return ResponseDTO.success(responseProductDTO);
        }
        if(product.getState().equals(productDTO.getState())) {
            return ResponseDTO.success(responseProductDTO);
        } else {
            return ResponseDTO.success(new ProductDTO());
        }
    }

    /**
     * 多条件搜索商品数据
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> searchProductList(ProductDTO productDTO) {
        ProductExample productExample = new ProductExample();
        ProductExample.Criteria criteria = productExample.createCriteria();
        if(!CommonUtil.isEmpty(productDTO.getName())) {
            criteria.andNameLike("%" + productDTO.getName() + "%");
        }
        if(!CommonUtil.isEmpty(productDTO.getCategoryId()) && !"0".equals(productDTO.getCategoryId())) {
            criteria.andCategoryIdEqualTo(productDTO.getCategoryId());
        }
        if(productDTO.getTag() != null && productDTO.getTag() != 0) {
            criteria.andTagEqualTo(productDTO.getTag());
        }
        criteria.andStateEqualTo(ProductStateEnum.SUCCESS.getCode());
        productExample.setOrderByClause("create_time desc");
        List<Product> productList = productMapper.selectByExample(productExample);
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        for(ProductDTO item : productDTOList) {
            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setId(item.getStoreId());
            ResponseDTO<StoreDTO> responseDTO = storeClient.getById(storeDTO);
            if(!CodeMsg.SUCCESS.getCode().equals(responseDTO.getCode()) ||
                    responseDTO.getData() == null || !ProductStateEnum.SUCCESS.getCode().equals(responseDTO.getData().getState())) {
                continue;
            } else {
                item.setStoreDTO(responseDTO.getData());
            }
            Category category = categoryMapper.selectByPrimaryKey(item.getCategoryId());
            item.setCategoryDTO(CopyUtil.copy(category, CategoryDTO.class));
        }
        productDTOList = productDTOList.stream().filter(ProductDTO -> ProductDTO.getStoreDTO() != null).collect(Collectors.toList());
        return ResponseDTO.success(productDTOList);
    }

    /**
     * 根据店铺id获取商品信息
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> getByStore(ProductDTO productDTO) {
        if(CommonUtil.isEmpty(productDTO.getStoreId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        ProductExample productExample = new ProductExample();
        ProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andStoreIdEqualTo(productDTO.getStoreId());
        if(!CommonUtil.isEmpty(productDTO.getName())) {
            criteria.andNameLike("%" + productDTO.getName() + "%");
        }
        // 如果进行商品状态搜索
        if(productDTO.getState() != null) {
            criteria.andStateEqualTo(productDTO.getState());
        }
        productExample.setOrderByClause("create_time desc");
        List<Product> productList = productMapper.selectByExample(productExample);
        List<ProductDTO> productDTOList = CopyUtil.copyList(productList, ProductDTO.class);
        // 封装商品分类信息
        for(ProductDTO item : productDTOList) {
            Category category = categoryMapper.selectByPrimaryKey(item.getCategoryId());
            item.setCategoryDTO(CopyUtil.copy(category, CategoryDTO.class));
        }
        return ResponseDTO.success(productDTOList);
    }

    /**
     * 获取五个成交额最高的商品分类
     * @return
     */
    @Override
    public ResponseDTO<List<CategoryDTO>> getCategoryListByPrice() {
        return ResponseDTO.success(myCategoryMapper.getCategoryListByPrice());
    }

    /**
     * 根据店铺id删除商品信息
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeProductByStoreId(ProductDTO productDTO) {
        if(CommonUtil.isEmpty(productDTO.getStoreId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andStoreIdEqualTo(productDTO.getStoreId());
        productMapper.deleteByExample(productExample);
        return ResponseDTO.successByMsg(true, "删除成功！");
    }

    /**
     * 获取同类好货商品
     * @param productDTO
     * @return
     */
    @Override
    public ResponseDTO<List<ProductDTO>> getSimilarProduct(ProductDTO productDTO) {
        if(CommonUtil.isEmpty(productDTO.getCategoryId())) {
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andCategoryIdEqualTo(productDTO.getCategoryId())
        .andStateEqualTo(ProductStateEnum.SUCCESS.getCode()).andIdNotEqualTo(productDTO.getId());
        PageHelper.startPage(1, 3);
        List<Product> productList = productMapper.selectByExample(productExample);

        return ResponseDTO.success(CopyUtil.copyList(productList, ProductDTO.class));
    }


    /**
     * 判断商品分类名称是否重复
     * @param category
     * @param id
     * @return
     */
    public Boolean isCategoryNameExist(Category category, String id) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andNameEqualTo(category.getName());
        List<Category> selectedCategoryList = categoryMapper.selectByExample(categoryExample);
        if(selectedCategoryList != null && selectedCategoryList.size() > 0) {
            if(selectedCategoryList.size() > 1){
                return true; //出现重名
            }
            if(!selectedCategoryList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }

    /**
     * 判断商品名称是否重复
     * @param product
     * @param id
     * @return
     */
    public Boolean isProductNameExist(Product product, String id) {
        ProductExample productExample = new ProductExample();
        productExample.createCriteria().andNameEqualTo(product.getName());
        List<Product> selectedProductList = productMapper.selectByExample(productExample);
        if(selectedProductList != null && selectedProductList.size() > 0) {
            if(selectedProductList.size() > 1){
                return true; //出现重名
            }
            if(!selectedProductList.get(0).getId().equals(id)) {
                return true; //出现重名
            }
        }
        return false;//没有重名
    }


}
