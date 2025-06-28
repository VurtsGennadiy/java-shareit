package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;
import ru.practicum.shareit.request.dto.ItemResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemRequestController.class)
@Import(ItemRequestMapperImpl.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRequestMapperImpl itemRequestMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestWithResponses itemRequestWithResponses;
    private final Long userId = 2L;

    @PostConstruct
    private void setData() {
        itemRequestDto = new ItemRequestDto(
                1L, "description", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        ItemResponse itemResponse = new ItemResponse(
                1L, "item_name", 1L
        );

        itemRequestWithResponses = itemRequestMapper.toItemRequestWithResponses(itemRequestDto, List.of(itemResponse));
    }

    @SneakyThrows
    @Test
    void createNewItemRequest() {
        ItemRequestDto createDto = new ItemRequestDto();
        createDto.setDescription("description");
        when(service.createNewRequest(createDto, userId)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
        verify(service, times(1)).createNewRequest(createDto, userId);
    }

    @SneakyThrows
    @Test
    void getUserItemRequests() {
        when(service.getUserRequests(userId)).thenReturn(List.of(itemRequestWithResponses));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestWithResponses))));

        verify(service, times(1)).getUserRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequests() {
        long userId = 3L;
        when(service.getOtherUsersRequests(userId)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(service, times(1)).getOtherUsersRequests(userId);
    }

    @SneakyThrows
    @Test
    void getItemRequest() {
        long itemRequestId = itemRequestDto.getId();
        when(service.getRequest(itemRequestId)).thenReturn(itemRequestWithResponses);

        mvc.perform(get("/requests/{itemRequestId}", itemRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestWithResponses)));

        verify(service, times(1)).getRequest(itemRequestId);
    }
}