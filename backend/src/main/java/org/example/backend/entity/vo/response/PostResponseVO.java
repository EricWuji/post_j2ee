package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class PostResponseVO implements BaseData {
    String postTitle;
    String postContent;
    String username;
}
