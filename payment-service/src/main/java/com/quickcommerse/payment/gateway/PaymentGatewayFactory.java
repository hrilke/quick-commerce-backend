package com.quickcommerse.payment.gateway;

import com.quickcommerse.payment.gateway.exception.GatewayException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class PaymentGatewayFactory {
    private final Map<String, PaymentGateway> gateways;

    public PaymentGatewayFactory(Map<String, PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway get(String name) {
        if (name == null) throw new GatewayException("Gateway name is required");
        PaymentGateway gw = gateways.get(name.toUpperCase(Locale.ROOT));
        if (gw == null) throw new GatewayException("Gateway not found: " + name);
        return gw;
    }
}
