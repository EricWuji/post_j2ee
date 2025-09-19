package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.dto.AccountForumBan;
import org.example.backend.mapper.AccountForumBanMapper;
import org.example.backend.service.AccountForumBanService;
import org.springframework.stereotype.Service;

@Service
public class AccountForumBanServiceImpl extends ServiceImpl<AccountForumBanMapper, AccountForumBan> implements AccountForumBanService {
    @Override
    public boolean existsAccountIdAndForumId(Integer accountId, Integer forumId) {
        QueryWrapper<AccountForumBan> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("account_id",accountId)
                .eq("forum_id",forumId);
        return this.count(queryWrapper) > 0;
    }
}
