package com.leyou.service;

import com.leyou.prop.SpecGroup;
import com.leyou.prop.SpecParam;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecificationService {
    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(specGroups)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOND);
        }
        //绑定每一组的参数
        bindParams(specGroups);
        return specGroups;
    }

    private void bindParams(List<SpecGroup> specGroups) {
        specGroups.forEach(g->{
            SpecParam specParam = new SpecParam();
            specParam.setGroupId(g.getId());
            g.setParams(specParamMapper.select(specParam));
        });
    }

    /**
     * 根据gid查询分组
     * 根据分类cid查询规格参数
     * 当gid或cid或searching中的任何一个不为空的时候，就根据不为空的那个数据进行条件查询
     * @param gid
     * @param searching
     * @param cid
     * @return
     */
    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        if (searching != null){
            specParam.setSearching(searching);
        }
        List<SpecParam> specParams = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParams)) {
            throw new LyException(ExceptionEnum.PARAMS_GROUP_NOT_ERROR);
        }
        return specParams;
    }

    @Transactional
    public void addGroup(SpecGroup specGroup) {
        int count = specGroupMapper.insert(specGroup);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_SAVE_ERROR);
        }
    }

    @Transactional
    public void editGroup(SpecGroup specGroup) {
        int count = specGroupMapper.updateByPrimaryKeySelective(specGroup);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_EDIT_ERROR);
        }
    }

    @Transactional
    public void deleteGroup(Long gid) {
        //1.删除规格参数
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> select = specParamMapper.select(specParam);
        if (!CollectionUtils.isEmpty(select)){
            int delete = specParamMapper.delete(specParam);
            if (delete==0){
                throw new LyException(ExceptionEnum.SPEC_PARAM_DELETE_ERROR);
            }
        }
        //2.删除分组
        int count = specGroupMapper.deleteByPrimaryKey(gid);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_DELETE_ERROR);
        }
    }

    @Transactional
    public void addParam(SpecParam specParam) {
        int count = specParamMapper.insert(specParam);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_SAVE_ERROR);
        }
    }

    @Transactional
    public void editParam(SpecParam specParam) {
        int count = specParamMapper.updateByPrimaryKeySelective(specParam);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_EDIT_ERROR);
        }
    }

    @Transactional
    public void deleteParam(Long pid) {
        int count = specParamMapper.deleteByPrimaryKey(pid);
        if (count==0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_DELETE_ERROR);
        }
    }

}
