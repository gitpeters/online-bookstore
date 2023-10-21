package com.peters.userservice.controller.advice;


import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.exception.ApplicationAuthenticationException;
import com.peters.userservice.exception.DuplicateException;
import com.peters.userservice.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvice {
    private final MessageSource messageSource;
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<CustomResponse> handleDuplicateException(DuplicateException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomResponse response = new CustomResponse(HttpStatus.BAD_REQUEST.name(),
                messageSource.getMessage("duplicate.product.message", e.getArgs(),
                        LocaleContextHolder.getLocale()), null);
        return ResponseEntity.status(status).body(response);
    }
    @ExceptionHandler(ApplicationAuthenticationException.class)
    public ResponseEntity<CustomResponse> handleApplicationAuthenticationException(ApplicationAuthenticationException e){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomResponse response = new CustomResponse(status, e.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CustomResponse> handleUserAlreadyException(UserAlreadyExistsException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomResponse response = new CustomResponse(status, e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
