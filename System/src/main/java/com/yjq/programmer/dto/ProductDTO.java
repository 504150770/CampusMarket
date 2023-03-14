package com.yjq.programmer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yjq.programmer.annotation.ValidateEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-06-30 18:02
 */
public class ProductDTO {
    private String id;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999.99,minValue=0,errorRequiredMsg="商品价格不能为空！",errorMaxValueMsg="商品价格不能大于99999999.99！",errorMinValueMsg="商品价格不能为小于0！")
    private BigDecimal price;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="商品名称不能为空！",errorMaxLengthMsg="商品名称长度不能大于16！",errorMinLengthMsg="商品名称不能为空！")
    private String name;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=32,minLength=1,errorRequiredMsg="商品简介不能为空！",errorMaxLengthMsg="商品简介长度不能大于32！",errorMinLengthMsg="商品简介不能为空！")
    private String info;

    private String photo;

    private String storeId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer tag;

    private Integer state;

    private String categoryId;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999,minValue=0,errorRequiredMsg="商品库存不能为空！",errorMaxValueMsg="商品库存不能大于99999999！",errorMinValueMsg="商品库存不能为小于0！")
    private Integer stock;

    private String content;

    private CategoryDTO categoryDTO;

    private StoreDTO storeDTO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CategoryDTO getCategoryDTO() {
        return categoryDTO;
    }

    public void setCategoryDTO(CategoryDTO categoryDTO) {
        this.categoryDTO = categoryDTO;
    }

    public StoreDTO getStoreDTO() {
        return storeDTO;
    }

    public void setStoreDTO(StoreDTO storeDTO) {
        this.storeDTO = storeDTO;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", price=").append(price);
        sb.append(", name=").append(name);
        sb.append(", info=").append(info);
        sb.append(", photo=").append(photo);
        sb.append(", userId=").append(storeId);
        sb.append(", createTime=").append(createTime);
        sb.append(", tag=").append(tag);
        sb.append(", state=").append(state);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", stock=").append(stock);
        sb.append(", content=").append(content);
        sb.append(", categoryDTO=").append(categoryDTO);
        sb.append(", storeDTO=").append(storeDTO);
        sb.append("]");
        return sb.toString();
    }
}
