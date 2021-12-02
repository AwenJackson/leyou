package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.prop.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    /**
     * 新增品牌与分类的对应关系
     * @param cid
     * @param bid
     * @return
     */
    @Insert("insert into tb_category_brand values(#{cid},#{bid})")
    int insertBrandCategory(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 清空品牌与分类的对应关系
     * @param bid
     * @return
     */
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteBrandCategory(Long bid);

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @Select("select * from tb_brand where id in(select brand_id from tb_category_brand where category_id = #{cid})")
    List<Brand> queryBrandByCid(Long cid);
}
