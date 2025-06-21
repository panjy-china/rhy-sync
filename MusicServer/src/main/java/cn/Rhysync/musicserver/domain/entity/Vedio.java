package cn.Rhysync.musicserver.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.io.Serializable;

/**
 * (Vedio)实体类
 *
 * @author makejava
 * @since 2025-06-21 19:31:17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vedio")
public class Vedio implements Serializable {
    private static final long serialVersionUID = 564638206217268000L;
/**
     * 视频id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
/**
     * 视频名称
     */
    @TableField("v_name")
    private String vName;
/**
     * 作者
     */
    private String vAuthor;
/**
     * 播放量
     */
    private Integer vPlayback;
/**
     * 创建时间
     */
    private Date vCreatetime;
/**
     * 视频图片
     */
    private String vImg;
/**
     * 视频地址
     */
    private String vUrl;
/**
     * 区域id
     */
    private Integer regionid;
/**
     * 版本id
     */
    private Integer versionid;
}

