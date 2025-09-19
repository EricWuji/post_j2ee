package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.AccountForumBan;

public interface AccountForumBanService extends IService<AccountForumBan> {
    boolean existsAccountIdAndForumId(Integer accountId, Integer forumId);
}
