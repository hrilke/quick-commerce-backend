package com.quickcommerce.orders.service.impl;

import com.quickcommerce.orders.dto.CartCheckoutRequest;
import com.quickcommerce.orders.dto.CheckoutResponse;
import com.quickcommerce.orders.dto.SingleCheckoutRequest;
import com.quickcommerce.orders.integration.payment.PaymentClient;
import com.quickcommerce.orders.integration.payment.dto.PaymentProcessResponse;
import com.quickcommerce.orders.integration.payment.dto.PaymentRequest;
import com.quickcommerce.orders.model.Order;
import com.quickcommerce.orders.model.OrderItem;
import com.quickcommerce.orders.model.OrderStatus;
import com.quickcommerce.orders.repository.OrderRepository;
import com.quickcommerce.orders.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderServiceImpl(OrderRepository orderRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

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

        order = orderRepository.save(order);

        PaymentRequest pr = new PaymentRequest();
        pr.setOrderId(order.getId());
        pr.setUserId(request.getUserId());
        pr.setAmountCents(request.getDiscountPrice().longValue());
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
        order = orderRepository.save(order);

        PaymentRequest pr = new PaymentRequest();
        pr.setOrderId(order.getId());
        pr.setUserId(request.getUserId());
        pr.setAmountCents(0L);
        pr.setCurrency(DEFAULT_CURRENCY);
        pr.setDescription("Cart Order " + order.getId());

        PaymentProcessResponse payment = paymentClient.createPayment("RAZORPAY", pr);
        order.setPaymentId(payment.getPaymentId());
        orderRepository.save(order);

        return new CheckoutResponse(order.getId(), payment.getPaymentId(), payment.getGateway(), payment.getClientPayload());
    }
}
