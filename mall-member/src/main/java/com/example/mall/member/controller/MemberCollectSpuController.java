package com.example.mall.member.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.member.model.po.MemberCollectSpu;
import com.example.mall.member.service.MemberCollectSpuService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 会员收藏的商品
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequestMapping("/member/membercollectspu")
public class MemberCollectSpuController {
    @Autowired
    private MemberCollectSpuService memberCollectSpuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<MemberCollectSpu> result = PageResultUtils.getPage(params, memberCollectSpuService.getBaseMapper(), MemberCollectSpu.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		MemberCollectSpu memberCollectSpu = memberCollectSpuService.getById(id);

        return Result.ok(memberCollectSpu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberCollectSpu memberCollectSpu){
		memberCollectSpuService.save(memberCollectSpu);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberCollectSpu memberCollectSpu){
		memberCollectSpuService.updateById(memberCollectSpu);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		memberCollectSpuService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
