package com.leyou.web;

import com.leyou.prop.Category;
import com.leyou.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 获取商品分类信息
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryAllCategory(@RequestParam Long pid){
        return ResponseEntity.ok(categoryService.queryAllCategoryByPid(pid));
    }

    /**
     *
     * @param ids
     * @return
     */
    @GetMapping("list/{ids}")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam List<Long> ids){
        return ResponseEntity.ok(categoryService.queryCategoryByIds(ids));
    }

    /**
     * 根据品牌id查询分类信息
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryByBid(@PathVariable Long bid){
        return ResponseEntity.ok(categoryService.queryCategoryByBid(bid));
    }
}
