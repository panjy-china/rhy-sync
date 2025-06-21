package cn.Rhysync.musicserver.controller;

import cn.Rhysync.musicserver.domain.dto.Result;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.domain.entity.Vedio;
import cn.Rhysync.musicserver.service.IVedioService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * (Vedio)表控制层
 *
 * @author makejava
 * @since 2025-06-21 19:31:17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/vedio")
public class VedioController {
    /**
     * 服务对象
     */
    private final IVedioService vedioService;
    
    @GetMapping("page")
    public Result getVedioPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", required = false)String search) {
        System.out.println("pageNo---:"+pageNo);
        System.out.println("size---:"+size);
        QueryWrapper<Vedio> wrapper = new QueryWrapper<>();
        wrapper.like(search != null, "v_name", search);
        wrapper.orderByDesc("id");
        IPage<Vedio> page = new Page<>(pageNo, size);
        page = vedioService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }

    @GetMapping("detail")
    public Result getVedioDetail(Integer id){
        Vedio vedio = vedioService.getById(id);
        return ResultUtil.returnDataSuccess(vedio);
    }

    @GetMapping("del")
    public Result delSong(Integer id){
        boolean b = vedioService.removeById(id);
        if(!b){
            return ResultUtil.returnFail("删除失败");
        }
        return ResultUtil.returnSuccess("删除成功");
    }

}

