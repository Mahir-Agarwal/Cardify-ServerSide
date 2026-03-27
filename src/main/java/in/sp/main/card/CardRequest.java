package in.sp.main.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardRequest {
    @NotBlank(message = "Bank name cannot be blank")
    private String bankName;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    private boolean available = true;
}
