package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.product.model.po.SpuImages;
import com.example.mall.product.mapper.SpuImagesMapper;
import com.example.mall.product.service.SpuImagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesMapper, SpuImages> implements SpuImagesService {

    @Override
    @Transactional
    public void saveImagesBySpuInfoId(Long spuInfoId, List<String> imagesUrl) {
        if (imagesUrl == null || imagesUrl.isEmpty()){
            log.info("spu保存的图片集为空[{}]",spuInfoId);
        }else {
            List<SpuImages> images = imagesUrl.stream()
                    .map(imgUrl -> {
                        SpuImages spuImages = new SpuImages();
                        spuImages.setSpuId(spuInfoId);
                        spuImages.setImgUrl(imgUrl);
                        int beginIndex = imgUrl.lastIndexOf("/");
                        if (beginIndex != -1) {
                            spuImages.setImgName(imgUrl.substring(beginIndex + 1));
                        }
                        return spuImages;
                    })
                    .collect(Collectors.toList());
            this.saveBatch(images);
            log.info("spu保存图集完成[{}]",spuInfoId);
        }
    }
}
