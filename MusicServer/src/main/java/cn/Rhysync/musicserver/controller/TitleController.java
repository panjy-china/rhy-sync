package cn.Rhysync.musicserver.controller;


import cn.hutool.core.util.ObjUtil;
import cn.Rhysync.musicserver.domain.dto.Result;
import cn.Rhysync.musicserver.domain.dto.ResultEnum;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.domain.entity.Title;
import cn.Rhysync.musicserver.service.ITitleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/title")
public class TitleController {
    private final ITitleService titleService;

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public Result get(Integer id) {
        Title title = titleService.getById(id);
        if(ObjUtil.isNull(title)) {
            return ResultUtil.returnResult(ResultEnum.FAIL_PARAM);
        }
        return ResultUtil.returnDataSuccess(title);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping("/list")
    public Object get() {
        List<Title> list = titleService.list();
        if(ObjectUtils.isEmpty(list)) {
            return ResultUtil.returnResult(ResultEnum.FAIL_PARAM);
        }
        return list;
    }

    /**
     * 分页查询
     *
     * @param page 当前页码
     * @param size 每页数量
     * @return
     */
    @GetMapping("page")
    public Object getPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        IPage<Title> iPage = new Page<>(page, size);
        QueryWrapper<Title> wrapper = new QueryWrapper<>();
        wrapper.like("name","人");
        IPage<Title> page1 = titleService.page(iPage, wrapper);
        //List<Title> titleList = titleService.list(iPage);

        return page1;
    }

}
