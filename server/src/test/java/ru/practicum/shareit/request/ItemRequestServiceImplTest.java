package ru.practicum.shareit.request;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponses;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestServiceImpl service;
    private final TestData testData;
    private final JdbcTemplate jdbcTemplate;
    private final ItemRequestMapper mapper;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item2;


    @PostConstruct
    private void setTestData() {
        user1 = testData.getUser1();
        user2 = testData.getUser2();
        request1 = testData.getRequest1();
        request2 = testData.getRequest2();
        item2 = testData.getItem2();
    }

    @Test
    void createNewRequest() {
        ItemRequestDto createDto = new ItemRequestDto();
        String description = "new_item_request";
        createDto.setDescription(description);
        Long userId = user2.getId();

        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM requests WHERE description = ?", Long.class, description));
        ItemRequestDto actual = service.createNewRequest(createDto, userId);
        Map<String, Object> savedRow = jdbcTemplate.queryForMap("SELECT * FROM requests WHERE description = ?", description);

        assertEquals(actual.getId(), savedRow.get("id"));
        assertEquals(description, actual.getDescription());
        assertEquals(actual.getCreated(), ((Timestamp) savedRow.get("created")).toLocalDateTime());
    }

    @Test
    void getUserRequests() {
        List<ItemRequestWithResponses> expected = List.of(
                new ItemRequestWithResponses(
                        request2.getId(),
                        request2.getDescription(),
                        request2.getCreated(),
                        List.of()),

                new ItemRequestWithResponses(
                        request1.getId(),
                        request1.getDescription(),
                        request1.getCreated(),
                        List.of(new ItemResponse(item2.getId(), item2.getName(), item2.getOwner().getId())))
        );

        List<ItemRequestWithResponses> actual = service.getUserRequests(user2.getId());
        assertEquals(expected, actual);
    }

    @Test
    void getOtherUsersRequests() {
        long userId = user1.getId();
        List<ItemRequestDto> expected = mapper.toItemRequestDto(List.of(request2, request1));

        List<ItemRequestDto> actual = service.getOtherUsersRequests(userId);
        assertEquals(expected, actual);
    }

    @Test
    void getRequest() {
        ItemRequestWithResponses expected = new ItemRequestWithResponses(
                request1.getId(),
                request1.getDescription(),
                request1.getCreated(),
                List.of(new ItemResponse(item2.getId(), item2.getName(), item2.getOwner().getId()))
        );

        ItemRequestWithResponses actual = service.getRequest(request1.getId());
        assertEquals(expected, actual);
    }

    @Test
    void getRequest_whenNotExist_thenThrow() {
        long requestId = -1;
        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM requests WHERE id = ?", Long.class, requestId));

        assertThrows(NotFoundException.class, () -> service.getRequest(requestId));
    }
}