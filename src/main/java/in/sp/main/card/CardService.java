package in.sp.main.card;

import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.constants.Role;
import in.sp.main.core.exception.CustomException;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    public CardResponse addCard(Long userId, CardRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "User not found", HttpStatus.NOT_FOUND));

        if (!user.getRoles().contains(Role.OWNER)) {
            user.getRoles().add(Role.OWNER); // Auto-promote to owner
            userRepository.save(user);
        }

        Card card = new Card();
        card.setOwner(user);
        card.setBankName(request.getBankName());
        card.setCardType(request.getCardType());
        card.setAvailable(request.isAvailable());

        return mapToDTO(cardRepository.save(card));
    }

    public Page<CardResponse> searchCards(String bankName, CardType cardType, Boolean isAvailable, Pageable pageable) {
        return cardRepository.searchCards(bankName, cardType, isAvailable, pageable).map(this::mapToDTO);
    }

    @Transactional
    public CardResponse updateCard(Long userId, Long cardId, CardRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Card not found", HttpStatus.NOT_FOUND));

        if (card.isDeleted()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Card is deleted", HttpStatus.NOT_FOUND);
        }

        if (!card.getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You do not own this card", HttpStatus.FORBIDDEN);
        }

        card.setBankName(request.getBankName());
        card.setCardType(request.getCardType());
        card.setAvailable(request.isAvailable());

        return mapToDTO(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Card not found", HttpStatus.NOT_FOUND));

        if (!card.getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "You do not own this card", HttpStatus.FORBIDDEN);
        }

        card.setDeleted(true);
        cardRepository.save(card);
    }

    private CardResponse mapToDTO(Card card) {
        CardResponse dto = new CardResponse();
        dto.setId(card.getId());
        dto.setOwnerId(card.getOwner().getId());
        dto.setOwnerName(card.getOwner().getName());
        dto.setOwnerRating(card.getOwner().getRating());
        dto.setBankName(card.getBankName());
        dto.setCardType(card.getCardType());
        dto.setAvailable(card.isAvailable());
        return dto;
    }
}
