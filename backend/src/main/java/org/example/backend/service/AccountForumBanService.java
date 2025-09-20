package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.AccountForumBan;

import java.util.List;

public interface AccountForumBanService extends IService<AccountForumBan> {
    boolean existsAccountIdAndForumId(Integer accountId, Integer forumId);
    String addAccountIdAndForumId(Integer accountId, Integer forumId);
    String removeAccountIdAndForumId(Integer accountId, Integer forumId);
    List<Integer> getForumIdsByAccountId(Integer accountId);
}
