package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.Forum;

public interface ForumService extends IService<Forum> {
    Forum findForumById(int id);
    String findForumNameById(int id);
}
