package cn.kgc.musicserver.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

   @TableField("user_id")
    private Integer userId;

   @TableField("type")
   private Integer type;

   @TableField("song_id")
   private Integer songId;

   @TableField("song_list_id")
   private String songListId;

   @TableField("content")
   private String content;

   @TableField("create_time")
    private LocalDateTime createTime;

   @TableField("up")
   private Integer up;
}
