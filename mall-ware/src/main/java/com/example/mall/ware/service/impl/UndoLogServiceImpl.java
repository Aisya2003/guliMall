package com.example.mall.ware.service.impl;

import com.example.mall.ware.model.po.UndoLog;
import com.example.mall.ware.mapper.UndoLogMapper;
import com.example.mall.ware.service.UndoLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class UndoLogServiceImpl extends ServiceImpl<UndoLogMapper, UndoLog> implements UndoLogService {

}
