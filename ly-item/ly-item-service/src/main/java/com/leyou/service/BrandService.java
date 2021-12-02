package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.prop.Brand;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BrandService {
    @Resource
    private BrandMapper brandMapper;

    /**
     * 查询品牌列表
     * @param page
     * @param rows
     * @param key
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String key, String sortBy, Boolean desc) {
        //1.分页
        PageHelper.startPage(page,rows);
        //初始化样例查询操作
        Example example = new Example(Brand.class);
        //初始化条件组合器
        Example.Criteria criteria = example.createCriteria();
        //2.key值条件（名称或者首字母）
        if (StringUtils.isNotEmpty(key)){
            criteria.orLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //3.排序
        if (StringUtils.isNotEmpty(sortBy)){
            example.setOrderByClause(sortBy+(desc?" DESC":" ASC"));
        }
        //查询
        List<Brand> brandList = brandMapper.selectByExample(example);
        //4.处理分页
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 添加品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //1.新增品牌
        int count = brandMapper.insert(brand);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //2.处理品牌与分类的关系
        for (Long cid : cids) {
            int result = brandMapper.insertBrandCategory(cid, brand.getId());
            if (result==0){
                throw new LyException(ExceptionEnum.BRAND_CATEGORY_SAVE_ERROR);
            }
        }
    }

    @Transactional
    public void editBrand(Brand brand, List<Long> cids) {
        //1.新增品牌(不为空就修改，为空就不修改)
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        //2.删除之前的品牌与分类的关系
        count= brandMapper.deleteBrandCategory(brand.getId());
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_CATEGORY_DELETE_ERROR);
        }
        //3.处理品牌与分类的关系
        for (Long cid : cids) {
            int result = brandMapper.insertBrandCategory(cid, brand.getId());
            if (result==0){
                throw new LyException(ExceptionEnum.BRAND_CATEGORY_SAVE_ERROR);
            }
        }
    }

    @Transactional
    public void deleteBrand(Long bid) {
        //1.删除之前的品牌与分类的关系
        int count= brandMapper.deleteBrandCategory(bid);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND_CATEGORY_DELETE_ERROR);
        }
        //2.删除品牌
        count = brandMapper.deleteByPrimaryKey(bid);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND__DELETE_ERROR);
        }
    }

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCategoryId(Long cid) {
        List<Brand> brandList = brandMapper.queryBrandByCid(cid);
        return brandList;
    }

    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return brandMapper.selectByIdList(ids);
    }
}
