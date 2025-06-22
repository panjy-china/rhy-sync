package cn.kgc.musicserver.domain.dto;

import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * springboot异常处理
 *
 * @Author XiaoMo
 * @Date 2024/8/24 11:19
 * @Version 1.0
 */
@RestControllerAdvice
public class ResultException {
    /**
     * 自定义异常抓取
     * @param e
     * @return
     */

    @ExceptionHandler(CustomeException.class)
    public Result errorException(CustomeException e) {
        return new Result(e.getCode(), e.getMsg());
    }


    /**
     * 全局通用异常
     * @param e
     * @return
     */

    //@ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return ResultUtil.returnResult(ResultEnum.COMMON_EXCEPTION);
    }
}
