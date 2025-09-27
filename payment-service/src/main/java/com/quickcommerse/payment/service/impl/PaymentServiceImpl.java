package com.quickcommerse.payment.service.impl;

import com.quickcommerse.payment.dto.PaymentProcessResponse;
import com.quickcommerse.payment.gateway.PaymentGatewayFactory;
import com.quickcommerse.payment.gateway.dto.PaymentGatewayResponse;
import com.quickcommerse.payment.gateway.dto.PaymentRequest;
import com.quickcommerse.payment.model.Payment;
import com.quickcommerse.payment.model.PaymentStatus;
import com.quickcommerse.payment.repository.PaymentRepository;
import com.quickcommerse.payment.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentGatewayFactory gatewayFactory;
    private final PaymentRepository paymentRepository;
    private final String defaultGateway;

    public PaymentServiceImpl(PaymentGatewayFactory gatewayFactory,
                              PaymentRepository paymentRepository,
                              @Value("${payment.default-gateway:RAZORPAY}") String defaultGateway) {
        this.gatewayFactory = gatewayFactory;
        this.paymentRepository = paymentRepository;
        this.defaultGateway = defaultGateway;
    }

    /**
     * Create payment (persist local payment record + call gateway to create remote order).
     * <p>
     * CONTRACT:
     * Inputs: gateway (optional), PaymentRequest containing orderId,userId,amountCents,currency.
     * Outputs: PaymentProcessResponse with local paymentId, gateway and gatewayPaymentId (order id).
     * Error modes: throws GatewayException if gateway call fails; validation exceptions for bad input.
     * <p>
     * RATIONALE:
     * Persisting the payment record first ensures idempotency and a local reference for order->payment mapping.
     * The gateway call creates an order used by the frontend checkout flow; we then attach remote id to the local record.
     */
    @Override
    @Transactional
    public PaymentProcessResponse createPayment(String gateway, PaymentRequest request) {

        String gw = (gateway == null || gateway.isBlank()) ? defaultGateway : gateway.toUpperCase();

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmountCents(request.getAmountCents());
        payment.setCurrency(request.getCurrency());
        payment.setGateway(gw);
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        PaymentGatewayResponse gwResp = gatewayFactory.get(gw).createPayment(request);
        if (gwResp != null) {
            payment.setGatewayPaymentId(gwResp.getGatewayPaymentId());
            payment.setStatus(gwResp.isSuccess() ? PaymentStatus.CREATED : PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);

        PaymentProcessResponse resp = new PaymentProcessResponse();
        resp.setPaymentId(payment.getId());
        resp.setGateway(payment.getGateway());
        resp.setGatewayPaymentId(payment.getGatewayPaymentId());
        resp.setStatus(payment.getStatus());
        resp.setMessage(gwResp != null ? gwResp.getMessage() : null);
        resp.setClientPayload(gwResp != null ? gwResp.getClientPayload() : null);
        return resp;
    }

    /**
     * Capture a previously authorized payment.
     * <p>
     * CONTRACT:
     * Inputs: gateway (optional), paymentIdOrGatewayPaymentId (local UUID or remote gateway id), amountCents
     * Outputs: PaymentProcessResponse reflecting updated status (CAPTURED/FAILED).
     * Error modes: throws IllegalArgumentException if payment not found; GatewayException for remote failure.
     * <p>
     * RATIONALE:
     * The method centralizes gateway capture logic and keeps repository handling inside the service layer.
     */
    @Override
    @Transactional
    public PaymentProcessResponse capture(String gateway, String paymentIdOrGatewayPaymentId, long amountCents) {

        String gw = (gateway == null || gateway.isBlank()) ? defaultGateway : gateway.toUpperCase();

        Payment payment = resolvePayment(paymentIdOrGatewayPaymentId);

        PaymentGatewayResponse gwResp = gatewayFactory.get(gw)
                .capturePayment(payment.getGatewayPaymentId(), amountCents);

        payment.setStatus(gwResp.isSuccess() ? PaymentStatus.CAPTURED : PaymentStatus.FAILED);
        paymentRepository.save(payment);

        PaymentProcessResponse resp = new PaymentProcessResponse();
        resp.setPaymentId(payment.getId());
        resp.setGateway(payment.getGateway());
        resp.setGatewayPaymentId(payment.getGatewayPaymentId());
        resp.setStatus(payment.getStatus());
        resp.setMessage(gwResp.getMessage());
        resp.setClientPayload(gwResp.getClientPayload());
        return resp;
    }

    /**
     * Issue a refund for a captured payment (partial or full).
     * <p>
     * CONTRACT:
     * Inputs: gateway (optional), paymentIdOrGatewayPaymentId, amountCents
     * Outputs: PaymentProcessResponse with updated status (REFUNDED) if successful.
     * Error modes: throws on missing payment or gateway failure.
     * <p>
     * RATIONALE:
     * Refunds must be recorded locally and delegated to the gateway; the local payment status reflects refund progress.
     */
    @Override
    @Transactional
    public PaymentProcessResponse refund(String gateway, String paymentIdOrGatewayPaymentId, long amountCents) {
        String gw = (gateway == null || gateway.isBlank()) ? defaultGateway : gateway.toUpperCase();

        Payment payment = resolvePayment(paymentIdOrGatewayPaymentId);

        PaymentGatewayResponse gwResp = gatewayFactory.get(gw)
                .refundPayment(payment.getGatewayPaymentId(), amountCents);

        if (gwResp.isSuccess()) {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        PaymentProcessResponse resp = new PaymentProcessResponse();
        resp.setPaymentId(payment.getId());
        resp.setGateway(payment.getGateway());
        resp.setGatewayPaymentId(payment.getGatewayPaymentId());
        resp.setStatus(payment.getStatus());
        resp.setMessage(gwResp.getMessage());
        resp.setClientPayload(gwResp.getClientPayload());
        return resp;
    }

    private Payment resolvePayment(String paymentIdOrGatewayPaymentId) {
        try {
            UUID id = UUID.fromString(paymentIdOrGatewayPaymentId);
            return paymentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentIdOrGatewayPaymentId));
        } catch (IllegalArgumentException ex) {
            return paymentRepository.findByGatewayPaymentId(paymentIdOrGatewayPaymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found by gatewayPaymentId: " + paymentIdOrGatewayPaymentId));
        }
    }

    /**
     * Handle gateway webhooks and update local payment state.
     * <p>
     * CONTRACT:
     * Inputs: gateway name, signature header, raw payload.
     * Outputs: side-effect on Payment persistent state (status and gatewayPaymentId).
     * Error modes: invalid signature -> throw; invalid payload -> throw.
     * <p>
     * RATIONALE:
     * Webhooks reconcile remote gateway events (payment creation, capture, refund) with local records.
     */
    @Override
    @Transactional
    public void handleWebhook(String gateway, String signature, String payload) {

        String gw = (gateway == null || gateway.isBlank()) ? defaultGateway : gateway.toUpperCase();
        if (!gatewayFactory.get(gw).verifyWebhookSignature(payload, signature)) {
            throw new IllegalArgumentException("Invalid webhook signature");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            String event = root.path("event").asText(null);
            JsonNode payloadObj = root.path("payload");
            if (payloadObj.isMissingNode()) return;

            String razorpayOrderId = null;
            String razorpayPaymentId = null;
            if (payloadObj.has("payment")) {
                JsonNode pay = payloadObj.path("payment").path("entity");
                if (!pay.isMissingNode()) {
                    razorpayPaymentId = pay.path("id").asText(null);
                    razorpayOrderId = pay.path("order_id").asText(null);
                }
            }
            if (razorpayOrderId == null && payloadObj.has("order")) {
                JsonNode ord = payloadObj.path("order").path("entity");
                if (!ord.isMissingNode()) {
                    razorpayOrderId = ord.path("id").asText(null);
                }
            }

            Payment payment = null;
            if (razorpayPaymentId != null) {
                payment = paymentRepository.findByGatewayPaymentId(razorpayPaymentId).orElse(null);
            }
            if (payment == null && razorpayOrderId != null) {
                payment = paymentRepository.findByGatewayPaymentId(razorpayOrderId).orElse(null);
                if (payment != null && razorpayPaymentId != null) {
                    payment.setGatewayPaymentId(razorpayPaymentId);
                }
            }
            if (payment == null) return; // nothing to update

            if ("payment.authorized".equalsIgnoreCase(event)) {
                payment.setStatus(PaymentStatus.AUTHORIZED);
            } else if ("payment.captured".equalsIgnoreCase(event)) {
                payment.setStatus(PaymentStatus.CAPTURED);
            } else if ("payment.failed".equalsIgnoreCase(event)) {
                payment.setStatus(PaymentStatus.FAILED);
            } else if ("refund.processed".equalsIgnoreCase(event) || "refund.created".equalsIgnoreCase(event)) {
                payment.setStatus(PaymentStatus.REFUNDED);
            }
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid webhook payload", e);
        }
    }

    /**
     * Retrieve a payment by its local id.
     * <p>
     * CONTRACT:
     * Inputs: local payment UUID
     * Outputs: PaymentProcessResponse with current persisted state
     * Error modes: throws IllegalArgumentException if not found
     * <p>
     * RATIONALE:
     * Provides the order service with a simple polling mechanism to observe payment state.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentProcessResponse getById(UUID paymentId) {


        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        PaymentProcessResponse resp = new PaymentProcessResponse();
        resp.setPaymentId(payment.getId());
        resp.setGateway(payment.getGateway());
        resp.setGatewayPaymentId(payment.getGatewayPaymentId());
        resp.setStatus(payment.getStatus());
        return resp;
    }
}
