package org.example.backend.controller.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {
    @ExceptionHandler(ValidationException.class)
    public Result<Void> validationException(ValidationException e) {
        log.error("Resolve [{}: {}]", e.getClass().getName(), e.getMessage());
        return Result.failure(400, "参数错误");
    }
}
