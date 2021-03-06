package com.leyou.vo;

import com.leyou.common.vo.PageResult;
import com.leyou.prop.Brand;
import com.leyou.prop.Category;
import com.leyou.prop.Goods;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;  // 分类过滤条件

    private List<Brand> brands;     // 品牌过滤条件

    private List<Map<String,Object>> specs;     // 规格参数过滤条件

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
