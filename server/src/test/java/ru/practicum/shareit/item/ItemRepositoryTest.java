package ru.practicum.shareit.item;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestData.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final TestData testData;

    private User user1;
    private ItemRequest request1;
    private Item item1;
    private Item item2;

    @PostConstruct
    private void getTestData() {
        user1 = testData.getUser1();
        request1 = testData.getRequest1();
        item1 = testData.getItem1();
        item2 = testData.getItem2();
    }

    @Test
    void findByOwner_Id() {
        Long ownerId = user1.getId();
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.findByOwner_Id(ownerId);
        assertEquals(expected, actual);
    }

    @Test
    void findItemsByNameContainingIgnoreCaseAndAvailableTrue() {
        List<Item> expected = List.of(item1);
        List<Item> actual = itemRepository.findItemsByNameContainingIgnoreCaseAndAvailableTrue("ITem");
        assertEquals(expected, actual);
    }

    @Test
    void findItemsByDescriptionContainingIgnoreCaseAndAvailableTrue() {
        List<Item> expected = List.of(item1);
        List<Item> actual = itemRepository.findItemsByDescriptionContainingIgnoreCaseAndAvailableTrue("DEScription");
        assertEquals(expected, actual);
    }

    @Test
    void findItemsByRequest() {
        List<ItemShortDto> expected = List.of(new ItemShortDto(
                item2.getId(), item2.getName(), item2.getOwner().getId(), item2.getItemRequest().getId()));

        List<ItemShortDto> actual = itemRepository.findItemsByRequest(request1);

        assertEquals(expected, actual);
    }

    @Test
    void findItemsByRequests() {
        List<ItemShortDto> expected = List.of(new ItemShortDto(
                item2.getId(), item2.getName(), item2.getOwner().getId(), item2.getItemRequest().getId()));

        List<ItemShortDto> actual = itemRepository.findItemsByRequests(List.of(request1));

        assertEquals(expected, actual);
    }
}
