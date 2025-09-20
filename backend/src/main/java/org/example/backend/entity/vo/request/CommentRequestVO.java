package org.example.backend.entity.vo.request;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class CommentRequestVO implements BaseData {
    Integer postId;
    String commentContent;
}
