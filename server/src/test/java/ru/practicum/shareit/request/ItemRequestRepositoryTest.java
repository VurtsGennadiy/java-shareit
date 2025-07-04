package ru.practicum.shareit.request;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestData.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final TestData testData;
    private final ItemRequestRepository repository;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private User user1;
    private User user2;

    @PostConstruct
    private void getTestData() {
        itemRequest1 = testData.getRequest1();
        itemRequest2 = testData.getRequest2();
        user1 = testData.getUser1();
        user2 = testData.getUser2();
    }

    @Test
    void findAllByAuthor_IdNotOrderByCreatedDesc() {
        List<ItemRequest> expected = List.of(itemRequest2, itemRequest1);

        List<ItemRequest> actual = repository.findAllByAuthor_IdNotOrderByCreatedDesc(user1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void findAllByAuthorOrderByCreatedDesc() {
        List<ItemRequest> expected = List.of(itemRequest2, itemRequest1);

        List<ItemRequest> actual = repository.findAllByAuthorOrderByCreatedDesc(user2);

        assertEquals(expected, actual);
    }
}
