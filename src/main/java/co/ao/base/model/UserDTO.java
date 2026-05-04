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

    public Long getId() {
        return id;
    }

    @JsonProperty("public_id")
    @com.fasterxml.jackson.annotation.JsonAlias("publicId")
    private String publicId;

    public String getPublicId() {
        return publicId;
    }


    private String nome;
    public String getNome() {
        return nome;
    }
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("refresh_token")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
