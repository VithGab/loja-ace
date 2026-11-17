package br.com.loja.dto;

import br.com.loja.dto.CatalogDtos.PageView;

import java.math.BigDecimal;
import java.time.*;

public final class DashboardDtos {

  public record Alerts(
      long lowStock, long overdueInstallments, long upcomingInstallments) {}

  public record BirthdayCustomerView(
      String customerId,
      String customerName,
      String phone,
      LocalDate birthday,
      boolean benefitGrantedThisYear) {}

  public record ReceivablesView(
      int month,
      int year,
      BigDecimal totalPending,
      PageView<InstallmentView> installments) {}


  public record InstallmentView(
      String id,
      String orderId,
      String customerId,
      String customerName,
      String customerPhone,
      int number,
      BigDecimal amount,
      LocalDate dueDate,
      String status,
      Instant paidAt) {}
}
