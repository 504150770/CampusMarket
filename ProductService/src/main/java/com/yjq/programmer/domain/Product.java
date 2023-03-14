package com.yjq.programmer.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Product {
    private String id;

    private BigDecimal price;

    private String name;

    private String info;

    private String photo;

    private String content;

    private String storeId;

    private Date createTime;

    private Integer tag;

    private Integer state;

    private String categoryId;

    private Integer stock;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        sb.append(", content=").append(content);
        sb.append(", storeId=").append(storeId);
        sb.append(", createTime=").append(createTime);
        sb.append(", tag=").append(tag);
        sb.append(", state=").append(state);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", stock=").append(stock);
        sb.append("]");
        return sb.toString();
    }
}