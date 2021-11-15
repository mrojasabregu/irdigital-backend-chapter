package org.irdigital.techmeup.api.controllers;

import lombok.RequiredArgsConstructor;
import org.irdigital.techmeup.api.dto.CreateOrderDTO;
import org.irdigital.techmeup.api.services.OrderService;
import org.irdigital.techmeup.domain.entities.Order;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping("{tenantId}/orders")
    public Mono<Void> createOrder(@PathVariable("tenantId")String tenantId, @RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(tenantId, createOrderDTO);
    }

    @GetMapping("{tenantId}/orders/{orderId}")
    public Mono<Order> findOrderById(@PathVariable("tenantId")String tenantId, @PathVariable("orderId") String orderId) {
        return orderService.findOrderById(tenantId, orderId);
    }
}
