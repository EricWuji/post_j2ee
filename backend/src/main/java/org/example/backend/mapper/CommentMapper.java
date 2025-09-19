package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.entity.dto.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
