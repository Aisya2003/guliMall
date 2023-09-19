package com.example.mall.member.service.impl;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.vo.FreightVo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mall.member.mapper.MemberReceiveAddressMapper;
import com.example.mall.member.model.po.MemberReceiveAddress;
import com.example.mall.member.service.MemberReceiveAddressService;

import java.math.BigDecimal;
import java.util.StringJoiner;


@Service
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressMapper, MemberReceiveAddress> implements MemberReceiveAddressService {
    @Override
    public Result<FreightVo> getFare(Long addrId) {
        MemberReceiveAddress receiveAddress = this.getById(addrId);
        if (receiveAddress == null) {
            return Result.fail();
        }

        FreightVo freightVo = new FreightVo();
        BigDecimal freight = new BigDecimal(addrId % 10 + addrId % 5 + addrId % 2);

        freightVo.setFreight(freight);

        freightVo.setProvince(receiveAddress.getProvince());
        freightVo.setCity(receiveAddress.getCity());
        freightVo.setRegion(receiveAddress.getRegion());
        freightVo.setDetailAddress(receiveAddress.getDetailAddress());

        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(receiveAddress.getProvince());
        stringJoiner.add(receiveAddress.getCity());
        stringJoiner.add(receiveAddress.getRegion());
        stringJoiner.add(receiveAddress.getDetailAddress());
        freightVo.setFullAddress(stringJoiner.toString());

        freightVo.setReceiver(receiveAddress.getName());

        freightVo.setAreacode(receiveAddress.getAreacode());
        freightVo.setPostCode(receiveAddress.getPostCode());

        freightVo.setPhone(receiveAddress.getPhone());
        return Result.ok(freightVo);
    }
}