package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
@TableName("forum")
public class Forum implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer forumId;
    String forumName;
    String forumDesc;
}
