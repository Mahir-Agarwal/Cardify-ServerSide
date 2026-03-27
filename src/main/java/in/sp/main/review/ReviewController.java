package in.sp.main.review;

import in.sp.main.core.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ReviewResponse> submitReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.submitReview(userDetails.getId(), orderId, request));
    }
}
