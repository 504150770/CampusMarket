package com.yjq.programmer.dto;

import com.yjq.programmer.annotation.ValidateEntity;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2022-07-10 16:45
 */
public class AddressDTO {
    private String id;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="收货人姓名不能为空！",errorMaxLengthMsg="收货人姓名长度不能大于16！",errorMinLengthMsg="收货人姓名不能为空！")
    private String receiverName;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=128,minLength=1,errorRequiredMsg="收货地址不能为空！",errorMaxLengthMsg="收货地址长度不能大于128！",errorMinLengthMsg="收货地址不能为空！")
    private String receiverLocation;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="联系电话不能为空！",errorMaxLengthMsg="联系电话长度不能大于16！",errorMinLengthMsg="联系电话不能为空！")
    private String receiverPhone;

    @ValidateEntity(required=true, errorRequiredMsg="地址所属用户不能为空！")
    private String userId;

    private Integer isSelect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(String receiverLocation) {
        this.receiverLocation = receiverLocation;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(Integer isSelect) {
        this.isSelect = isSelect;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", receiverName=").append(receiverName);
        sb.append(", receiverLocation=").append(receiverLocation);
        sb.append(", receiverPhone=").append(receiverPhone);
        sb.append(", userId=").append(userId);
        sb.append(", isSelect=").append(isSelect);
        sb.append("]");
        return sb.toString();
    }
}
