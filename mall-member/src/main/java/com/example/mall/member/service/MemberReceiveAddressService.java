package com.example.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.vo.FreightVo;
import com.example.mall.member.model.po.MemberReceiveAddress;

import java.math.BigDecimal;


/**
 * 会员收货地址
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddress> {


    Result<FreightVo> getFare(Long addrId);
}

