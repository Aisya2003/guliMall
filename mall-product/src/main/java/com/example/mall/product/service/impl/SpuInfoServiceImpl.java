package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.GetItemWeightTo;
import com.example.mall.common.model.to.SkuFullReductionTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import com.example.mall.common.model.to.SpuBoundsTo;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.product.feign.CouponFeignClient;
import com.example.mall.product.feign.SearchFeignClient;
import com.example.mall.product.feign.WareFeignClient;
import com.example.mall.product.model.dto.*;
import com.example.mall.product.model.po.*;
import com.example.mall.product.mapper.SpuInfoMapper;
import com.example.mall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService {
    private final SpuInfoDescService spuInfoDescService;
    private final SpuImagesService spuImagesService;
    private final ProductAttrValueService productAttrValueService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final AttrService attrService;
    private final CouponFeignClient couponFeignClient;
    private final WareFeignClient wareFeignClient;
    private final SearchFeignClient searchFeignClient;

    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService, SpuImagesService spuImagesService, ProductAttrValueService productAttrValueService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService, CouponFeignClient couponFeignClient, WareFeignClient wareFeignClient, BrandService brandService, CategoryService categoryService, AttrService attrService, SearchFeignClient searchFeignClient) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.productAttrValueService = productAttrValueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.couponFeignClient = couponFeignClient;
        this.wareFeignClient = wareFeignClient;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.attrService = attrService;
        this.searchFeignClient = searchFeignClient;
    }


    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveDto dto) {
        if (dto == null) {
            log.info("spu保存信息时提供的参数为空[{}]", dto);
            return;
        }
        log.info("开始保存spu保存信息[{}]", dto);
        Boolean spuInfoResult = null;
        //1.保存商品的基本信息(pms_spu_info)
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(dto, spuInfo);
        spuInfo.setCreateTime(LocalDateTime.now());
        spuInfo.setUpdateTime(LocalDateTime.now());
        spuInfoResult = this.save(spuInfo);
        log.info("spu的基本信息保存完成[{}]", spuInfo.getId());

        Long spuInfoId = spuInfo.getId();

        //2.保存spu的描述图片(pms_spu_info_desc)
        String imagesDescUrl = dto.getDecript()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.joining(","));
        log.info("开始保存Spu的描述图片信息，spuId[{}]", spuInfoId);
        spuInfoDescService.saveSpuImagesInfoDescBySpuInfoId(spuInfoId, imagesDescUrl);

        //3.保存spu的图片集(pms_spu_images)
        List<String> imagesUrl = dto.getImages()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.info("开始保存Spu的图片集信息，spuId[{}]", spuInfoId);
        spuImagesService.saveImagesBySpuInfoId(spuInfoId, imagesUrl);

        //4.保存spu的规格参数（pms_product_attr_value)
        List<BaseAttrs> baseAttrs = dto.getBaseAttrs();
        log.info("开始保存spu的规格参数[{}]", spuInfoId);
        productAttrValueService.saveBaseAttrs(spuInfoId, baseAttrs);

        //5.保存积分信息(sms_spu_bounds)
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(dto.getBounds(), spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoId);
        log.info("开始远程调用spuCoupon服务保存积分信息[{}]", spuBoundsTo);
        if (couponFeignClient.saveSpuBoundsInfo(spuBoundsTo) != null) {
            log.info("远程调用spuCoupon服务保存积分信息成功[{}]", spuBoundsTo.getSpuId());
        } else {
            throw new RuntimeException("远程调用spuCoupon服务保存积分信息失败，事务开始回滚");
        }

        //6.保存spu的sku信息
        List<Skus> skus = dto.getSkus();
        //找到默认图片
        if (skus != null && !skus.isEmpty()) {
            skus.forEach(skuDto -> {
                String defaultImageUrl = null;
                List<Images> images = skuDto.getImages();
                for (Images image : images) {
                    if (image.getDefaultImg() == 1) {
                        defaultImageUrl = image.getImgUrl();
                        break;
                    }
                }
                //6.1保存sku的基本信息(pms_sku_info)
                SkuInfo skuInfo = new SkuInfo();
                log.info("开始保存SkuInfo信息[{}]", skuDto);
                skuInfoService.saveSkuInfo(skuDto, spuInfo, defaultImageUrl, skuInfo);
                //设置每张图片的属性
                Long skuId = skuInfo.getSkuId();
                List<SkuImages> skuImagesList = images.stream()
                        .map(img -> {
                            SkuImages skuImages = new SkuImages();
                            skuImages.setSkuId(skuId);
                            skuImages.setDefaultImg(img.getDefaultImg());
                            skuImages.setImgUrl(img.getImgUrl());
                            return skuImages;
                        })
                        .filter(skuImages -> StringUtils.isNotBlank(skuImages.getImgUrl()))
                        .collect(Collectors.toList());
                //6.2保存sku的图片信息(pms_sku_images)
                //一个sku可以对应多张图片
                log.info("开始保存sku的images信息[{}]", skuId);
                skuImagesService.saveBatch(skuImagesList);
                log.info("sku的image信息保存完成[{}]", skuImagesList);


                //6.3保存sku的销售属性信息(pms_sku_sale_attr_value)
                List<SkuSaleAttrValue> saleAttrValues = skuDto.getAttr().stream()
                        .map(attr -> {
                            SkuSaleAttrValue saleAttrValue = new SkuSaleAttrValue();
                            BeanUtils.copyProperties(attr, saleAttrValue);
                            saleAttrValue.setSkuId(skuId);
                            return saleAttrValue;
                        })
                        .collect(Collectors.toList());

                log.info("开始保存sku的销售属性sale_attr_value信息[{}]", skuId);
                skuSaleAttrValueService.saveBatch(saleAttrValues);
                log.info("sku的销售属性sale_attr_value信息保存完成[{}]", skuId);

                //6.4保存sku的优惠、满减信息
                SkuFullReductionTo skuFullReductionTo = new SkuFullReductionTo();
                BeanUtils.copyProperties(skuDto, skuFullReductionTo);
                skuFullReductionTo.setSkuId(skuId);

                //判断满减信息合法性
                if (skuFullReductionTo.getFullCount() > 0 ||
                        skuFullReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    log.info("开始远程调用spuCoupon服务保存满减信息");
                    if (couponFeignClient.saveSkuFullReductionInfo(skuFullReductionTo) != null) {
                        log.info("远程调用spuCoupon服务保存满减信息成功");
                    } else {
                        throw new RuntimeException("远程调用spuCoupon服务保存满减信息息失败，事务开始回滚");
                    }
                }
            });
            log.info("保存Spu信息和Sku信息结束[{}]", spuInfoId);
        }
    }

    @Override
    public PageResult<SpuInfo> listWithConditions(PageRequestParams pageRequestParams, SpuRequestPageParams spuRequestPageParams) {
        if (spuRequestPageParams != null) {
            QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
            Long status = spuRequestPageParams.getStatus();
            Long brandId = spuRequestPageParams.getBrandId();
            Long catelogId = spuRequestPageParams.getCatelogId();

            queryWrapper.eq(status != null && status != 0
                    , ProductConstant.SpuInfoTableNameEnum.PUBLISH_STATUS.getTableName()
                    , status);
            queryWrapper.eq(brandId != null && brandId != 0
                    , ProductConstant.SpuInfoTableNameEnum.BRAND_ID.getTableName()
                    , brandId);
            queryWrapper.eq(catelogId != null && catelogId != 0
                    , ProductConstant.SpuInfoTableNameEnum.CATEGORY_ID.getTableName()
                    , catelogId);

            return PageResultUtils.getPage(pageRequestParams, baseMapper, SpuInfo.class, queryWrapper);
        } else {
            return PageResultUtils.getPage(pageRequestParams, baseMapper, SpuInfo.class);
        }
    }

    @Override
    public void upLoadToES(Long spuId) {
        //查询Sku所有可以被检索的规格属性
        //获取商品相关属性
        List<ProductAttrValue> attrs = productAttrValueService.getBaseSpuList(spuId);
        //获取属性对应id
        List<Long> attrIds = attrs
                .stream()
                .map(ProductAttrValue::getAttrId)
                .collect(Collectors.toList());
        //手机可检索的attr的id
        Set<Long> searchableIds = new HashSet<>(attrService.getSearchableAttrIdList(attrIds));
        //过滤属性的不可检索项
        List<ProductAttrValue> searchableAttrs = attrs.stream()
                .filter((productAttrValue) -> searchableIds.contains(productAttrValue.getAttrId()))
                .collect(Collectors.toList());
        //映射为To需要的属性
        List<SkuUploadESTo.Attrs> esAttrs = searchableAttrs.stream()
                .map(productAttrValue -> {
                    SkuUploadESTo.Attrs esAttr = new SkuUploadESTo.Attrs();
                    BeanUtils.copyProperties(productAttrValue, esAttr);
                    return esAttr;
                })
                .collect(Collectors.toList());
        //远程调用查询是否存在库存
        List<SkuInfo> skuInfoList = skuInfoService.getSkuListBySpuId(spuId);
        List<Long> skuIds = skuInfoList.stream()
                .map(SkuInfo::getSkuId)
                .collect(Collectors.toList());
        Optional<List<SkuHasStockTo>> result;
        try {
            result = Optional.ofNullable(wareFeignClient.hasStock(skuIds));
        } catch (Exception e) {
            log.error("远程调用查询库存处发生异常,[{}]", e.getMessage());
            return;
        }
        Map<Long, Boolean> hasStockMap = null;
        if (result.isPresent()) {
            hasStockMap = result.get()
                    .stream()
                    .collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }
        //封装成上传到ES的属性
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        hasStockMap = null;
        List<SkuUploadESTo> skuUploadESToList = skuInfoList
                .stream()
                .map(skuInfo -> {
                    SkuUploadESTo skuUploadESTo = new SkuUploadESTo();
                    BeanUtils.copyProperties(skuInfo, skuUploadESTo);

                    skuUploadESTo.setSkuPrice((skuInfo.getPrice()));
                    skuUploadESTo.setSkuImage(skuInfo.getSkuDefaultImg());
                    skuUploadESTo.setHasStock(finalHasStockMap.get(skuInfo.getSkuId()));

                    //远程调用查询热度
                    skuUploadESTo.setHotStock(0L);


                    //查询品牌和分类信息
                    Brand brand = brandService.getById(skuUploadESTo.getBrandId());
                    skuUploadESTo.setBrandName(brand.getName());
                    skuUploadESTo.setBrandImg(brand.getLogo());

                    Category category = categoryService.getById(skuUploadESTo.getCatalogId());
                    skuUploadESTo.setCatalogName(category.getName());

                    skuUploadESTo.setAttrs(esAttrs);
                    return skuUploadESTo;

                })
                .collect(Collectors.toList());

        //远程调用es保存
        Optional<Result> indexResult = Optional.ofNullable(searchFeignClient.index(skuUploadESToList));
        if (indexResult.isPresent()) {
            int code = indexResult.get().getCode();
            if (code != 200) {
                log.error("商品上架失败");
                //TODO 重复调用、解决幂等性
            } else {
                //修改当前spu的状态
                SpuInfo spuInfo = new SpuInfo();
                spuInfo.setId(spuId);
                spuInfo.setPublishStatus(ProductConstant.ProductStatusEnum.UP.getCode());
                spuInfo.setUpdateTime(LocalDateTime.now());
                this.updateById(spuInfo);
            }
        }

    }

    @Override
    public List<GetItemWeightTo> getWeight(Set<Long> skuIds) {
        ArrayList<GetItemWeightTo> result = new ArrayList<>();
        Map<Long, Long> map = skuInfoService.listByIds(skuIds)
                .stream()
                .collect(Collectors.toMap(SkuInfo::getSkuId, SkuInfo::getSpuId));
        for (Map.Entry<Long, Long> entry : map.entrySet()) {
            GetItemWeightTo weightTo = new GetItemWeightTo();
            weightTo.setWeight(this.getById(entry.getValue()).getWeight());
            weightTo.setSkuId(entry.getKey());
            result.add(weightTo);
        }
        return result;
    }

    @Override
    public SpuInfo getSpuInfoBySkuId(Long skuId) {
        Long spuId = skuInfoService.getById(skuId).getSpuId();
        return this.getById(spuId);
    }

}
