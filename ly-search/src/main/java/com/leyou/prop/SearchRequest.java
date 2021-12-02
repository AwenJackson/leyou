package com.leyou.prop;

import lombok.Data;

import java.util.Map;

@Data
public class SearchRequest {
    private String key; //搜索字段
    private Integer page; // 当前页
    private Map<String,Object> filter; //过滤字段(接收过滤属性)
    private Boolean descending;  // 排序
    private String sortBy; // 排序字段

    private static final  int DEFAULT_PAGE =1;// 默认页
    private static final int DEFAULT_SIZE = 20; // 每页大小,不从页面接受,而是固定大小

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        //获取页码时做一些效验,不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
