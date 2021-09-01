package com.marketlogic.app.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionHelper {

    @ExceptionHandler(value = {AppServiceException.class})
    public ResponseEntity<ErrorMessage> handleAppException(AppServiceException ex) {
        log.error("Application exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorMessage(ex.getErrorCode()), ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        log.error("Exception: ", ex);
        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}