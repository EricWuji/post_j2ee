package org.example.backend.entity.vo.response;

import lombok.Builder;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
@Builder
public class ForumAvailableResponseVO implements BaseData {
    Integer forumId;
    String forumName;
}
