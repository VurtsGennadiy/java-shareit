package ru.practicum.shareit.errorhandle;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice {
    // ошибки валидации @Validation
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String fieldName = path.substring(path.lastIndexOf(".") + 1);
                    return  fieldName +  " " + violation.getMessage() + ". ";
                })
                .reduce("", String::concat);
        message = message.strip();
        log.warn("Invalid request, validation error: {}", message);
        return new ErrorResponse(message);
    }

    // ошибки валидации @Valid
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().stream()
                .map(fieldError -> "Поле " + fieldError.getField() + " " + fieldError.getDefaultMessage() + ". ")
                .reduce(" ", String::concat);
        message = message.strip();
        log.warn("Invalid request, validation error: {}", message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse error(Exception e) {
        log.error("Error", e);
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }
}
