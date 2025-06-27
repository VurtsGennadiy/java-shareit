package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner_Id(Long ownerId);

    List<Item> findItemsByNameContainingIgnoreCaseAndAvailableTrue(String text);

    List<Item> findItemsByDescriptionContainingIgnoreCaseAndAvailableTrue(String text);

    @Query("""
            select new ru.practicum.shareit.item.dto.ItemShortDto(i.id, i.name, i.owner.id, i.itemRequest.id)
            from Item as i
            where i.itemRequest = ?1""")
    List<ItemShortDto> findItemsByRequest(ItemRequest itemRequest);

    @Query("""
            select new ru.practicum.shareit.item.dto.ItemShortDto(i.id, i.name, i.owner.id, i.itemRequest.id)
            from Item as i
            where i.itemRequest in ?1""")
    List<ItemShortDto> findItemsByRequests(Collection<ItemRequest> itemRequests);
}
