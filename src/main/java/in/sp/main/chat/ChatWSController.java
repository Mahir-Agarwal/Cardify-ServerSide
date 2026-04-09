package in.sp.main.chat;

import in.sp.main.core.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWSController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * Handles real-time text chat messages.
     * Persists them in the database for history and broadcasts to the recipient.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@AuthenticationPrincipal UserDetailsImpl userDetails, @Payload ChatWSMessage chatWSMessage) {
        // Persist message for history
        ChatRequest request = new ChatRequest();
        request.setMessage(chatWSMessage.getMessage());
        ChatResponse response = chatService.sendMessage(userDetails.getId(), chatWSMessage.getOrderId(), request);

        // Broadcast to the order's topic (Both participants are subscribed)
        messagingTemplate.convertAndSend("/topic/order." + chatWSMessage.getOrderId(), response);
    }

    /**
     * Handles WebRTC Signaling (OFFER, ANSWER, ICE_CANDIDATE).
     * These messages are NOT persisted and are forwarded directly to the recipient.
     */
    @MessageMapping("/chat.signal")
    public void handleSignal(@AuthenticationPrincipal UserDetailsImpl userDetails, @Payload ChatWSMessage signal) {
        // Security check: ensure user is part of the order (already handled by WebSocket interceptor context if needed, 
        // but here we just route it)
        
        // Forward signaling data directly to the recipient's personal queue or order topic
        // We broadcast to the order topic so the other participant receives it
        messagingTemplate.convertAndSend("/topic/order." + signal.getOrderId(), signal);
    }
}
