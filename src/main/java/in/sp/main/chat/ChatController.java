package in.sp.main.chat;

import in.sp.main.core.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ChatResponse> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(userDetails.getId(), orderId, request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<ChatResponse>> getOrderMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(chatService.getOrderMessages(userDetails.getId(), orderId));
    }
}
