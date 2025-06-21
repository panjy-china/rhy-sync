package cn.Rhysync.musicserver.controller;


import cn.hutool.core.util.ObjUtil;
import cn.Rhysync.musicserver.domain.dto.Result;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.domain.entity.Admin;
import cn.Rhysync.musicserver.service.IAdminService;
import cn.Rhysync.musicserver.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 管理员 前端控制器
 * </p>
 *
 * @author kgc
 * @since 2025-06-18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final IAdminService adminService;

    /**
     * 登录
     * @param admin
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Admin admin) {
        //封装查询条件
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("name", admin.getName());
        wrapper.eq("password", admin.getPassword());
        //调用查询
        Admin loginAdmin = adminService.getOne(wrapper);
        //判断是否查询到用户
        if(ObjUtil.isNotNull(loginAdmin)) {
            String token = JwtUtil.createJWT(loginAdmin.getName());
            return ResultUtil.returnSuccess("登录成功", token);
        }
        return ResultUtil.returnFail("用户名或密码错误");
    }
}
