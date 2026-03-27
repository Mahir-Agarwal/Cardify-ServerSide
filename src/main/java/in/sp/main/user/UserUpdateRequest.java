package in.sp.main.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
