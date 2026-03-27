package in.sp.main.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Commission is required")
    @Min(value = 1, message = "Commission must be greater than 0")
    private Double commission;
}
