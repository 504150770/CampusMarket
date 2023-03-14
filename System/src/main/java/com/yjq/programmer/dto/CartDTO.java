package com.yjq.programmer.dto;

import com.yjq.programmer.annotation.ValidateEntity;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-05 11:40
 */
public class CartDTO {

    private String id;

    @ValidateEntity(required=true, errorRequiredMsg="购买商品不能为空！")
    private String productId;

    private ProductDTO productDTO;

    @ValidateEntity(required=true, errorRequiredMsg="购买用户不能为空！")
    private String userId;

    @ValidateEntity(required=true, errorRequiredMsg="购买数量不能为空！")
    private Integer quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", userId=").append(userId);
        sb.append(", quantity=").append(quantity);
        sb.append(", productDTO=").append(productDTO);
        sb.append("]");
        return sb.toString();
    }
}
