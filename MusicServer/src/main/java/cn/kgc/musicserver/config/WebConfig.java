package cn.kgc.musicserver.config;

import cn.kgc.musicserver.interceptors.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @author XiaoMo
 * @version 1.0
 * @date 2025/6/18 15:23
 */
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserInterceptor userInterceptor;
    //注入拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      //  拦截所有请求路径交给userInterceptor来验证是否登录（放行登录方法）
      registry
              .addInterceptor(userInterceptor)
              .addPathPatterns("/**")
              .excludePathPatterns("/admin/login","/admin/register","/r/**");
    }
    //资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String file_path = "file:d:"+ File.separator+"mres"+File.separator;
        //映射本地音乐
        registry.addResourceHandler("/r/song/**")
                .addResourceLocations(file_path+"song"+ File.separator);

        //映射本地图片
        registry.addResourceHandler("/r/img/**")
                .addResourceLocations(file_path+"images"+ File.separator);

    }
    //跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*");
    }
}
