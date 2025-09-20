package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class CommentResponseVO implements BaseData {
    Integer commentId;
    String username;
    String commentContent;
}
