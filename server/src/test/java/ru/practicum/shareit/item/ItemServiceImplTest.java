package ru.practicum.shareit.item;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceImplTest {
    private final ItemServiceImpl service;
    private final TestData testData;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    private static final String SELECT_COUNT_BY_ID = "SELECT COUNT(*) FROM items WHERE id = ?";
    private static final String SELECT_COUNT_BY_DESCRIPTION = "SELECT COUNT(*) FROM items WHERE description = ?";

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private ItemRequest request1;
    private Comment comment1;
    private Booking booking2;

    @PostConstruct
    private void setTestData() {
        user1 = testData.getUser1();
        user2 = testData.getUser2();
        item1 = testData.getItem1();
        item2 = testData.getItem2();
        request1 = testData.getRequest1();
        comment1 = testData.getComment1();
        booking2 = testData.getBooking2();
    }

    @Test
    void createNewItem() {
        String name = "new_item_name";
        ItemDto createDto = ItemDto.builder()
                .name(name)
                .description("new_item_description")
                .available(true)
                .requestId(request1.getId())
                .build();
        long userId = user1.getId();

        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE name = ?", Long.class, name));

        ItemDto actual = service.createNewItem(createDto, userId);
        em.flush();
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM items WHERE name = ?", name);

        assertEquals(name, actual.getName());
        assertEquals(name, savedRow.get("name"));
        assertEquals(createDto.getDescription(), actual.getDescription());
        assertEquals(createDto.getDescription(), savedRow.get("description"));
        assertEquals(createDto.getAvailable(), actual.getAvailable());
        assertEquals(createDto.getAvailable(), savedRow.get("available"));
        assertEquals(userId, savedRow.get("owner_id"));
        assertEquals(request1.getId(), actual.getRequestId());
        assertEquals(request1.getId(), savedRow.get("request_id"));
        assertEquals(actual.getId(), savedRow.get("id"));
    }

    @Test
    void createNewItem_whenUserNotFound_thenNotSavedAndThrownException() {
        String name = "new_item_name";
        ItemDto createDto = ItemDto.builder()
                .name(name)
                .description("new_item_description")
                .available(true)
                .requestId(request1.getId())
                .build();
        long userId = -1L;

        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long.class, userId));

        assertThrows(NotFoundException.class, () -> service.createNewItem(createDto, userId));
        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE name = ?", Long.class, name));
    }

    @Test
    void createNewItem_whenRequestNotFound_thenNotSavedAndThrownException() {
        String name = "new_item_name";
        Long requestId = -1L;
        ItemDto createDto = ItemDto.builder()
                .name(name)
                .description("new_item_description")
                .available(true)
                .requestId(requestId)
                .build();
        long userId = user1.getId();

        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM requests WHERE id = ?", Long.class, requestId));

        assertThrows(NotFoundException.class, () -> service.createNewItem(createDto, userId));
        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM items WHERE name = ?", Long.class, name));
    }

    @Test
    void updateItem() {
        Long itemId = item2.getId();
        Long userId = user1.getId();
        String oldDescription = item2.getDescription();
        String newDescription = "updated_description";
        ItemDto updateDto = ItemDto.builder()
                .description(newDescription)
                .available(true)
                .build();

        assertEquals(1L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, oldDescription));
        assertEquals(0L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, newDescription));

        ItemDto actual = service.updateItem(updateDto, itemId, userId);
        em.flush();

        assertEquals(0L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, oldDescription));
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM items WHERE description = ?", newDescription);

        assertEquals(itemId, savedRow.get("id"));
        assertEquals(itemId, actual.getId());
        assertEquals(item2.getName(), savedRow.get("name"), "item name не должен обновиться");
        assertEquals(item2.getName(), actual.getName());
        assertEquals(newDescription, savedRow.get("description"));
        assertEquals(newDescription, actual.getDescription());
        assertTrue((Boolean) savedRow.get("available"));
        assertTrue(actual.getAvailable());
        assertEquals(userId, savedRow.get("owner_id"));
        assertEquals(request1.getId(), savedRow.get("request_id"));
        assertEquals(request1.getId(), actual.getRequestId());
    }

    @Test
    void updateItem_whenItemNotExist_thenThrowException() {
        long itemId = -1L;
        long userId = user1.getId();
        ItemDto updateDto = ItemDto.builder()
                .description("updated_description")
                .available(true)
                .build();

        assertEquals(0L, jdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Long.class, itemId));
        assertThrows(NotFoundException.class, () -> service.updateItem(updateDto, itemId, userId));
    }

    @Test
    void updateItem_whenUserNotExist_thenThrowException() {
        long itemId = item2.getId();
        long userId = -1L;
        ItemDto updateDto = ItemDto.builder()
                .description("updated_description")
                .available(true)
                .build();

        assertEquals(0L, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long.class, userId));
        assertThrows(NotFoundException.class, () -> service.updateItem(updateDto, itemId, userId));
    }

    @Test
    void updateItem_whenUserIsNotOwner_thenNotUpdateAndThrowException() {
        Long itemId = item2.getId();
        Long userId = user2.getId();
        String oldDescription = item2.getDescription();
        String newDescription = "updated_description";
        ItemDto updateDto = ItemDto.builder()
                .description(newDescription)
                .available(true)
                .build();

        assertEquals(1L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, oldDescription));
        assertEquals(0L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, newDescription));

        assertThrows(NotFoundException.class, () -> service.updateItem(updateDto, itemId, userId));
        assertEquals(1L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, oldDescription));
        assertEquals(0L,
                jdbcTemplate.queryForObject(SELECT_COUNT_BY_DESCRIPTION, Long.class, newDescription));
    }



    @Test
    void getItem() {
        Long id = item1.getId();

        ItemExtendDto actual = service.getItem(id);

        assertEquals(id, actual.getId());
        assertEquals(item1.getName(), actual.getName());
        assertEquals(item1.getDescription(), actual.getDescription());
        assertEquals(item1.getAvailable(), actual.getAvailable());
        assertEquals(item1.getOwner().getId(), actual.getId());
        assertEquals(commentMapper.toDto(List.of(comment1)), actual.getComments());
        assertNull(actual.getLastBooking());
        assertNull(actual.getNextBooking());
    }

    @Test
    void getUserItems() {
        long userId = user1.getId();
        Collection<ItemExtendDto> expected = List.of(
                itemMapper.toExtendDto(item1, List.of(comment1), null, null),
                itemMapper.toExtendDto(item2, List.of(), booking2, booking2)
        );

        Collection<ItemExtendDto> actual = service.getUserItems(userId);
        assertEquals(expected, actual);
    }

    @Test
    void searchByText_whenNameContainsText() {
        String textForFind = "item";
        Collection<ItemDto> expected = itemMapper.toDto(Set.of(item1));

        Collection<ItemDto> actual = service.searchByText(textForFind);
        assertEquals(expected, actual);
    }

    @Test
    void searchByText_whenDescriptionContainsText() {
        String textForFind = "description";
        Collection<ItemDto> expected = itemMapper.toDto(Set.of(item1));

        Collection<ItemDto> actual = service.searchByText(textForFind);
        assertEquals(expected, actual);
    }

    @Test
    void searchByText_whenTextIsBlank_thenReturnEmptyCollection() {
        Collection<ItemDto> actual = service.searchByText("");
        assertTrue(actual.isEmpty());
    }

    @Test
    void deleteItem() {
        Long itemId = item1.getId();
        Long userId = user1.getId();

        assertEquals(1L, jdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Long.class, itemId));

        service.deleteItem(itemId, userId);
        em.flush();

        assertEquals(0L, jdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Long.class, itemId));
    }

    @Test
    void deleteItem_whenUserIsNotOwner_thenThrowException() {
        long itemId = item1.getId();
        long userId = 2L;

        assertEquals(1L, jdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Long.class, itemId));
        assertThrows(NotFoundException.class, () -> service.deleteItem(itemId, userId));
        assertEquals(1L, jdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Long.class, itemId));
    }

    @Test
    void addComment() {
        Long itemId = item1.getId();
        Long userId = user2.getId();
        String text = "new_comment";
        CommentCreateDto commentDto = new CommentCreateDto(text);

        assertEquals(0L, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE text = ?", Long.class, text));
        CommentDto actual = service.addComment(commentDto, itemId, userId);
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM comments WHERE text = ?", text);

        assertEquals(savedRow.get("id"), actual.getId());
        assertEquals(itemId, savedRow.get("item_id"));
        assertEquals(userId, savedRow.get("author_id"));
        assertEquals(user2.getName(), actual.getAuthorName());
        assertEquals(text, savedRow.get("text"));
        assertEquals(text, actual.getText());;
        assertEquals(((Timestamp) savedRow.get("created")).toLocalDateTime(), actual.getCreated());
    }

    @Test
    void addComment_whenUserNoBooking_thenThrowException() {
        Long itemId = item2.getId();
        Long userId = user2.getId();
        String text = "new_comment";
        CommentCreateDto commentDto = new CommentCreateDto(text);

        assertEquals(0L, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE text = ?", Long.class, text));
        assertThrows(CommentException.class, () -> service.addComment(commentDto, itemId, userId));
        assertEquals(0L, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE text = ?", Long.class, text));
    }
}