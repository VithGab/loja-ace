package br.com.loja.repository;

import br.com.loja.model.PurchaseOrder;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from PurchaseOrder e where e.id = :id")
  Optional<PurchaseOrder> lockById(@Param("id") Long id);

  List<PurchaseOrder> findByCustomerIdOrderByCreatedAtDesc(Long id);

  Page<PurchaseOrder> findByCustomerIdOrderByCreatedAtDesc(Long id, Pageable pageable);

  Optional<PurchaseOrder> findByIdempotencyKey(String idempotencyKey);

  long countByCouponIdAndStatusNot(Long id, String status);

  @Query(""" 
    SELECT o FROM PurchaseOrder o 
    WHERE o.createdAt >= :startInstant
    AND o.createdAt < :endInstant
    AND (:status IS NULL OR o.status = :status)
    AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
    ORDER BY o.createdAt DESC
    """)
   List<PurchaseOrder> findForSalesReport(
    @Param("startInstant") Instant startInstant,
    @Param("endInstant") Instant endInstant,
    @Param("status") String status,
    @Param("paymentStatus") String paymentStatus);

}
