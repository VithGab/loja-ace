package br.com.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.loja.dto.OrderDtos.OrderView;

public record SalesReportView(
    LocalDate startDate,
    LocalDate endDate,
    long totalOrders,
    BigDecimal totalRevenue,
    BigDecimal averageTicket,
    List<OrderView> orders) {
}
