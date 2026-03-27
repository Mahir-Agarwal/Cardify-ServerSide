package in.sp.main.order;

import in.sp.main.core.constants.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findByOwnerId(Long ownerId);
    List<Order> findByStatus(OrderStatus status);
}
