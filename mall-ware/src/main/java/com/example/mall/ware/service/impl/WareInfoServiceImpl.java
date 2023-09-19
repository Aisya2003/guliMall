package com.example.mall.ware.service.impl;

import com.example.mall.ware.model.po.WareInfo;
import com.example.mall.ware.mapper.WareInfoMapper;
import com.example.mall.ware.service.WareInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class WareInfoServiceImpl extends ServiceImpl<WareInfoMapper, WareInfo> implements WareInfoService {

}
