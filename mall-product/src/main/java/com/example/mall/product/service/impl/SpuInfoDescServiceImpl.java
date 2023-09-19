package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.product.model.po.SpuInfoDesc;
import com.example.mall.product.mapper.SpuInfoDescMapper;
import com.example.mall.product.service.SpuInfoDescService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescMapper, SpuInfoDesc> implements SpuInfoDescService {

    @Override
    @Transactional
    public void saveSpuImagesInfoDescBySpuInfoId(Long spuInfoId, String imagesUrl) {
        Boolean result = null;
        if (StringUtils.isNotBlank(imagesUrl)) {
            SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
            spuInfoDesc.setSpuId(spuInfoId);
            spuInfoDesc.setDecript(imagesUrl);
            result = this.save(spuInfoDesc);
        }
        log.info("Spu的描述图片信息保存完成[{}]",result);
    }
}
