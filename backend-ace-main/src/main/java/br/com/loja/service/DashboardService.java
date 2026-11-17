package br.com.loja.service;

import br.com.loja.dto.CatalogDtos.*;
import br.com.loja.dto.DashboardDtos.*;
import br.com.loja.exception.ApiException;
import br.com.loja.model.Installment;
import br.com.loja.repository.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {
  private final ProductRepository productRepository;
  private final InstallmentRepository installmentRepository;
  private final CatalogService catalogService;
  private final CustomerRepository customerRepository;
  private final BirthdayBenefitRepository birthdayBenefitRepository;

  private InstallmentView view(Installment installment) {
    var purchaseOrder = installment.getOrder();
    return new InstallmentView(
        installment.getId().toString(),
        purchaseOrder.getId().toString(),
        purchaseOrder.getCustomer().getId().toString(),
        purchaseOrder.getCustomerName(),
        purchaseOrder.getCustomerPhone(),
        installment.getNumber(),
        installment.getAmount(),
        installment.getDueDate(),
        installment.getStatus(),
        installment.getPaidAt());
  }

  public Alerts alerts(int days) {
    validateAlertDays(days);
    LocalDate today = LocalDate.now();
    return new Alerts(
        productRepository.lowStockCount(),
        installmentRepository.countByStatusAndDueDateBefore("PENDING", today),
        installmentRepository.countByStatusAndDueDateBetween(
            "PENDING", today, today.plusDays(days)));
  }

  private void validateAlertDays(int days) {
    if (days < 1 || days > 90)
      throw new ApiException(400, "A antecedência deve estar entre 1 e 90 dias");
  }

  public PageView<ProductView> lowStock(int page, int size) {
    return PageView.from(
        productRepository.lowStock(CatalogService.page(page, size)).map(catalogService::view));
  }

  public PageView<InstallmentView> overdue(int page, int size) {
    return PageView.from(
        installmentRepository
            .findByStatusAndDueDateBefore(
                "PENDING", LocalDate.now(), CatalogService.page(page, size))
            .map(this::view));
  }

  public PageView<InstallmentView> upcoming(int page, int size, int days) {
    validateAlertDays(days);
    LocalDate today = LocalDate.now();
    return PageView.from(
        installmentRepository
            .findByStatusAndDueDateBetween(
                "PENDING", today, today.plusDays(days), CatalogService.page(page, size))
            .map(this::view));
  }

  public List<BirthdayCustomerView> birthdays(boolean todayOnly) {
    LocalDate today = LocalDate.now();
    return customerRepository.findCustomersWithBirthdayInMonth(today.getMonthValue()).stream()
        .filter(customer -> customer.getBirthday() != null)
        .filter(customer -> !todayOnly
            || (customer.getBirthday().getMonthValue() == today.getMonthValue()
                && customer.getBirthday().getDayOfMonth() == today.getDayOfMonth()))
        .map(customer -> new BirthdayCustomerView(
            customer.getId().toString(),
            customer.getName(),
            customer.getPhone(),
            customer.getBirthday(),
            birthdayBenefitRepository.existsByCustomerIdAndYear(
                customer.getId(), today.getYear())))
        .toList();
  }

  public ReceivablesView receivables(int month, int year, int page, int size) {
    final YearMonth requestedMonth;
    try {
      requestedMonth = YearMonth.of(year, month);
    } catch (DateTimeException exception) {
      throw new ApiException(400, "Mês ou ano inválido");
    }
    LocalDate start = requestedMonth.atDay(1);
    LocalDate end = requestedMonth.atEndOfMonth();
    var total = installmentRepository.sumAmountByStatusAndDueDateBetween(
        "PENDING", start, end);
    var installmentsPage = installmentRepository.findByStatusAndDueDateBetween(
        "PENDING", start, end, CatalogService.page(page, size));
    return new ReceivablesView(
        month,
        year,
        PricingCalculator.money(total),
        PageView.from(installmentsPage.map(this::view)));
  }

  public PageView<InstallmentView> installments(int page, int size, String status) {
    if (status != null && !List.of("PENDING", "PAID", "CANCELLED").contains(status))
      throw new ApiException(400, "Status inválido");
    var paging = CatalogService.page(page, size);
    return PageView.from(
        (status == null
                ? installmentRepository.findAll(paging)
                : installmentRepository.findByStatus(status, paging))
            .map(this::view));
  }
}
