package com.example.mall.ware.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-08-01 08:41:39
 */
@Data
@TableName("mq_message")
public class MqMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String messageId;
	/**
	 * 
	 */
	private String content;
	/**
	 * 
	 */
	private String toExchange;
	/**
	 * 
	 */
	private String routingKey;
	/**
	 * 
	 */
	private String classType;
	/**
	 * 0-新建 1-已发送 2-错误 3-已到达
	 */
	private Integer messageStatus;
	/**
	 * 
	 */
	private LocalDateTime creatTime;
	/**
	 * 
	 */
	private LocalDateTime updateTime;

}
