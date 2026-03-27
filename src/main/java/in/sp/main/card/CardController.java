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

    @GetMapping
    public ResponseEntity<Page<CardResponse>> searchCards(
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) CardType cardType,
            @RequestParam(required = false) Boolean isAvailable,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.searchCards(bankName, cardType, isAvailable, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CardRequest request) {
        return ResponseEntity.ok(cardService.updateCard(userDetails.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        cardService.deleteCard(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
