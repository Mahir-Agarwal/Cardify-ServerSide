package in.sp.main.review;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long orderId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String feedback;
    private LocalDateTime timestamp;
}
