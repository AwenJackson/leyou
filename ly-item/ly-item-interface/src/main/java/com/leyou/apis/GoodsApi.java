package com.leyou.apis;

import com.leyou.common.vo.PageResult;
import com.leyou.prop.Sku;
import com.leyou.prop.Spu;
import com.leyou.prop.Spudetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable Long id);

    /**
     * 根据商品id查询SKU
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam Long spuId);

    /**
     * 根据spuId查询商品详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    Spudetail querySpuDetailBySpuId(@PathVariable Long spuId);

    /**
     * 查询商品列表
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    PageResult<Spu> querySpuPage(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );
}
