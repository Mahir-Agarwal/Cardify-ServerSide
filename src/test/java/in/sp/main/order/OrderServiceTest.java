package in.sp.main.order;

import in.sp.main.card.Card;
import in.sp.main.card.CardRepository;
import in.sp.main.core.constants.OrderStatus;
import in.sp.main.core.constants.Role;
import in.sp.main.core.exception.CustomException;
import in.sp.main.payment.PaymentService;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    private User buyer;
    private User owner;
    private Card card;
    private Order order;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setRoles(Set.of(Role.BUYER));

        owner = new User();
        owner.setId(2L);
        owner.setRoles(Set.of(Role.OWNER));

        card = new Card();
        card.setId(10L);
        card.setOwner(owner);
        card.setAvailable(true);
        card.setDeleted(false);

        order = new Order();
        order.setId(100L);
        order.setBuyer(buyer);
        order.setOwner(owner);
        order.setCard(card);
        order.setStatus(OrderStatus.REQUESTED);
        order.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testAcceptOrder_Success() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO response = orderService.acceptOrder(2L, 100L); // Owner accepts

        assertEquals(OrderStatus.ACCEPTED, response.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testAcceptOrder_Failure_NotOwner() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.acceptOrder(1L, 100L); // Buyer tries to accept
        });

        assertEquals("You do not have permission to modify this order", exception.getMessage());
    }

    @Test
    void testAcceptOrder_Failure_InvalidState() {
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.acceptOrder(2L, 100L); 
        });

        assertEquals("Order must be in REQUESTED state to accept", exception.getMessage());
    }

    @Test
    void testPayForOrder_Failure_AlreadyPaid() {
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.payForOrder(1L, 100L); // Buyer tries to pay again
        });

        assertEquals("Order is already paid", exception.getMessage());
    }

    @Test
    void testCreateOrder_Failure_BuyerRequestsOwnCard() {
        card.setOwner(buyer); // buyer is the owner
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCardId(10L);

        CustomException exception = assertThrows(CustomException.class, () -> {
            orderService.createOrderRequest(1L, dto); 
        });

        assertEquals("You cannot request your own card", exception.getMessage());
    }
}
