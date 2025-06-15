package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentCreateDto dto, Item item, User user) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
        return dto;
    }

    public static List<CommentDto> toDto(Collection<Comment> comments) {
        if (comments == null) return List.of();
        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }
}
