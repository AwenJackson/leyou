package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.mapper.*;
import com.leyou.prop.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsService {
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private SpuMapper spuMapper;
    @Resource
    private SpudetailMapper spudetailMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private BrandMapper brandMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息到mq
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    /**
     * 商品列表
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> querySpuPage(String key, Boolean saleable, Integer page, Integer rows) {
        //1.分页
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //2.处理查询
        if (StringUtils.isNotEmpty(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //3.处理选项卡（saleable）
        if (saleable!=null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //4.确保不显示被删除商品
        criteria.andEqualTo("valid",true);
        //5.处理分类与品牌
        List<Spu> spus = spuMapper.selectByExample(example);
        bindCategoryBrand(spus);
        //6.分页结果处理
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        return new PageResult<Spu>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 处理分类与品牌
     * @param spus
     */
    private void bindCategoryBrand(List<Spu> spus) {
        spus.forEach(spu -> {
            //1.查找分类对象处理分类名称
            List<Category> categories = categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            String cname = categories.stream().map(Category::getName).collect(Collectors.joining("/"));
            spu.setCname(cname);
            //2.处理品牌名称
            spu.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
        });
    }

    /**
     * 新增
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //1.新增SPU
        spu.setSaleable(spu.getSaleable());
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.insertSelective(spu);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SPU_SAVE_ERROR);
        }
        //2.新增SPU_DETAL
        Long spuId = spu.getId();
        Spudetail spudetail = spu.getSpuDetail();
        spudetail.setSpuId(spuId);
        count = spudetailMapper.insertSelective(spudetail);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SPU_DETAL_SAVE_ERROR);
        }
        //3.新增SKU
        List<Sku> skus = spu.getSkus();
        saveSkuAndStock(spuId, skus,"SAVE");
        //向mq发送新增消息
        sendMessage(spu.getId(),"insert");
    }

    /**
     * 处理新增SKU和库存
     * @param spuId
     * @param skus
     * @param type  判断是修改商品还是新增商品
     */
    private void saveSkuAndStock(Long spuId, List<Sku> skus,String type) {
        skus.forEach(sku -> {
            //1.新增sku
            sku.setSpuId(spuId);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            int result = skuMapper.insertSelective(sku);
            if (result==0) {
                if (type.equals("SAVE")) {
                    throw new LyException(ExceptionEnum.ITEM_SKU_SAVE_ERROR);
                }else {
                    throw new LyException(ExceptionEnum.ITEM_SKU_EDIT_ERROR);
                }
            }
            //2.新增stock
            Stock stock = new Stock();
            stock.setStock(sku.getStock());
            stock.setSkuId(sku.getId());
            result = stockMapper.insertSelective(stock);
            if (result==0) {
                if (type.equals("SAVE")) {
                    throw new LyException(ExceptionEnum.ITEM_STOCK_SAVE_ERROR);
                }else {
                    throw new LyException(ExceptionEnum.ITEM_STOCK_EDIT_ERROR);
                }
            }
        });
    }

    /**
     * 修改商品
     * @param spu
     */
    @Transactional
    public void editGoods(Spu spu) {
        Long spuId = spu.getId();
        //1.修改SPU
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SPU_EDIT_ERROR);
        }
        //2.修改SPU_DETAIL
        count = spudetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SPU_DETAL_EDIT_ERROR);
        }
        //3.删除SPU对应的SKU
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        count = skuMapper.delete(sku);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SKU_DELETE_ERROR);
        }
        //4.修改SKU
        List<Sku> skus = spu.getSkus();
        saveSkuAndStock(spuId,skus,"EDIT");
        //向mq发送修改消息
        sendMessage(spu.getId(),"update");
    }

    /**
     * 根据spuId查询商品详情
     * @param spuId
     * @return
     */
    public Spudetail querySpuDetailBySpuId(Long spuId) {
        return spudetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据商品id查询sku
     * @param spuId
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        bindSkuStock(skus);
        return skus;
    }

    /**
     * 处理库存
     * @param skus
     */
    private void bindSkuStock(List<Sku> skus) {
        skus.forEach(sku->{
            //查询库存
            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
    }

    /**
     * 根据spuId删除商品
     * @param spuId
     */
    public void deleteGoods(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        spu.setValid(false);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_SPU_DELETE_ERROR);
        }
    }

    /**
     * 上下架商品
     * @param spuId
     * @param saleable
     */
    public void upOrDownSaleable(Long spuId, Boolean saleable) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        spu.setSaleable(saleable);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count==0) {
            throw new LyException(ExceptionEnum.ITEM_UPORDOWN_SALEABLE_ERROR);
        }
    }

    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查找商品价格或库存是否有变化
     * @param ids
     * @return
     */
    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //处理skus
        loadStockInSku(ids, skus);
        return skus;
    }

    private void loadStockInSku(List<Long> ids, List<Sku> skus) {
        //根据sku的id，ids查询库存
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOND);
        }
        //把stock变成一个map,其key是:sku的id，值是库存值
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skus.forEach(sku -> sku.setStock(stockMap.get(sku.getId())));
    }
}
