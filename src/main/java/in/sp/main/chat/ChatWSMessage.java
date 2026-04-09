package in.sp.main.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatWSMessage {
    private String type; // TEXT, OFFER, ANSWER, ICE_CANDIDATE
    private Long orderId;
    private String message; // The text content or the signaling payload
    private Long senderId;
    private Long receiverId;
}
