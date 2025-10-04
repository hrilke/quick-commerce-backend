package com.quickcommerce.orders.service.impl;

import com.quickcommerce.orders.dto.CartCheckoutRequest;
import com.quickcommerce.orders.dto.CheckoutResponse;
import com.quickcommerce.orders.dto.SingleCheckoutRequest;
import com.quickcommerce.orders.integration.catalogue.dto.CatalogueClient;
import com.quickcommerce.orders.integration.payment.PaymentClient;
import com.quickcommerce.orders.integration.payment.dto.PaymentProcessResponse;
import com.quickcommerce.orders.integration.payment.dto.PaymentRequest;
import com.quickcommerce.orders.model.Order;
import com.quickcommerce.orders.model.OrderItem;
import com.quickcommerce.orders.model.OrderStatus;
import com.quickcommerce.orders.repository.OrderRepository;
import com.quickcommerce.orders.service.OrderService;
import com.quickcommerce.shared.dto.CartItemCheckoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final CatalogueClient catalogueClient;

    @Override
    @Transactional
    public CheckoutResponse checkoutSingle(SingleCheckoutRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.ORDER_PLACED);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        order.getItems().add(item);

    BigDecimal unitPrice = request.getDiscountPrice() != null ? request.getDiscountPrice() : request.getMrp();
    if (unitPrice == null) unitPrice = BigDecimal.ZERO; // defensive fallback
    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
    order.setTotalAmount(total);
    order = orderRepository.save(order);

    long amountCents = total.multiply(BigDecimal.valueOf(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact();

    PaymentRequest pr = new PaymentRequest();
    pr.setOrderId(order.getId());
    pr.setUserId(request.getUserId());
    pr.setAmountCents(amountCents);
    pr.setCurrency(DEFAULT_CURRENCY);
    pr.setDescription("Order " + order.getId());

        PaymentProcessResponse payment = paymentClient.createPayment("RAZORPAY", pr);

        order.setPaymentId(payment.getPaymentId());
        orderRepository.save(order);

        return new CheckoutResponse(order.getId(), payment.getPaymentId(), payment.getGateway(), payment.getClientPayload());
    }

    @Override
    @Transactional
    public CheckoutResponse checkoutCart(CartCheckoutRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.ORDER_PLACED);
        List<OrderItem> orderItemList = resolveOrderItemsFromCart(request.getUserId());
        
        for (OrderItem oi : orderItemList) {
            oi.setOrder(order);
            order.getItems().add(oi);
        }
    // compute cart total (discountPrice fallback to mrp)
    BigDecimal cartTotal = order.getItems().stream()
        .map(oi -> {
            BigDecimal price = oi.getDiscountPrice() != null ? oi.getDiscountPrice() : oi.getMrp();
            if (price == null) price = BigDecimal.ZERO;
            return price.multiply(BigDecimal.valueOf(oi.getQuantity()));
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotalAmount(cartTotal);
    order = orderRepository.save(order);

    long amountCents = cartTotal.multiply(BigDecimal.valueOf(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact();

    PaymentRequest pr = new PaymentRequest();
    pr.setOrderId(order.getId());
    pr.setUserId(request.getUserId());
    pr.setAmountCents(amountCents);
    pr.setCurrency(DEFAULT_CURRENCY);
    pr.setDescription("Cart Order " + order.getId());

        PaymentProcessResponse payment = paymentClient.createPayment("RAZORPAY", pr);
        order.setPaymentId(payment.getPaymentId());
        orderRepository.save(order);
        catalogueClient.emptyCart(request.getUserId());
        return new CheckoutResponse(order.getId(), payment.getPaymentId(), payment.getGateway(), payment.getClientPayload());
    }

    private List<OrderItem> resolveOrderItemsFromCart(UUID userId) {
        List<CartItemCheckoutDto> cartItemCheckoutDtoList = catalogueClient.getCartItemListByUserId(userId);
        return cartItemCheckoutDtoList.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setMrp(ci.getMrp());
            oi.setDiscountPrice(ci.getDiscountPrice());
            oi.setSku(ci.getSku());
            return oi;
        }).toList();
    }

}
