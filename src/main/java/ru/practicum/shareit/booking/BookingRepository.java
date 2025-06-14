package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph("Booking.withItem")
    List<Booking> findAllByBookerOrderByStartDesc(User bookerId);

    @EntityGraph("Booking.withItem")
    List<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, Booking.Status status);

    @Query("""
            select b from Booking as b join fetch b.item
            where b.booker.id = ?1 and b.status = 'APPROVED'
            and b.start <= CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP
            order by b.start desc""")
    List<Booking> findCurrentBookingsByBooker_Id(long bookerId);

    @Query("""
            select b from Booking as b join fetch b.item
            where b.booker.id = ?1 and b.status = 'APPROVED'
            and  b.end <= CURRENT_TIMESTAMP
            order by b.start desc""")
    List<Booking> findPastBookingByBooker_Id(long bookerId);

    @Query("""
            select b from Booking as b join fetch b.item
            where b.booker.id = ?1 and b.status = 'APPROVED'
            and CURRENT_TIMESTAMP < b.start
            order by b.start desc""")
    List<Booking> findFutureBookingByBooker_Id(long bookerId);

    @EntityGraph("Booking.withItemAndBooker")
    List<Booking> findAllByItem_OwnerOrderByStartDesc(User itemOwner);

    @EntityGraph("Booking.withItemAndBooker")
    List<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc(User itemOwner, Booking.Status status);

    @Query("""
            select b from Booking as b join fetch b.item as i join fetch i.owner as o
            where o.id = ?1 and b.status = 'APPROVED'
            and b.start <= CURRENT_TIMESTAMP and b.end > CURRENT_TIMESTAMP
            order by b.start desc""")
    List<Booking> findCurrentBookingsByItemOwner_Id(long ownerId);

    @Query("""
            select b from Booking as b join fetch b.item as i join fetch i.owner as o
            where o.id = ?1 and b.status = 'APPROVED'
            and b.end <= CURRENT_TIMESTAMP
            order by b.start desc""")
    List<Booking> findPastBookingsByItemOwner_Id(long ownerId);

    @Query("""
            select b from Booking as b join fetch b.item as i join fetch i.owner as o
            where o.id = ?1 and b.status = 'APPROVED'
            and CURRENT_TIMESTAMP < b.start
            order by b.start desc""")
    List<Booking> findFutureBookingByItemOwner_Id(long ownerId);
}
