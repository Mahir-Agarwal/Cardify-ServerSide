package in.sp.main.payment;

import in.sp.main.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void processBuyerPayment(Order order) {
        // Escrow logic: Platform holds the money
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setAmount(order.getAmount() + order.getCommission());
        transaction.setType(TransactionType.PAYMENT_BY_BUYER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void releasePaymentToOwner(Order order) {
        // Release to Owner
        Transaction payout = new Transaction();
        payout.setOrder(order);
        payout.setAmount(order.getAmount()); // Owner gets the actual amount
        payout.setType(TransactionType.PAYOUT_TO_OWNER);
        payout.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(payout);

        // Platform keeps commission
        Transaction commission = new Transaction();
        commission.setOrder(order);
        commission.setAmount(order.getCommission());
        commission.setType(TransactionType.PLATFORM_COMMISSION);
        commission.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(commission);
    }
}
