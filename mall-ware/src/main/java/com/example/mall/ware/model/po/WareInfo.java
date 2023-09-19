package com.example.mall.ware.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 仓库信息
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("wms_ware_info")
public class WareInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 仓库名
     */
    private String name;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 区域编码
     */
    private String areacode;


}
