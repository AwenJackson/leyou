package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.utils.ThreadUtils;
import com.leyou.prop.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class PageService {

    @Resource
    private BrandClient brandClient;

    @Resource
    private CategoryClient categoryClient;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SpecificationClient specificationClient;

    @Value("${ly.page.destPath}")
    private String destPath;

    @Resource
    private TemplateEngine templateEngine;

    /**
     * 根据id（spuId）商品详情页显示
     * @param id
     * @return
     */
    public Map<String, Object> itemPage(Long id) {
        Map<String,Object> params = new HashMap<>();
        //0.查询商品
        Spu spu = goodsClient.querySpuById(id);
        //1.查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //2.查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //3.商品详情
        Spudetail spudetail = goodsClient.querySpuDetailBySpuId(id);
        //4.规格参数和规格组
        List<SpecGroup> specGroups = specificationClient.queryGroupByCid(spu.getCid3());
        //5.根据商品id查询skus
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        params.put("categories",categories);
        params.put("brand",brand);
        params.put("spu",spu);
        params.put("detail",spudetail);
        params.put("skus",skus);
        params.put("specs",specGroups);
        return params;
    }

    /**
     * 静态页面的生成
     * @param id
     */
    private void createHtml(Long id){
        try {
            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(this.itemPage(id));
            //创建输出流
            File file = new File(destPath , id + ".html");
            //修改，先删除在添加
            if(file.exists()){
                file.delete();
            }
            // 生成模板
            templateEngine.process("item",context,new FileWriter(file));
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("[静态页服务]生成静态页异常!", e);
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
    }

    public void deleteHtml(Long spuId) {
        File dest = new File(destPath + spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
    }
}
