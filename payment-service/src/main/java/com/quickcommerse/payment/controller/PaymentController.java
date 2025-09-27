package com.quickcommerse.payment.controller;

import com.quickcommerse.payment.dto.PaymentProcessResponse;
import com.quickcommerse.payment.gateway.dto.PaymentRequest;
import com.quickcommerse.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentProcessResponse> create(@RequestParam(value = "gateway", required = false) String gateway,
                                                         @Valid @RequestBody PaymentRequest request) {
        PaymentProcessResponse response = paymentService.createPayment(gateway, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/{paymentId}/capture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentProcessResponse> capture(@PathVariable("paymentId") String paymentId,
                                                          @RequestParam(value = "gateway", required = false) String gateway,
                                                          @RequestParam("amountCents") long amountCents) {
        PaymentProcessResponse response = paymentService.capture(gateway, paymentId, amountCents);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{paymentId}/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentProcessResponse> refund(@PathVariable("paymentId") String paymentId,
                                                         @RequestParam(value = "gateway", required = false) String gateway,
                                                         @RequestParam("amountCents") long amountCents) {
        PaymentProcessResponse response = paymentService.refund(gateway, paymentId, amountCents);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(path = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> webhook(@RequestParam(value = "gateway", required = false, defaultValue = "RAZORPAY") String gateway,
                                        @RequestHeader(name = "X-Razorpay-Signature", required = false) String signature,
                                        @RequestBody String payload) {
        paymentService.handleWebhook(gateway, signature, payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentProcessResponse> getById(@PathVariable("paymentId") UUID paymentId) {
        return ResponseEntity.ok(paymentService.getById(paymentId));
    }
}
