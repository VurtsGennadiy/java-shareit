package ru.practicum.shareit.errorhandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice {

    // нарушение ограничений (constraint) БД
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Нарушение ограничений базы данных", ex);
        if (ex.getMessage().contains("uq_user_email")) {
            return new ErrorResponse("Пользователь с таким email уже существует");
        } else {
            return new ErrorResponse("Ошибка уникальности или целостности данных");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException exception) {
        log.warn("Invalid request: {}",exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Ошибка доступа: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BookingCreateException.class)
    public ErrorResponse handleBookingException(BookingCreateException ex) {
        log.warn("Ошибка создания бронирования для item_id = {}, owner_id = {}, период с {} по {}: {}",
                ex.getBookingCreateDto().getItemId(),
                ex.getBooker().getId(),
                ex.getBookingCreateDto().getStart(),
                ex.getBookingCreateDto().getEnd(),
                ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommentException.class)
    public ErrorResponse handleCommentException(CommentException ex) {
        log.warn("Ошибка создания комментария: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse error(Exception e) {
        log.error("Error", e);
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }
}
