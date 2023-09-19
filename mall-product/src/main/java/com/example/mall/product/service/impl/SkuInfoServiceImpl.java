package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.common.config.CustomThreadPoolConfiguration;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.exception.ThreadPoolException;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.product.feign.SeckillFeignClient;
import com.example.mall.product.model.dto.SkuRequestPageParams;
import com.example.mall.product.model.dto.Skus;
import com.example.mall.product.model.po.SkuImages;
import com.example.mall.product.model.po.SkuInfo;
import com.example.mall.product.mapper.SkuInfoMapper;
import com.example.mall.product.model.po.SpuInfo;
import com.example.mall.product.model.vo.SkuDetailVo;
import com.example.mall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Service
@RequiredArgsConstructor
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService, SkuInfoService.Web {
    private final SkuImagesService skuImagesService;
    private final SpuInfoDescService spuInfoDescService;
    private final AttrGroupService attrGroupService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final SeckillFeignClient seckillFeignClient;

    @Override
    @Transactional
    public void saveSkuInfo(Skus skuDto, SpuInfo spuInfo, String defaultImageUrl, SkuInfo skuInfo) {
        BeanUtils.copyProperties(skuDto, skuInfo);
        skuInfo.setBrandId(spuInfo.getBrandId());
        skuInfo.setCatalogId(spuInfo.getCatalogId());
        skuInfo.setSaleCount(0L);
        skuInfo.setSpuId(spuInfo.getId());
        skuInfo.setSkuDefaultImg(defaultImageUrl);
        this.save(skuInfo);
        log.info("skuInfo信息保存完成[{}]", skuInfo.getSkuId());
    }

    @Override
    public PageResult<SkuInfo> listWithCondition(PageRequestParams pageRequestParams, SkuRequestPageParams skuRequestPageParams) {
        if (skuRequestPageParams != null) {
            BigDecimal max = skuRequestPageParams.getMax();
            BigDecimal min = skuRequestPageParams.getMin();
            if (max == null || min == null) {
                return null;
            }
            if (max.compareTo(new BigDecimal(0)) < 0 || min.compareTo(new BigDecimal(0)) < 0) {//max < 0 || min < 0
                return null;
            }
            if (max.compareTo(min) < 0) {//max < min
                return null;
            }
            QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
            Long brandId = skuRequestPageParams.getBrandId();
            Long catelogId = skuRequestPageParams.getCatelogId();

            queryWrapper.eq(brandId != null && brandId != 0
                    , ProductConstant.SpuInfoTableNameEnum.BRAND_ID.getTableName()
                    , brandId);
            queryWrapper.eq(catelogId != null && catelogId != 0
                    , ProductConstant.SpuInfoTableNameEnum.CATEGORY_ID.getTableName()
                    , catelogId);
            if (max.compareTo(new BigDecimal(0)) > 0 && min.compareTo(new BigDecimal(0)) > 0) {//max > 0 && min > 0
                queryWrapper.le(ProductConstant.SkuInfoTableNameEnum.SKU_INFO_PRICE.getTableName()
                        , max);
                queryWrapper.ge(ProductConstant.SkuInfoTableNameEnum.SKU_INFO_PRICE.getTableName()
                        , min);
            }
            //剩下一种max == 0 && min == 0不需要添加条件
            return PageResultUtils.getPage(pageRequestParams, baseMapper, SkuInfo.class, queryWrapper);
        } else {
            return PageResultUtils.getPage(pageRequestParams, baseMapper, SkuInfo.class);
        }

    }

    @Override
    public List<SkuInfo> getSkuListBySpuId(Long spuId) {
        return this.list(new LambdaQueryWrapper<SkuInfo>()
                .eq(SkuInfo::getSpuId, spuId));
    }

    @Override
    public SkuDetailVo getDetail(Long skuId) {
        SkuDetailVo result = new SkuDetailVo();
        //sku(商品库存单位)基本信息获取 pms_sku_info
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = this.getById(skuId);
            result.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);


        //sku的图片信息 pms_sku_images
        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImages> skuImagesList = skuImagesService.list(new LambdaQueryWrapper<SkuImages>()
                    .eq(SkuImages::getSkuId, skuId));
            result.setSkuImages(skuImagesList);
        }, threadPoolExecutor);

        //获取spu(商品标准化产品单元)的销售信息集合
        CompletableFuture<Void> skuSaleAttrFuture = skuInfoFuture.thenAcceptAsync(
                skuInfo -> result.setSaleAttr(skuSaleAttrValueService.getSaleAttrsBySpuId(skuInfo.getSpuId())),
                threadPoolExecutor);

        //获取spu介绍
        CompletableFuture<Void> spuDescFuture = skuInfoFuture.thenAcceptAsync(
                skuInfo -> result.setSpuInfoDesc(spuInfoDescService.getById(skuInfo.getSpuId())),
                threadPoolExecutor);

        //获取spu的规格参数
        CompletableFuture<Void> spuAttrFuture = skuInfoFuture.thenAcceptAsync(
                skuInfo -> result.setGroupAttrs(attrGroupService.getAttrAndAttrGroupBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId())),
                threadPoolExecutor);

        //获取秒杀活动的信息
        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            Result<SeckillSessionTo.SeckillSkuRelationTo> feignResult = seckillFeignClient.getSkuSeckillInfo(skuId);
            if (feignResult != null && feignResult.isSuccess()) {
                SeckillSessionTo.SeckillSkuRelationTo skuRelationTo = feignResult.castData(SeckillSessionTo.SeckillSkuRelationTo.class);
                if (skuRelationTo == null) {
                    return;
                }
                result.setSeckillInfo(this.buildSeckillInfo(feignResult, skuRelationTo));
            }
        });

        try {
            CompletableFuture.allOf(skuInfoFuture, spuDescFuture, skuImagesFuture, skuSaleAttrFuture, spuAttrFuture,seckillFuture).get();
        } catch (Exception e) {
            ThreadPoolException.error("商品服务中线程执行异常->" + e.getMessage(), e.getCause());
        }
        return result;
    }

    private SkuDetailVo.SeckillInfo buildSeckillInfo(Result<SeckillSessionTo.SeckillSkuRelationTo> feignResult, SeckillSessionTo.SeckillSkuRelationTo skuRelationTo) {
        SkuDetailVo.SeckillInfo seckillInfo = new SkuDetailVo.SeckillInfo();
        seckillInfo.setLimit(skuRelationTo.getSeckillLimit());
        seckillInfo.setRandomCode(skuRelationTo.getRandomCode());
        seckillInfo.setPrompt(feignResult.getMsg());
        seckillInfo.setStartTime(skuRelationTo.getStartTime().toString().replace("T"," "));
        seckillInfo.setEndTime(skuRelationTo.getEndTime().toString().replace("T"," "));
        seckillInfo.setSeckillPrice(skuRelationTo.getSeckillPrice());
        seckillInfo.setPromotionSessionId(skuRelationTo.getPromotionSessionId());
        return seckillInfo;
    }
}
