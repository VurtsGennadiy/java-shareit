package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.validator.NotBlankOrNull;

@Data
public class UserUpdateDto {
    @NotBlankOrNull
    private String name;
    @Email
    @NotBlankOrNull
    private String email;
}
