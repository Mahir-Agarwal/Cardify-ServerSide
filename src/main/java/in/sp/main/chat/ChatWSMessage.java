package in.sp.main.chat;

import lombok.Data;

@Data
public class ChatWSMessage {
    private String type; // TEXT, OFFER, ANSWER, ICE_CANDIDATE
    private Long orderId;
    private String message; // The text content or the signaling payload
    private Long senderId;
    private Long receiverId;
}
