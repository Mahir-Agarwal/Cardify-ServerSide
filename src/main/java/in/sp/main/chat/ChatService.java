package in.sp.main.chat;

import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.exception.CustomException;
import in.sp.main.order.Order;
import in.sp.main.order.OrderRepository;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatResponse sendMessage(Long senderId, Long orderId, ChatRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found", HttpStatus.NOT_FOUND));

        log.info("Chat Security Audit - Sender: {}, Order: {}, Buyer: {}, Owner: {}", 
                senderId, orderId, order.getBuyer().getId(), order.getOwner().getId());

        Long receiverId;
        if (order.getBuyer().getId().equals(senderId)) {
            receiverId = order.getOwner().getId();
        } else if (order.getOwner().getId().equals(senderId)) {
            receiverId = order.getBuyer().getId();
        } else {
            log.error("Chat Security Violation - User {} is not part of Order {}", senderId, orderId);
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You are not part of this order", HttpStatus.FORBIDDEN);
        }

        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        ChatMessage message = new ChatMessage();
        message.setOrder(order);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(request.getMessage());

        return mapToDTO(chatRepository.save(message));
    }

    public List<ChatResponse> getOrderMessages(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found", HttpStatus.NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId) && !order.getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You are not part of this order", HttpStatus.FORBIDDEN);
        }

        return chatRepository.findByOrderId(orderId, Sort.by(Sort.Direction.ASC, "createdAt"))
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ChatResponse mapToDTO(ChatMessage message) {
        ChatResponse dto = new ChatResponse();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setMessage(message.getMessage());
        dto.setTimestamp(message.getCreatedAt());
        return dto;
    }
}
