package org.example.backend.entity.vo.request;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

import java.util.List;

@Data
public class ForumBanRequestVO implements BaseData {
    List<Integer> forumId;
    Integer userId;
}
