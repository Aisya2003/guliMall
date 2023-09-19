package com.example.mall.member.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.member.model.po.MemberLoginLog;
import com.example.mall.member.service.MemberLoginLogService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 会员登录记录
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequestMapping("/member/memberloginlog")
public class MemberLoginLogController {
    @Autowired
    private MemberLoginLogService memberLoginLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<MemberLoginLog> result = PageResultUtils.getPage(params, memberLoginLogService.getBaseMapper(), MemberLoginLog.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		MemberLoginLog memberLoginLog = memberLoginLogService.getById(id);

        return Result.ok(memberLoginLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberLoginLog memberLoginLog){
		memberLoginLogService.save(memberLoginLog);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberLoginLog memberLoginLog){
		memberLoginLogService.updateById(memberLoginLog);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		memberLoginLogService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
