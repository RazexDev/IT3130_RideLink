package com.ridelink.fare.dto;

import com.ridelink.fare.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class ProcessPaymentRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public ProcessPaymentRequest() {
    }

    public ProcessPaymentRequest(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
