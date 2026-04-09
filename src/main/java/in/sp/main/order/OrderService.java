package in.sp.main.order;

import in.sp.main.card.Card;
import in.sp.main.card.CardRepository;
import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.constants.OrderStatus;
import in.sp.main.core.exception.CustomException;
import in.sp.main.payment.PaymentService;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Transactional
    public OrderResponseDTO createOrderRequest(Long buyerId, OrderRequestDTO requestDTO) {
        User buyer = userRepository.findById(buyerId).orElseThrow();
        Card card = cardRepository.findById(requestDTO.getCardId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Card not found", HttpStatus.NOT_FOUND));

        if (!card.isAvailable() || card.isDeleted()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "Card is not available", HttpStatus.BAD_REQUEST);
        }

        if (card.getOwner().getId().equals(buyerId)) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "You cannot request your own card", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOwner(card.getOwner());
        order.setCard(card);
        order.setAmount(requestDTO.getAmount());
        order.setCommission(requestDTO.getCommission());
        order.setStatus(OrderStatus.REQUESTED);

        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO acceptOrder(Long ownerId, Long orderId) {
        Order order = getOrderAndValidateOwnership(orderId, ownerId, false);

        if (order.getStatus() != OrderStatus.REQUESTED) {
            throw new CustomException(ErrorCode.ORDER_INVALID_STATE, "Order must be in REQUESTED state to accept", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(OrderStatus.ACCEPTED);
        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO payForOrder(Long buyerId, Long orderId) {
        Order order = getOrderAndValidateOwnership(orderId, buyerId, true);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new CustomException(ErrorCode.DOUBLE_PAYMENT_ATTEMPT, "Order is already paid", HttpStatus.CONFLICT);
        }
        
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.ORDER_INVALID_STATE, "Order must be in ACCEPTED state to pay", HttpStatus.BAD_REQUEST);
        }

        // Simulate Escrow Payment
        paymentService.processBuyerPayment(order);
        order.setStatus(OrderStatus.PAID);
        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO placeOrder(Long ownerId, Long orderId, String externalOrderId) {
        Order order = getOrderAndValidateOwnership(orderId, ownerId, false);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new CustomException(ErrorCode.ORDER_INVALID_STATE, "Order must be in PAID state to place external order", HttpStatus.BAD_REQUEST);
        }

        order.setExternalOrderId(externalOrderId);
        order.setStatus(OrderStatus.ORDER_PLACED);
        return mapToDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO markDeliveredAndComplete(Long buyerId, Long orderId) {
        Order order = getOrderAndValidateOwnership(orderId, buyerId, true);

        if (order.getStatus() != OrderStatus.ORDER_PLACED && order.getStatus() != OrderStatus.DELIVERED) {
            throw new CustomException(ErrorCode.ORDER_INVALID_STATE, "Order must be placed or delivered to complete", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(OrderStatus.COMPLETED);
        
        // Release Escrow Payment to Owner & Platform
        paymentService.releasePaymentToOwner(order);

        return mapToDTO(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersForUser(Long userId) {
        List<Order> ordersAsBuyer = orderRepository.findByBuyerId(userId);
        List<Order> ordersAsOwner = orderRepository.findByOwnerId(userId);
        
        Set<Order> allOrders = new HashSet<>();
        allOrders.addAll(ordersAsBuyer);
        allOrders.addAll(ordersAsOwner);
        
        return allOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Order getOrderAndValidateOwnership(Long orderId, Long userId, boolean isBuyer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Order not found", HttpStatus.NOT_FOUND));

        Long expectedOwnerId = isBuyer ? order.getBuyer().getId() : order.getOwner().getId();
        if (!expectedOwnerId.equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You do not have permission to modify this order", HttpStatus.FORBIDDEN);
        }
        return order;
    }

    private OrderResponseDTO mapToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyer().getId());
        dto.setOwnerId(order.getOwner().getId());
        dto.setCardId(order.getCard().getId());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCommission(order.getCommission());
        dto.setExternalOrderId(order.getExternalOrderId());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
