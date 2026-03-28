package in.sp.main.order;

import in.sp.main.core.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmDelivery(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.markDeliveredAndComplete(userDetails.getId(), id));
    }
}
