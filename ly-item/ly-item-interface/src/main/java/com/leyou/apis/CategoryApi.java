package com.leyou.apis;

import com.leyou.prop.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    @GetMapping("category/list/{ids}")
    List<Category> queryCategoryByIds(@RequestParam List<Long> ids);
}
