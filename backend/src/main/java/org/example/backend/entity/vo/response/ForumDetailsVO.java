package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class ForumDetailsVO implements BaseData {
    String forumName;
    String forumDesc;
}
