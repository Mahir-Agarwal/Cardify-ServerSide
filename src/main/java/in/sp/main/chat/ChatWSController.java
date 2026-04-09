package in.sp.main.chat;

import in.sp.main.core.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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
    public void sendMessage(Principal principal, @Payload ChatWSMessage chatWSMessage) {
        UserDetailsImpl userDetails = (UserDetailsImpl) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long senderId = userDetails.getId();
        
        // Persist message for history
        ChatRequest request = new ChatRequest();
        request.setMessage(chatWSMessage.getMessage());
        ChatResponse response = chatService.sendMessage(senderId, chatWSMessage.getOrderId(), request);

        // Broadcast to the order's topic (Both participants are subscribed)
        messagingTemplate.convertAndSend("/topic/order." + chatWSMessage.getOrderId(), response);
    }

    /**
     * Handles WebRTC Signaling (OFFER, ANSWER, ICE_CANDIDATE).
     * These messages are NOT persisted and are forwarded directly to the recipient.
     */
    @MessageMapping("/chat.signal")
    public void handleSignal(Principal principal, @Payload ChatWSMessage signal) {
        UserDetailsImpl userDetails = (UserDetailsImpl) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long senderId = userDetails.getId();
        
        // Explicitly set senderId to prevent "Echo Ghosting" in the mobile UI
        signal.setSenderId(senderId);
        
        // Broadcast signaling data (OFFER/ANSWER/ICE) to the order topic
        messagingTemplate.convertAndSend("/topic/order." + signal.getOrderId(), signal);
    }
}
