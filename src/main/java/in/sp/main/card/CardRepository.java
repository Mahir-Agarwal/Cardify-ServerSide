package in.sp.main.card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, Long> {
    
    @Query("SELECT c FROM Card c WHERE c.isDeleted = false " +
           "AND (:bankName IS NULL OR c.bankName = :bankName) " +
           "AND (:cardType IS NULL OR c.cardType = :cardType) " +
           "AND (:isAvailable IS NULL OR c.isAvailable = :isAvailable)")
    Page<Card> searchCards(
            @Param("bankName") String bankName,
            @Param("cardType") CardType cardType,
            @Param("isAvailable") Boolean isAvailable,
            Pageable pageable);
}
