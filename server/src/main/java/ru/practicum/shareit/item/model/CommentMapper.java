package ru.practicum.shareit.item.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(source = "author.name", target = "authorName")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "user")
    Comment toComment(CommentCreateDto dto, Item item, User user);
}
