package ru.practicum.shareit.user;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class UserServiceImplTest {
    private final UserServiceImpl service;
    private final TestData testData;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    private User user1;

    @PostConstruct
    private void setTestData() {
        user1 = testData.getUser1();
    }

    @Test
    void createNewUser_whenEmailNotExist_UserSaved() {
        String email = "user@practicum";
        UserDto createDto = UserDto.builder().name("user_name").email(email).build();

        // проверяем что пользователя не было в БД
        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Long.class, email));

        UserDto actual = service.createNewUser(createDto);
        Map<String, Object> savedObject = jdbcTemplate.queryForMap("SELECT * FROM users WHERE email = ?", email);

        assertEquals(createDto.getName(), savedObject.get("name"));
        assertEquals(createDto.getEmail(), savedObject.get("email"));
        assertEquals(createDto.getName(), actual.getName());
        assertEquals(createDto.getEmail(), actual.getEmail());
        assertEquals(savedObject.get("id"), actual.getId());
    }

    @Test
    void createNewUser_whenEmailExist_thenUserNotSavedAndThrownException() {
        String email = user1.getEmail();
        UserDto createDto = UserDto.builder().name("user_name").email(email).build();

        // проверяем что пользователь был в БД
        assertEquals(1L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Long.class, email));

        assertThrows(DataIntegrityViolationException.class, () -> service.createNewUser(createDto));

        // запись в БД не изменилась
        Map<String, Object> savedObject = jdbcTemplate.queryForMap("SELECT * FROM users WHERE email = ?", email);
        assertEquals(user1.getName(), savedObject.get("name"));
        assertNotEquals(createDto.getName(), savedObject.get("name"));
    }

    @Test
    void getUser_whenUserExist_thenReturnUserDto() {
        Long userId = user1.getId();
        UserDto expected = new UserDto(userId, user1.getName(), user1.getEmail());

        UserDto actual = service.getUser(userId);
        assertEquals(expected, actual);
    }

    @Test
    void getUser_whenUserNotExist_thenThrownException() {
        long userId = -1L;

        // проверяем что пользователя нет в БД
        assertEquals(0,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long.class, userId));

        assertThrows(NotFoundException.class, () -> service.getUser(userId));
    }

    @Test
    void updateUser_whenDtoFieldNotNull_updateUserField() {
        long userId = user1.getId();
        String oldEmail = user1.getEmail();
        String newEmail = "updated.email@practicum";
        UserDto dto = UserDto.builder().email(newEmail).build();

        // проверяем что пользователь есть в БД
        assertEquals(1L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Long.class, oldEmail));
        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Long.class, newEmail));

        UserDto actual = service.updateUser(dto, userId);
        // после обновления сущности EntityManager может сразу не отправить обновленную сущность в БД,
        // для принудительной синхронизации с БД вызываем flush
        em.flush();

        assertEquals(0L,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Long.class, oldEmail));
        Map<String, Object> savedObject = jdbcTemplate.queryForMap("SELECT * FROM users WHERE email = ?", newEmail);

        assertEquals(userId, savedObject.get("id"));
        assertEquals(userId, actual.getId());
        assertEquals(newEmail, savedObject.get("email"));
        assertEquals(newEmail, actual.getEmail());
        assertEquals(user1.getName(), actual.getName());
        assertEquals(user1.getName(), savedObject.get("name"), "user name не должно измениться");
    }

    @Test
    void updateUser_whenUserNotExist_thenThrownException() {
        long userId = -1L;

        // проверяем что пользователя нет в БД
        assertEquals(0,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long.class, userId));

        assertThrows(NotFoundException.class, () -> service.updateUser(new UserDto(), userId));
    }

    @Test
    void deleteUser() {
        long userId = user1.getId();

        service.deleteUser(userId);
        em.flush();

        assertEquals(0,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = ?", Long.class, userId));
    }
}