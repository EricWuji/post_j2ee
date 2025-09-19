package org.example.backend.entity.vo.request;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class PostRequestVO implements BaseData {
    String postTitle;
    String postContent;
    Integer forumId;
}
