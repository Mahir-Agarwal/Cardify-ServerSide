package in.sp.main.user;

import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND));
        
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "User is deleted", HttpStatus.NOT_FOUND);
        }

        return mapToDTO(user);
    }

    @Transactional
    public UserProfileDTO updateProfile(Long id, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND));

        user.setName(updateRequest.getName());
        return mapToDTO(userRepository.save(user));
    }

    private UserProfileDTO mapToDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setRating(user.getRating());
        dto.setTotalReviews(user.getTotalReviews());
        return dto;
    }
}
