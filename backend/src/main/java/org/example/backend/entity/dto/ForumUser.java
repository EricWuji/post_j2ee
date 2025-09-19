package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("forum_user")
public class ForumUser {
    @TableId(type = IdType.AUTO)
    private Integer forumUserId;
    Integer userId;
    Integer forumId;
}
