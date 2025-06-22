package cn.kgc.musicserver.controller;

import cn.kgc.musicserver.domain.dto.Result;
import cn.kgc.musicserver.domain.dto.ResultUtil;
import cn.kgc.musicserver.domain.entity.Comment;
import cn.kgc.musicserver.domain.entity.Singer;
import cn.kgc.musicserver.domain.entity.Song;
import cn.kgc.musicserver.service.ICommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

// 评论控制器

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final ICommentService commentService;


    /**
     * 获取评论列表
     * @param pageNo
     * @param size
     * @return
     */
    @GetMapping("page")
    public Result getCommentPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        IPage<Comment> page = new Page<>(pageNo, size);
        page = commentService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }

    /**
     * 根据用户id查询其评论，并分页显示
     * @param pageNo
     * @param size
     * @return
     */
    @GetMapping("pagebyuserid")
    public Result getCommentPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", required = false)String search) {

        Integer userid;
        if(Objects.isNull(search) || "".equals(search)) {
            userid = 1;
        }else {
            userid = Integer.parseInt(search);
        }
        System.out.println(userid);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq(search != null, "user_id", userid);
        wrapper.orderByDesc("id");
        IPage<Comment> page = new Page<>(pageNo, size);
        page = commentService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }


    /**
     * 添加或修改评论
     */
    @PostMapping("add")
    public Result addSinger(@RequestBody Comment comment) {
        boolean res = commentService.saveOrUpdate(comment);
        if(!res){
            return ResultUtil.returnFail("添加失败");
        }
        return ResultUtil.returnSuccess("添加成功");
    }


    // 根据点赞数从高到低返回分页数据
    @GetMapping("pagebyup")
    public Result getCommentPageByUp(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("up");
        IPage<Comment> page = new Page<>(pageNo, size);
        page = commentService.page(page,wrapper);
        return ResultUtil.returnDataSuccess(page);
    }


    /**
     * 评论详情
     * @param id
     * @return
     */
    @GetMapping("detail")
    public Result getCommentDetail(Integer id){
        Comment comment = commentService.getById(id);
        return ResultUtil.returnDataSuccess(comment);
    }

    // 删除评论
    @GetMapping("delete")
    public Result delComment(Integer id){
        boolean b = commentService.removeById(id);
        if(!b){
            return ResultUtil.returnFail("删除失败");
        }
        return ResultUtil.returnSuccess("删除成功");
    }
}
