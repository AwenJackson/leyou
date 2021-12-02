package com.leyou.apis;

import com.leyou.prop.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandsApi {
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable Long id);

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("brand/ids")
    List<Brand> queryBrandByIds(@RequestParam List<Long> ids);
}
