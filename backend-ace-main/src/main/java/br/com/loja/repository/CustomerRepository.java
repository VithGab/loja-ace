package br.com.loja.repository;

import br.com.loja.model.Customer;
import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.*;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  boolean existsByPhone(String phone);


  Optional<Customer> findByPhone(String phone);

  @Query(
      value = "select * from customer "
          + "where extract(month from birthday) = :month "
          + "order by extract(day from birthday), name",
      nativeQuery = true)
  List<Customer> findCustomersWithBirthdayInMonth(@Param("month") int month);

}
