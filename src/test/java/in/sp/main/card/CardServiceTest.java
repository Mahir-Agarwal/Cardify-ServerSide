package in.sp.main.card;

import in.sp.main.core.constants.Role;
import in.sp.main.core.exception.CustomException;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    private User owner;
    private User otherUser;
    private Card card;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setRoles(new HashSet<>(Set.of(Role.OWNER)));

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRoles(new HashSet<>(Set.of(Role.BUYER)));

        card = new Card();
        card.setId(10L);
        card.setOwner(owner);
        card.setAvailable(true);
        card.setDeleted(false);
    }

    @Test
    void testUpdateCard_Failure_NotOwner() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        CardRequest request = new CardRequest();
        request.setBankName("SBI");

        CustomException exception = assertThrows(CustomException.class, () -> {
            cardService.updateCard(2L, 10L, request); // otherUser tries to update
        });

        assertEquals("You do not own this card", exception.getMessage());
    }

    @Test
    void testDeleteCard_Failure_NotOwner() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        CustomException exception = assertThrows(CustomException.class, () -> {
            cardService.deleteCard(2L, 10L); // otherUser tries to delete
        });

        assertEquals("You do not own this card", exception.getMessage());
    }

    @Test
    void testDeleteCard_Success() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        
        cardService.deleteCard(1L, 10L);
        
        assertTrue(card.isDeleted());
        verify(cardRepository).save(card);
    }
}
