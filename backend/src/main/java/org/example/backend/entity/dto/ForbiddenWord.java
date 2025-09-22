package org.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
@TableName("forbidden_word")
public class ForbiddenWord implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer forbiddenWordId;
    String forbiddenWord;
    String replaceWord;
}
