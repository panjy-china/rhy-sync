package cn.Rhysync.musicserver.interceptors;

import cn.hutool.core.util.StrUtil;
import cn.Rhysync.musicserver.domain.dto.ResultEnum;
import cn.Rhysync.musicserver.domain.dto.ResultUtil;
import cn.Rhysync.musicserver.utils.JwtUtil;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 验证用户是否登录
 *
 * @author XiaoMo
 * @version 1.0
 * @date 2025/6/18 15:17
 */
@Component
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");


        //判断是否controller方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //获取令牌token
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            String res= JSON.toJSONString(ResultUtil.returnResult(ResultEnum.FAIL_NOT_HAVE_PERMISSION));
            response.getWriter().println(res);
            return false;
        }
        //令牌是否过期
        if(JwtUtil.isExpiration(token)) {
            String res= JSON.toJSONString(ResultUtil.returnResult(ResultEnum.FAIL_TOKEN_EXPIRED));
            response.getWriter().println(res);
            return false;
        }
        Claims claims = JwtUtil.parseJWT(token);
        //令牌为空
        if(claims == null) {
            String res= JSON.toJSONString(ResultUtil.returnResult(ResultEnum.FAIL_NOT_HAVE_PERMISSION));
            response.getWriter().println(res);
            return false;
        };

        return true;
    }
}
