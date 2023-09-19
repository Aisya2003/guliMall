package com.example.mall.ware.mapper;

import com.example.mall.ware.model.po.WareSku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface WareSkuMapper extends BaseMapper<WareSku> {
    @Select("SELECT SUM(stock - stock_locked) FROM wms_ware_sku  WHERE sku_id = #{skuId}")
    Long getStock(@Param("skuId") Long skuId);

    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}
