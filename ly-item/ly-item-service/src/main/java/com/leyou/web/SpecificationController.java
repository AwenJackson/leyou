package com.leyou.web;

import com.leyou.prop.SpecGroup;
import com.leyou.prop.SpecParam;
import com.leyou.service.SpecificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Resource
    private SpecificationService specificationService;

    /**
     * 根据分类Id查询分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 新增分组
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(@RequestBody SpecGroup specGroup ){
        specificationService.addGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改分组
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> editGroup(@RequestBody SpecGroup specGroup ){
        specificationService.editGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除分组
     * @param gid
     * @return
     */
    @DeleteMapping("group/{gid}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long gid){
        specificationService.deleteGroup(gid);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(specificationService.queryParamList(gid,cid,searching));
    }


    /**
     * 新增规格参数
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addParam(@RequestBody SpecParam specParam){
        specificationService.addParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格参数
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> editParam(@RequestBody SpecParam specParam){
        specificationService.editParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 删除规格参数
     * @return
     */
    @DeleteMapping("param/{pid}")
    public ResponseEntity<Void> deleteParam(@PathVariable Long pid){
        specificationService.deleteParam(pid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
