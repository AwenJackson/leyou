package com.leyou.web;

import com.leyou.prop.Brand;
import com.leyou.common.vo.PageResult;
import com.leyou.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Resource
    private BrandService brandService;

    /**
     * 品牌列表查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key",required = false) String key,       //搜索条件（首字母或者名称模糊）
            @RequestParam(value = "page",defaultValue = "1") Integer page,      //当前页数
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,          //每页条数
            @RequestParam(value = "sortBy",required = false) String sortBy,         //排序字段
            @RequestParam(value = "desc",required = false) Boolean desc             //是否降序
    ){
        return ResponseEntity.ok(brandService.queryBrandByPage(page,rows,key,sortBy,desc));
    }

    /**
     * 品牌新增
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam List<Long> cids){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 品牌修改
     * @param brand
     * @param cids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> editBrand(Brand brand, @RequestParam List<Long> cids){
        brandService.editBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 品牌删除
     * @param bid
     * @return
     */
    @DeleteMapping("{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long bid){
        brandService.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable Long id){
        return ResponseEntity.ok(brandService.queryBrandById(id));
    }

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("ids")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam List<Long> ids){
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }

    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategoryId(@PathVariable Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCategoryId(cid));
    }
}
