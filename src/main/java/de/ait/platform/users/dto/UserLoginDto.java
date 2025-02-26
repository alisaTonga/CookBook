package de.ait.platform.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserLoginDto {

    @Schema(description = "User name", example = "john_doe")
    private String username;

    @Schema(description = "User password", example = "pasSword123 ", minLength = 8)
    private String password;
}
