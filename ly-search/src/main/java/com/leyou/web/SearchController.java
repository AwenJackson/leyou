package com.leyou.web;

import com.leyou.common.vo.PageResult;
import com.leyou.prop.Goods;
import com.leyou.prop.SearchRequest;
import com.leyou.service.GoodsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SearchController {
    @Resource
    private GoodsService goodsService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> querySearch(@RequestBody SearchRequest request){
        return ResponseEntity.ok(goodsService.querySearch(request));
    }
}
