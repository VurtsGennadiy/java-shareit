package ru.practicum.shareit.booking;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.EntityGraph;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bookings")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Booking.withItem", attributeNodes = {@NamedAttributeNode("item")}),
        @NamedEntityGraph(name = "Booking.withItemAndBooker", attributeNodes = {
                @NamedAttributeNode("item"),
                @NamedAttributeNode("booker")
        })
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User booker;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.WAITING;

    public static enum Status {
        WAITING,
        APPROVED,
        REJECTED,
    }
}
