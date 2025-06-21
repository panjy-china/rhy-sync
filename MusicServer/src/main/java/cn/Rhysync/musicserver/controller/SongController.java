package cn.Rhysync.musicserver.controller;


import cn.Rhysync.musicserver.domain.dto.Result;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.domain.entity.Song;
import cn.Rhysync.musicserver.service.ISongService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 歌曲 前端控制器
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/song")
public class SongController {
    private final ISongService songService;

    /**
     * 分页歌曲
     * @param pageNo
     * @param size
     * @return
     */
    @GetMapping("page")
    public Result getSongPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", required = false)String search) {
        System.out.println("pageNo---:"+pageNo);
        QueryWrapper<Song> wrapper = new QueryWrapper<>();
        wrapper.like(search != null, "name", search);
        wrapper.orderByDesc("id");
        IPage<Song> page = new Page<>(pageNo, size);
        page = songService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }

    /**
     * 歌曲详情
     * @param id
     * @return
     */
    @GetMapping("detail")
    public Result getSongDetail(Integer id){
        Song song = songService.getById(id);
        return ResultUtil.returnDataSuccess(song);
    }

    @GetMapping("del")
    public Result delSong(Integer id){
        boolean b = songService.removeById(id);
        if(!b){
            return ResultUtil.returnFail("删除失败");
        }
        return ResultUtil.returnSuccess("删除成功");
    }
}
