package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest extends MapperTest {

    @Test
    void toDto() {
        Comment comment = getComment();
        CommentDto dto = commentMapper.toDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
    }

    @Test
    void toComment() {
        CommentCreateDto dto = new CommentCreateDto("item_comment_text");
        Item item = getItem();
        User user = getBooker();
        Comment comment = commentMapper.toComment(dto, item, user);

        assertEquals(dto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    void toListDto() {
        Item item = getItem();
        User user = getBooker();
        Comment comment = getComment();

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthor(user);
        comment2.setText("item_comment2_text");
        comment2.setItem(item);

        List<Comment> listComments = new ArrayList<>(2);
        listComments.add(comment);
        listComments.add(comment2);
        List<CommentDto> listDto = commentMapper.toDto(listComments);

        assertEquals(listComments.size(), listDto.size());
        assertEquals(commentMapper.toDto(listComments.getFirst()), listDto.getFirst());
        assertEquals(commentMapper.toDto(listComments.getLast()), listDto.getLast());
    }
}
