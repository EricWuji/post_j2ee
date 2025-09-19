package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
@TableName("post")
public class Post implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer postId;
    String postContent;
    String postTitle;
    Integer userId;
    Integer forumId;
    boolean deleted;
    boolean topped;
}
