package com.example.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mall.order.model.po.MqMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-08-01 08:41:39
 */
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {
	
}
