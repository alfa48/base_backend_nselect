package co.ao.base.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @JsonProperty("public_id")
    private String publicId;

    private String nome;
    private String username;
    private String role;
    private String email;
    private String foto;

    private String createdAt;
    private String updatedAt;
    private String criadorPublicId;
    private String criadorNome;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
