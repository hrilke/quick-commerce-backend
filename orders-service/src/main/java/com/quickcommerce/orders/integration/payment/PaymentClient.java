package com.quickcommerce.orders.integration.payment;

import com.quickcommerce.orders.integration.payment.dto.PaymentProcessResponse;
import com.quickcommerce.orders.integration.payment.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping(path = "/api/payments", consumes = MediaType.APPLICATION_JSON_VALUE)
    PaymentProcessResponse createPayment(@RequestParam(value = "gateway", required = false) String gateway,
                                         @RequestBody PaymentRequest request);

    @GetMapping(path = "/api/payments/{paymentId}")
    PaymentProcessResponse getPayment(@PathVariable("paymentId") UUID paymentId);
}
