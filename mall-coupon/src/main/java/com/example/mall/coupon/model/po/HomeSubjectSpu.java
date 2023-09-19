package com.example.mall.coupon.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 专题商品
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("sms_home_subject_spu")
public class HomeSubjectSpu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专题名字
     */
    private String name;

    /**
     * 专题id
     */
    private Long subjectId;

    /**
     * spu_id
     */
    private Long spuId;

    /**
     * 排序
     */
    private Integer sort;


}
