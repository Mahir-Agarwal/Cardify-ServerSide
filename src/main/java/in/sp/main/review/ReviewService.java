package in.sp.main.review;

import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.constants.OrderStatus;
import in.sp.main.core.exception.CustomException;
import in.sp.main.order.Order;
import in.sp.main.order.OrderRepository;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse submitReview(Long reviewerId, Long orderId, ReviewRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found", HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ORDER_INVALID_STATE, "Order must be COMPLETED to submit a review", HttpStatus.BAD_REQUEST);
        }

        Long revieweeId;
        if (order.getBuyer().getId().equals(reviewerId)) {
            revieweeId = order.getOwner().getId();
        } else if (order.getOwner().getId().equals(reviewerId)) {
            revieweeId = order.getBuyer().getId();
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You are not part of this order", HttpStatus.FORBIDDEN);
        }

        User reviewer = userRepository.findById(reviewerId).orElseThrow();
        User reviewee = userRepository.findById(revieweeId).orElseThrow();

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(request.getRating());
        review.setFeedback(request.getFeedback());

        Review savedReview = reviewRepository.save(review);
        
        updateUserRating(reviewee, request.getRating());

        return mapToDTO(savedReview);
    }

    private void updateUserRating(User user, int newRating) {
        int currentReviews = user.getTotalReviews();
        double currentRating = user.getRating();
        
        double newAverage = ((currentRating * currentReviews) + newRating) / (currentReviews + 1);
        
        user.setTotalReviews(currentReviews + 1);
        user.setRating(newAverage);
        userRepository.save(user);
    }

    private ReviewResponse mapToDTO(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setOrderId(review.getOrder().getId());
        dto.setReviewerId(review.getReviewer().getId());
        dto.setRevieweeId(review.getReviewee().getId());
        dto.setRating(review.getRating());
        dto.setFeedback(review.getFeedback());
        dto.setTimestamp(review.getCreatedAt());
        return dto;
    }
}
