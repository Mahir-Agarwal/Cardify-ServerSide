package in.sp.main.card;

import lombok.Data;

@Data
public class CardResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private Double ownerRating;
    private String bankName;
    private CardType cardType;
    private boolean available;
}
