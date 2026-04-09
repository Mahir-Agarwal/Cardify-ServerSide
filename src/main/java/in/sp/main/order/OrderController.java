package in.sp.main.order;

import in.sp.main.core.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/request")
    public ResponseEntity<OrderResponseDTO> createRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderService.createOrderRequest(userDetails.getId(), request));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<OrderResponseDTO> acceptOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.acceptOrder(userDetails.getId(), id));
    }

    @PutMapping("/{id}/confirm-info")
    public ResponseEntity<OrderResponseDTO> confirmInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.confirmInfo(userDetails.getId(), id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponseDTO> payForOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.payForOrder(userDetails.getId(), id));
    }

    @PutMapping("/{id}/place")
    public ResponseEntity<OrderResponseDTO> placeExternalOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id,
            @RequestParam("externalOrderId") String externalOrderId) {
        return ResponseEntity.ok(orderService.placeOrder(userDetails.getId(), id, externalOrderId));
    }

    @PutMapping("/{id}/delivered")
    public ResponseEntity<OrderResponseDTO> confirmDelivery(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.markDeliveredAndComplete(userDetails.getId(), id));
    }

    @PutMapping("/{id}/dispute")
    public ResponseEntity<OrderResponseDTO> disputeOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.disputeOrder(userDetails.getId(), id));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userDetails.getId()));
    }
}
