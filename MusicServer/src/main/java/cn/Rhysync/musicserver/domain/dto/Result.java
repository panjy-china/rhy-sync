package cn.Rhysync.musicserver.domain.dto;



import java.io.Serializable;

/**
 * 返回信息包装类
 * @param <T>
 */
public class Result<T> implements Serializable {
    //信息编码
    public String code;
    //内容
    public String msg;
    //携带数据
    private T data;

    /**
     * 无参构造
     */
    public Result() {}

    /**
     * 根据code，msg创建一个Resutl
     * @param code
     * @param msg
     */
    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据code，msg，data创建一个Resutl
     * @param code
     * @param msg
     * @param data
     */
    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 根据枚举创建一个Result
     * @param resultEnum
     */
    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
