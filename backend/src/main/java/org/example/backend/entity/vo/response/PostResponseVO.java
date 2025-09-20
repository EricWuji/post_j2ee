package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class PostResponseVO implements BaseData {
    Integer postId;
    String postTitle;
    String postContent;
    String username;
    String forumName;
    boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResponseVO that = (PostResponseVO) o;
        return postId.equals(that.postId);
    }

    @Override
    public int hashCode() {
        return postId.hashCode();
    }
}
