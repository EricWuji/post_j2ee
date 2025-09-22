package org.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.dto.ForbiddenWord;
import org.example.backend.mapper.ForbiddenWordMapper;
import org.example.backend.service.ForbiddenWordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForbiddenWordServiceImpl extends ServiceImpl<ForbiddenWordMapper, ForbiddenWord> implements ForbiddenWordService {
    @Override
    public List<String> getAllForbiddenWords() {
        return this.list()
                .stream()
                .map(ForbiddenWord::getForbiddenWord)
                .toList();
    }
}
