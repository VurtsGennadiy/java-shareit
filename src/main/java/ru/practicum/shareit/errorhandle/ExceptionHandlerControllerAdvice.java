package ru.practicum.shareit.errorhandle;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice {
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
        log.warn("Invalid request", exception);
        return new ErrorResponse(message.strip());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().stream()
                .map(fieldError -> "Поле " + fieldError.getField() + " " + fieldError.getDefaultMessage() + ". ")
                .reduce(" ", String::concat);
        log.warn("Invalid request", exception);
        return new ErrorResponse(message.strip());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        log.warn("Invalid request " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DuplicateDataException.class})
    public ErrorResponse handleDuplicateDataException(DuplicateDataException exception) {
        log.warn("Invalid request {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse error(Exception e) {
        log.error("Error", e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
