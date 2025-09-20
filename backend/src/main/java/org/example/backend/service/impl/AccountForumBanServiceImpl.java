package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.dto.AccountForumBan;
import org.example.backend.mapper.AccountForumBanMapper;
import org.example.backend.service.AccountForumBanService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public String addAccountIdAndForumId(Integer accountId, Integer forumId) {
        if (this.existsAccountIdAndForumId(accountId,forumId)) return "User is already banned in this forum";
        AccountForumBan accountForumBan = new AccountForumBan();
        accountForumBan.setAccountId(accountId);
        accountForumBan.setForumId(forumId);
        if (this.save(accountForumBan)) {
            return null;
        } else {
            return "Failed to ban user in this forum";
        }
    }

    @Override
    public String removeAccountIdAndForumId(Integer accountId, Integer forumId) {
        QueryWrapper<AccountForumBan> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("account_id",accountId)
                .eq("forum_id",forumId);
        if (!this.existsAccountIdAndForumId(accountId,forumId)) return "User is not banned in this forum";
        if (this.remove(queryWrapper)) {
            return null;
        } else {
            return "Failed to unban user in this forum";
        }
    }

    @Override
    public List<Integer> getForumIdsByAccountId(Integer accountId) {
        QueryWrapper<AccountForumBan> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("forum_id")
                .eq("account_id",accountId);
        return this.list(queryWrapper).stream().map(AccountForumBan::getForumId).toList();
    }
}
