package cn.kgc.musicserver.domain.dto;

import lombok.Data;

/**
 * 自定义异常
 *
 * @Author XiaoMo
 * @Date 2024/8/24 11:25
 * @Version 1.0
 */
@Data
public class CustomeException extends RuntimeException {
    private String code;
    private String msg;

    public CustomeException(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }
}
