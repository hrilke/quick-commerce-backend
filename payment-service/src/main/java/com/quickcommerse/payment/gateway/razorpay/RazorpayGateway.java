package com.quickcommerse.payment.gateway.razorpay;

import com.quickcommerse.payment.gateway.exception.GatewayException;
import com.quickcommerse.payment.gateway.PaymentGateway;
import com.quickcommerse.payment.gateway.dto.PaymentGatewayResponse;
import com.quickcommerse.payment.gateway.dto.PaymentRequest;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

@Component("RAZORPAY")
public class RazorpayGateway implements PaymentGateway {

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    @Override
    public PaymentGatewayResponse createPayment(PaymentRequest request) throws GatewayException {
        if (isBlank(keyId) || isBlank(keySecret)) {
            throw new GatewayException("Razorpay credentials are not configured");
        }
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmountCents());
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", request.getOrderId().toString());
            orderRequest.put("payment_capture", 1);
            Order order = client.orders.create(orderRequest);

            Map<String, Object> clientPayload = new HashMap<>();
            clientPayload.put("keyId", keyId);
            clientPayload.put("orderId", order.get("id"));
            clientPayload.put("amount", order.get("amount"));
            clientPayload.put("currency", order.get("currency"));

            PaymentGatewayResponse resp = new PaymentGatewayResponse();
            resp.setSuccess(true);
            resp.setGatewayPaymentId(order.get("id"));
            resp.setStatus("CREATED");
            resp.setClientPayload(clientPayload);
            resp.setMessage("Order created");
            return resp;
        } catch (Exception e) {
            throw new GatewayException("Failed to create Razorpay order", e);
        }
    }

    @Override
    public PaymentGatewayResponse capturePayment(String gatewayPaymentId, long amountCents) throws GatewayException {
        if (isBlank(keyId) || isBlank(keySecret)) {
            throw new GatewayException("Razorpay credentials are not configured");
        }
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amountCents);
            captureRequest.put("currency", "INR");
            Payment payment = client.payments.capture(gatewayPaymentId, captureRequest);

            PaymentGatewayResponse resp = new PaymentGatewayResponse();
            String status = payment.get("status");
            resp.setSuccess("captured".equalsIgnoreCase(status));
            resp.setGatewayPaymentId(payment.get("id"));
            resp.setStatus(status);
            resp.setMessage("Payment capture attempted");
            return resp;
        } catch (Exception e) {
            throw new GatewayException("Failed to capture Razorpay payment", e);
        }
    }

    @Override
    public PaymentGatewayResponse refundPayment(String gatewayPaymentId, long amountCents) throws GatewayException {
        if (isBlank(keyId) || isBlank(keySecret)) {
            throw new GatewayException("Razorpay credentials are not configured");
        }
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amountCents);
            Refund refund = client.payments.refund(gatewayPaymentId, refundRequest);

            PaymentGatewayResponse resp = new PaymentGatewayResponse();
            String status = refund.get("status");
            resp.setSuccess("processed".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status));
            resp.setGatewayPaymentId(refund.get("id"));
            resp.setStatus(status);
            resp.setMessage("Refund initiated");
            return resp;
        } catch (Exception e) {
            throw new GatewayException("Failed to refund Razorpay payment", e);
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signatureHeader) {
        try {
            if (isBlank(webhookSecret) || isBlank(signatureHeader)) return false;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = toHex(hash);
            return expected.equals(signatureHeader);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
