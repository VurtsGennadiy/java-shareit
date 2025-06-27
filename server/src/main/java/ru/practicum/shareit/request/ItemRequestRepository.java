package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByAuthor_IdNotOrderByCreatedDesc(Long authorId);

    List<ItemRequest> findAllByAuthorOrderByCreatedDesc(User author);
}
