package br.com.loja.controller;

import br.com.loja.dto.CatalogDtos.*;
import br.com.loja.dto.DashboardDtos.*;
import br.com.loja.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;

  @GetMapping("/alerts")
  Alerts alerts(@RequestParam(defaultValue = "7") int days) {
    return dashboardService.alerts(days);
  }

  @GetMapping("/alerts/low-stock")
  PageView<ProductView> low(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return dashboardService.lowStock(page, size);
  }

  @GetMapping("/alerts/overdue")
  PageView<InstallmentView> overdue(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return dashboardService.overdue(page, size);
  }

  @GetMapping("/alerts/upcoming")
  PageView<InstallmentView> upcoming(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "7") int days) {
    return dashboardService.upcoming(page, size, days);
  }

  @GetMapping("/birthdays")
  List<BirthdayCustomerView> birthdays(
      @RequestParam(defaultValue = "false") boolean todayOnly) {
    return dashboardService.birthdays(todayOnly);
  }

  @GetMapping("/reports/receivables")
  ReceivablesView receivables(
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer year,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    LocalDate today = LocalDate.now();
    return dashboardService.receivables(
        month == null ? today.getMonthValue() : month,
        year == null ? today.getYear() : year,
        page,
        size);
  }

  @GetMapping("/installments")
  PageView<InstallmentView> installments(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String status) {
    return dashboardService.installments(page, size, status);
  }
}
