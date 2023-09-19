package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.WareConstant;
import com.example.mall.ware.model.po.WareOrderTask;
import com.example.mall.ware.model.po.WareOrderTaskDetail;
import com.example.mall.ware.mapper.WareOrderTaskDetailMapper;
import com.example.mall.ware.service.WareOrderTaskDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.ware.service.WareOrderTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailMapper, WareOrderTaskDetail> implements WareOrderTaskDetailService {
    private final WareOrderTaskService wareOrderTaskService;

    @Override
    public void updateStatusBySkuIdAndTaskId(Long taskId, Long skuId) {
        List<WareOrderTaskDetail> wareOrderTaskDetailList = this.list(new LambdaQueryWrapper<WareOrderTaskDetail>()
                .eq(WareOrderTaskDetail::getTaskId, taskId));
        //更新单个状态
        WareOrderTaskDetail wareOrderTaskDetail = wareOrderTaskDetailList.stream()
                .filter(taskDetail -> taskDetail.getSkuId().longValue() == skuId)
                .collect(Collectors.toList()).get(0);

        WareOrderTaskDetail updateTaskDetail = new WareOrderTaskDetail();
        updateTaskDetail.setId(wareOrderTaskDetail.getId());
        updateTaskDetail.setLockStatus(WareConstant.WareOrderTaskDetailStatusEnum.UNLOCKED.getCode());

        this.updateById(updateTaskDetail);

        //查询最新状态
        wareOrderTaskDetailList = this.list(new LambdaQueryWrapper<WareOrderTaskDetail>()
                .eq(WareOrderTaskDetail::getTaskId, taskId));
        boolean allUnlocked = wareOrderTaskDetailList.stream()
                .allMatch(orderTask -> orderTask.getLockStatus() != WareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode());
        //存在未解锁的库存
        if (!allUnlocked) {
            return;
        }
        //更新总状态
        WareOrderTask updateTask = new WareOrderTask();
        updateTask.setId(wareOrderTaskDetail.getTaskId());
        updateTask.setTaskStatus(WareConstant.WareOrderTaskStatusEnum.SUCCESS.getCode());
        wareOrderTaskService.updateById(updateTask);

    }
}
