package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    Integer commentId;
    Integer postId;
    Integer userId;
    String commentContent;
    boolean deleted;
}
