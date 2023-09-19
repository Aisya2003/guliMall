package com.example.mall.coupon.service.impl;

import com.example.mall.coupon.model.po.HomeSubject;
import com.example.mall.coupon.mapper.HomeSubjectMapper;
import com.example.mall.coupon.service.HomeSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class HomeSubjectServiceImpl extends ServiceImpl<HomeSubjectMapper, HomeSubject> implements HomeSubjectService {

}
