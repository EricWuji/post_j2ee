package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("account_forum_ban")
public class AccountForumBan {
    @TableId(type = IdType.AUTO)
    Integer accountForumBanId;
    Integer accountId;
    Integer forumId;
}
