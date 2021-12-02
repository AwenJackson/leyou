package com.leyou.service;

import com.leyou.prop.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 获取商品分类信息
     * @param pid
     * @return
     */
    public List<Category> queryAllCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);
    }

    /**
     * 根据品牌id查询分类信息
     * @param bid
     * @return
     */
    public List<Category> queryCategoryByBid(Long bid) {
        //1.查找品牌对应的分类id
        List<Long> cids = categoryMapper.selectCategoryByBrand(bid);
        //2.根据分类id查询分类信息
//        List<Category> categories = new ArrayList<>();
//        for (Long cid : cids) {
//            Category category = categoryMapper.selectByPrimaryKey(cid);
//            categories.add(category);
//        }
//        return categories;
        //根据主键id批量性查找
        return categoryMapper.selectByIdList(cids);
    }

    public List<Category> queryCategoryByIds(List<Long> ids) {
        return categoryMapper.selectByIdList(ids);
    }
}
