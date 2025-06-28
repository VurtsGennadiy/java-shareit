package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc

class ItemControllerTest {
    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private final Long userId = 1L;

    @PostConstruct
    private void setData() {
        itemDto = new ItemDto(1L, "item1", "description1", Boolean.TRUE, null);
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("comment_author_name")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .build();
    }

    @SneakyThrows
    @Test
    void create() {
        ItemDto createDto = new ItemDto(null, "item1", "description1", Boolean.TRUE, null);

        when(service.createNewItem(createDto, userId)).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));

        verify(service, times(1)).createNewItem(createDto, userId);
    }

    @SneakyThrows
    @Test
    void getItem() {
        Long itemId = itemDto.getId();
        ItemExtendDto itemExtendDto = ItemExtendDto.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .comments(List.of(commentDto))
                .lastBooking(null)
                .nextBooking(null)
                .build();

        when(service.getItem(itemId)).thenReturn(itemExtendDto);

        mvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemExtendDto)));

        verify(service, times(1)).getItem(itemId);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        String findString = "item1";
        when(service.searchByText(findString)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", findString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        verify(service, times(1)).searchByText(findString);
    }

    @SneakyThrows
    @Test
    void addComment() {
        long commentAuthorId = 2L;
        long itemId = itemDto.getId();
        CommentCreateDto commentCreateDto = new CommentCreateDto("comment");
        when(service.addComment(commentCreateDto, itemId, commentAuthorId)).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", commentAuthorId)
                .content(objectMapper.writeValueAsString(commentCreateDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        verify(service, times(1)).addComment(commentCreateDto, itemId, commentAuthorId);
    }
}