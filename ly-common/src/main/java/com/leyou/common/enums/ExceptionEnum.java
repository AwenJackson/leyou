package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {
    BRAND_SAVE_ERROR(500,"品牌新增失败"),
    BRAND_EDIT_ERROR(500,"品牌修改失败"),
    BRAND__DELETE_ERROR(500,"品牌删除失败"),
    BRAND_CATEGORY_SAVE_ERROR(500,"品牌与分类关系新增失败"),
    BRAND_CATEGORY_DELETE_ERROR(500,"品牌与分类关系删除失败"),
    UPLOAD_IMAGE_TYPE_ERROR(400,"图片上传格式不正确"),
    UPLOAD_IMAGE_CTX_ERROR(400,"图片上传内容不正确"),
    UPLOAD_IMAGE_ERROR(400,"图片上传失败"),
    SPEC_GROUP_NOT_FOND(500,"未找到任何配置信息"),
    SPEC_GROUP_SAVE_ERROR(500,"新增分组失败"),
    SPEC_GROUP_EDIT_ERROR(500,"修改分组失败"),
    SPEC_GROUP_DELETE_ERROR(500,"删除分组失败"),
    PARAMS_GROUP_NOT_ERROR(500,"未找到任何规格参数信息"),
    SPEC_PARAM_SAVE_ERROR(500,"新增规格参数失败"),
    SPEC_PARAM_EDIT_ERROR(500,"修改规格参数失败"),
    SPEC_PARAM_DELETE_ERROR(500,"删除规格参数失败"),
    ITEM_SPU_SAVE_ERROR(500,"新增SPU失败"),
    ITEM_SPU_DETAL_SAVE_ERROR(500,"新增SPU_DETAL失败"),
    ITEM_SKU_SAVE_ERROR(500,"新增SKU失败"),
    ITEM_STOCK_SAVE_ERROR(500,"新增STOCK失败"),
    ITEM_SPU_EDIT_ERROR(500,"修改SPU失败"),
    ITEM_SPU_DETAL_EDIT_ERROR(500,"修改SPU_DETAL失败"),
    ITEM_SKU_EDIT_ERROR(500,"修改SKU失败"),
    ITEM_STOCK_EDIT_ERROR(500,"修改STOCK失败"),
    ITEM_SKU_DELETE_ERROR(500,"删除SKU失败"),
    ITEM_SPU_DELETE_ERROR(500,"删除SPU失败"),
    ITEM_UPORDOWN_SALEABLE_ERROR(500,"上架或下架商品失败"),
    INVALID_USER_DATA_TYPE(400,"手机号、用户名的唯一性校验失败"),
    INVALID_VERIFY_CODE(400,"验证码不正确"),
    BRAND__DELETE_CODE_ERROR(400,"删除验证码失败"),
    USER_USERNAME_NOTFOUND_ERROR(400,"用户不存在"),
    USER_PASSWORD_ERROR(400,"密码错误"),
    UNAUTHORIZED(403,"未登录"),
    GOODS_NOT_FOND(403,"未找到该商品"),
    GOODS_SKU_NOT_FOND(403,"该商品没有库存了"),
    ;
    private int code;
    private String msg;

 }
