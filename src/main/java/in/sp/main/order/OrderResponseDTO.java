package in.sp.main.order;

import in.sp.main.core.constants.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long buyerId;
    private Long ownerId;
    private Long cardId;
    private OrderStatus status;
    private Double amount;
    private Double commission;
    private String externalOrderId;
    private LocalDateTime createdAt;
}
