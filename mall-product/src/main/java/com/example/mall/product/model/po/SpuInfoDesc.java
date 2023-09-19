package com.example.mall.product.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * spu信息介绍
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @TableId(value = "spu_id",type = IdType.INPUT)
    private Long spuId;

    /**
     * 商品介绍
     */
    private String decript;


}
