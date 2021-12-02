package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.prop.*;
import com.leyou.repositorys.GoodsRepository;
import com.leyou.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 索引库的转化 以及后期全文检索
 */
@Service
public class GoodsService {

    @Resource
    private BrandClient brandClient;

    @Resource
    private CategoryClient categoryClient;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SpecificationClient specificationClient;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private GoodsRepository goodsRepository;

    /**
     * 将Spu转化成索引库docs
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();
        //处理all     = 标题 分类名称 品牌名称
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        List<Category> categoryList = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        String cnames = categoryList.stream().map(Category::getName).collect(Collectors.joining(" "));
        String all = spu.getTitle()+" "+brand.getName()+" "+cnames;
        //查询sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        //处理sku
        List<Map<String,Object>> skuList = new  ArrayList<>();
        //价格集合
        Set<Long> prices = new HashSet<>();
        for (Sku sku : skus) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images", StringUtils.substringBefore(sku.getImages(),","));
            skuList.add(map);
            //处理价格
            prices.add(sku.getPrice());
        }
        //处理spec
        List<SpecParam> specParams = specificationClient.queryParamList(null, spu.getCid3(), true);
        //查询商品详情
        Spudetail spudetail = goodsClient.querySpuDetailBySpuId(spuId);
        //获取通用规格参数
       Map<Long,String> genericSpec = JsonUtils.toMap(spudetail.getGenericSpec(),Long.class,String.class);
        //获取特有规格参数
        Map<Long,List<String>> specialSpec = JsonUtils.nativeRead(spudetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {});
        //处理参数
        Map<String,Object> specs = new HashMap<>();
        for (SpecParam param : specParams) {
            //规格名称
            String key = param.getName();
            Object value = "";
            //判断是否是通用规格
            if (param.getGeneric()){
                value = genericSpec.get(param.getId());
                //判断是否是数值类型
                if(param.getNumeric()){
                    //处理成段
                    value = chooseSegment(value.toString(), param);
                }
            }else {
                value = specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key,value);
        }
        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setAll(all);
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(new Date());
        goods.setPrice(prices);
        goods.setSkus(JsonUtils.toString(skuList));
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 处理成段
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        //保存数值段
        for (String segment : p.getSegments().split(",")){
            String[] segs = segment.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end){
                if (segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if (begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 实现搜索过滤
     * @param request
     * @return
     */
    public SearchResult querySearch(SearchRequest request) {
        String key = request.getKey();
        // 判断搜索条件是否为空，如果没有，直接返回null,不允许搜索全部商品
        if(StringUtils.isEmpty(key)){
            return null;
        }
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //处理搜索关键词 key
//        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("all", key);
//        queryBuilder.withQuery(matchQuery);
        //基本查询
        QueryBuilder basicQuery = buildBasicQuery(request);
        queryBuilder.withQuery(basicQuery);     //其中包含处理搜索关键词 key
        //处理分页
        Integer page = request.getPage() - 1;       //分页从0开始
        Integer rows = request.getSize();           //每页条数
        queryBuilder.withPageable(PageRequest.of(page,rows));
        //实现排序
        if (StringUtils.isNotEmpty(request.getSortBy())) {
            queryBuilder.withSort(SortBuilders.fieldSort(request.getSortBy()).order(
                    request.getDescending()? SortOrder.DESC:SortOrder.ASC
            ));
        }
        /*聚合品牌和分类*/
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //查询
        AggregatedPage<Goods> goodsAggregatedPage = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //解析分页结果
        long totalElements = goodsAggregatedPage.getTotalElements();            //查询结果数量（总条数）
        int totalPages = goodsAggregatedPage.getTotalPages();       //总页数
        List<Goods> content = goodsAggregatedPage.getContent();         //当前页面数据
        //解析聚合结果
        Aggregations aggregations = goodsAggregatedPage.getAggregations();
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));
        List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
        //根据商品分类判断是否需要商品聚合
        List<Map<String,Object>> specs = null;
        if (categories.size()==1){
            //如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = getSpec(categories.get(0).getId(),basicQuery);
        }
        return new SearchResult(totalElements,totalPages,content,categories,brands,specs);
    }

    /**
     * 拼接搜索条件
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建组合查询（布尔查询）
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //处理搜索关键词 key
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("all", request.getKey());
//        queryBuilder.withQuery(matchQuery);
        queryBuilder.must(matchQuery);
        Map<String, Object> map = request.getFilter();
        map.forEach((k,v)->{
            // 处理key
            if (!"cid3".equals(k) && !"brandId".equals(k)) {
                //规格参数
                k = "specs." + k + ".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(k, v));
        });
        /*for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            // 处理key
            if (!"cid3".equals(key) && !"brandId".equals(key)) {
                key = "specs." + key + ".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key, value));
        }*/
        return queryBuilder;
    }

    private List<Map<String, Object>> getSpec(Long cid, QueryBuilder matchQuery) {
        List<Map<String,Object>> specs = new ArrayList<>();
        List<SpecParam> specParams = specificationClient.queryParamList(null, cid, true);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //1.加入搜索条件
        queryBuilder.withQuery(matchQuery);
        //拼接规格参数条件
        for (SpecParam specParam : specParams) {
            String key = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs."+key+".keyword"));
        }
        //查询结果
        Map<String, Aggregation> aggregationMap = elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations).asMap();
        for (SpecParam specParam : specParams) {
            Map<String,Object> map = new HashMap<>();
            String key = specParam.getName();
            StringTerms stringTerms = (StringTerms) aggregationMap.get(key);
            List<String> options = stringTerms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            map.put("k",key);
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    private List<Category> parseCategoryAgg(LongTerms longTerms) {
        List<Long> cids = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        return categoryClient.queryCategoryByIds(cids);
    }

    private List<Brand> parseBrandAgg(LongTerms longTerms) {
        List<Long> ids = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        return brandClient.queryBrandByIds(ids);
    }

    /**
     * 处理消息 , 对索引库实现新增或修改
     *通过mq获取消息，根据spuId查询商品，将修改或新增的商品存入索引库
     * @param spuId
     */
    public void listenInsertOrUpdate(Long spuId) {
        // 查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        Goods goods = buildGoods(spu);
        // 存入索引库
        goodsRepository.save(goods);
    }

    /**
     * 处理消息 , 对索引库实现删除
     * 通过mq获取消息,即spuId，根据spuId删除商品
     * @param spuId
     */
    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
