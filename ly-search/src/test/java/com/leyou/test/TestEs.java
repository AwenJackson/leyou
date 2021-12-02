package com.leyou.test;

import com.leyou.client.GoodsClient;
import com.leyou.common.vo.PageResult;
import com.leyou.prop.Goods;
import com.leyou.prop.Spu;
import com.leyou.repositorys.GoodsRepository;
import com.leyou.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestEs {
    @Resource
    private GoodsRepository goodsRepository;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private GoodsService goodsService;
    @Test
    public void test_create() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void test_data() {
        int page = 1;
        int rows = 20;
        int count = 0;
        do {
            //查询商品信息
            PageResult<Spu> spuPage = goodsClient.querySpuPage(null, true, page, rows);
            List<Spu> spuList = spuPage.getItems();
//            spuList.forEach(System.out::println);
            //添加到索引库
            goodsRepository.saveAll(spuList.stream().map(goodsService::buildGoods).collect(Collectors.toList()));
//            for (Spu spu : spuList) {
//               Goods goods =  goodsService.buildGoods(spu);
//               //添加到索引库
//               goodsRepository.save(goods);
//            }
            page++;
            count =spuList.size();
        }while (count==20);

    }
}
