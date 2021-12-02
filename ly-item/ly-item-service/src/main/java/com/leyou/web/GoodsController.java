package com.leyou.web;

import com.leyou.prop.Sku;
import com.leyou.prop.Spu;
import com.leyou.prop.Spudetail;
import com.leyou.common.vo.PageResult;
import com.leyou.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    /**
     * 商品列表
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
            ){
        return ResponseEntity.ok(goodsService.querySpuPage(key,saleable,page,rows));
    }

    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuId查询商品详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<Spudetail> querySpuDetailBySpuId(@PathVariable Long spuId){
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable Long id){
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 根据商品id查询SKU
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam Long spuId){
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }

    /**
     * 修改商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> editGoods(@RequestBody Spu spu){
        goodsService.editGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuId删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/{spuId}")
    public ResponseEntity<Void> deleteGoods(@PathVariable Long spuId){
        goodsService.deleteGoods(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("spu/{spuId}/saleable/{saleable}")
    public ResponseEntity<Void> upOrDownSaleable(@PathVariable Long spuId,@PathVariable Boolean saleable){
        goodsService.upOrDownSaleable(spuId,saleable);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 查找商品价格或库存是否有变化
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }
}
