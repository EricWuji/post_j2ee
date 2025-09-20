package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
@TableName("comment")
public class Comment implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer commentId;
    Integer postId;
    Integer userId;
    String commentContent;
    boolean deleted;
}
