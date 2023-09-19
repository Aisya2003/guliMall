package com.example.mall.common.model.page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PageResultUtils<T> implements Serializable {
    public static <T, M extends BaseMapper<T>> PageResult<T> getPage(PageRequestParams params, M baseMapper, Class<T> type) {
        Page<T> page = new Page<>(params.getPage(), params.getLimit());
        String key = params.getKey();
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        //存在参数时需要进行模糊匹配
        if (StringUtils.isNotBlank(key)) {
            //获取类中的所有字段
            List<String> fieldNames = getColumns(type);
            for (String fieldName : fieldNames) {
                //构建查询条件
                queryWrapper.or().like(StringUtils.isNotBlank(fieldName), fieldName, key);
            }
        }
        page = baseMapper.selectPage(page, queryWrapper);
        List<T> result = page.getRecords();
        long total = page.getTotal();
        return new PageResult<>(result, total);
    }

    public static <T, M extends BaseMapper<T>> PageResult<T> getPage(PageRequestParams params, M baseMapper, Class<T> type, QueryWrapper<T> queryWrapper) {
        if (queryWrapper == null){
            queryWrapper = new QueryWrapper<>();
        }
        Page<T> page = new Page<>(params.getPage(), params.getLimit());
        String key = params.getKey();
        //存在参数时需要进行模糊匹配
        if (StringUtils.isNotBlank(key)) {
            //获取类中的所有字段
            List<String> fieldNames = getColumns(type);

            //构建查询条件
            queryWrapper.and((wrapper) -> {
                for (String fieldName : fieldNames) {
                    wrapper.or().like(StringUtils.isNotBlank(fieldName), fieldName, key);
                }
            });

        }
        page = baseMapper.selectPage(page, queryWrapper);
        List<T> result = page.getRecords();
        long total = page.getTotal();
        return new PageResult<>(result, total);
    }


    private static <T> List<String> getColumns(Class<T> type) {
        ArrayList<String> fieldNames = new ArrayList<>();
        String name = null;
        String filedName = null;
        Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            filedName = field.getName();
            if (filedName.equals("serialVersionUID")) {
                continue;
            }
            //将驼峰命名转为下划线格式
            name = StringUtils.camelToUnderline(filedName);
            fieldNames.add(name);
        }
        return fieldNames;
    }


}
