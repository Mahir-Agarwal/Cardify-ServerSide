package in.sp.main.user;

import in.sp.main.core.constants.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles;
    private Double rating;
    private Integer totalReviews;
}
