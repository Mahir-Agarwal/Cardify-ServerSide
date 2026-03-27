package in.sp.main.chat;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;
}
