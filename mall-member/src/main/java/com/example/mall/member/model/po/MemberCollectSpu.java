package com.example.mall.member.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 会员收藏的商品
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("ums_member_collect_spu")
public class MemberCollectSpu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * spu_id
     */
    private Long spuId;

    /**
     * spu_name
     */
    private String spuName;

    /**
     * spu_img
     */
    private String spuImg;

    /**
     * create_time
     */
    private LocalDateTime createTime;


}
