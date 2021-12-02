package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.prop.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("select category_id from tb_category_brand where brand_id = #{bid}")
    List<Long> selectCategoryByBrand(Long bid);
}
