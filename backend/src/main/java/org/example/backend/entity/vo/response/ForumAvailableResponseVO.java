package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class ForumAvailableResponseVO implements BaseData {
    Integer forumId;
    String forumName;
}
