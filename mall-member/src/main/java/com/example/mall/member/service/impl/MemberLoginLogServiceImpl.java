package com.example.mall.member.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mall.member.mapper.MemberLoginLogMapper;
import com.example.mall.member.model.po.MemberLoginLog;
import com.example.mall.member.service.MemberLoginLogService;


@Service
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogMapper, MemberLoginLog> implements MemberLoginLogService {


}