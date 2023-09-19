package com.example.mall.member.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mall.member.mapper.GrowthChangeHistoryMapper;
import com.example.mall.member.model.po.GrowthChangeHistory;
import com.example.mall.member.service.GrowthChangeHistoryService;


@Service
public class GrowthChangeHistoryServiceImpl extends ServiceImpl<GrowthChangeHistoryMapper, GrowthChangeHistory> implements GrowthChangeHistoryService {

}