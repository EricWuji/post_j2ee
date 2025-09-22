package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.dto.ForbiddenWord;

import java.util.List;

public interface ForbiddenWordService extends IService<ForbiddenWord> {
    List<String> getAllForbiddenWords();
}
