package com.banquito.switchpagos.onussettlement.listener;

import com.banquito.switchpagos.onussettlement.dto.event.PaymentLineRoutedOnUsEvent;
import com.banquito.switchpagos.onussettlement.service.OnUsSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentLineRoutedOnUsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentLineRoutedOnUsListener.class);

    private final OnUsSettlementService onUsSettlementService;

    public PaymentLineRoutedOnUsListener(OnUsSettlementService onUsSettlementService) {
        this.onUsSettlementService = onUsSettlementService;
    }

    @RabbitListener(queues = "${rabbit.queue.settlement.on-us}")
    public void onPaymentLineRoutedOnUs(PaymentLineRoutedOnUsEvent event) {
        if (event == null) {
            LOGGER.warn("Evento On-Us nulo recibido");
            return;
        }
        LOGGER.info("Evento On-Us recibido. batchId={}, lineId={}", event.getBatchId(), event.getLineId());
        onUsSettlementService.processOnUsLine(event);
    }
}
