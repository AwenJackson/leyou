package com.leyou.apis;

import com.leyou.prop.SpecGroup;
import com.leyou.prop.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {

    /**
     * 根据分类Id查询分组
     * @param cid
     * @return
     */
    @GetMapping("spec/groups/{cid}")
    List<SpecGroup> queryGroupByCid(@PathVariable Long cid);

    /**
     * 根据gid查询分组
     * 根据分类cid查询规格参数
     * 当gid或cid或searching中的任何一个不为空的时候，就根据不为空的那个数据进行条件查询
     * @param gid
     * @param searching
     * @param cid
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );
}
