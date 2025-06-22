package cn.Rhysync.musicserver.controller;

import cn.Rhysync.musicserver.domain.dto.Result;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.domain.entity.SongList;
import cn.Rhysync.musicserver.service.SongListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 歌单(SongList)表控制层
 *
 * @author makejava
 * @since 2025-06-22 08:43:41
 */
@RestController
@RequestMapping("songlist")
public class SongListController {
    /**
     * 服务对象
     */
    @Resource
    private SongListService songListService;

    @GetMapping()
    public Result queryByPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.like(search != null, "title", search);
        wrapper.orderByAsc("id");
        IPage<SongList> page = new Page<>(pageNo, size);
        page = songListService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }

}

