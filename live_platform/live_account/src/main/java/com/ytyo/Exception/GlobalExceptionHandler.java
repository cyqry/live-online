package com.ytyo.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        // 从异常中获取校验错误信息
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();

        // 构建错误响应
        List<String> errorMessages = new ArrayList<>();
        for (ObjectError error : errors) {
            errorMessages.add(error.getDefaultMessage());
        }
        if (errorMessages.isEmpty()) {
            return ResponseEntity.badRequest().body(ex);
        }
        // 返回带有错误信息的响应
        return ResponseEntity.badRequest().body(errorMessages.get(0));
    }
}