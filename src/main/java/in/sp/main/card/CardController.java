package in.sp.main.card;

import in.sp.main.core.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> addCard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CardRequest request) {
        return ResponseEntity.ok(cardService.addCard(userDetails.getId(), request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CardResponse>> searchCards(
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "cardType", required = false) CardType cardType,
            @RequestParam(value = "isAvailable", required = false) Boolean isAvailable,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.searchCards(bankName, cardType, isAvailable, pageable));
    }

    @GetMapping("/my-cards")
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getMyCards(userDetails.getId(), pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id,
            @Valid @RequestBody CardRequest request) {
        return ResponseEntity.ok(cardService.updateCard(userDetails.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id) {
        cardService.deleteCard(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
