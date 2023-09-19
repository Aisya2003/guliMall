package com.example.mall.ware.service;

import com.example.mall.ware.model.po.WareOrderTaskDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 库存工作单 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetail> {

    void updateStatusBySkuIdAndTaskId(Long taskId, Long skuId);
}
