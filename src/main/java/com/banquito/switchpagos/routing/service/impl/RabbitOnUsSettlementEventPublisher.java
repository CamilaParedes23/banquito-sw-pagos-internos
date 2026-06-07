package com.banquito.switchpagos.onussettlement.service.impl;

import com.banquito.switchpagos.onussettlement.dto.event.OnUsSettlementCompletedEvent;
import com.banquito.switchpagos.onussettlement.service.OnUsSettlementEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitOnUsSettlementEventPublisher implements OnUsSettlementEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String settlementExchange;
    private final String onUsCompletedRoutingKey;

    public RabbitOnUsSettlementEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbit.exchange.settlement}") String settlementExchange,
            @Value("${rabbit.routing-key.on-us-completed}") String onUsCompletedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.settlementExchange = settlementExchange;
        this.onUsCompletedRoutingKey = onUsCompletedRoutingKey;
    }

    @Override
    public void publishCompleted(OnUsSettlementCompletedEvent event) {
        rabbitTemplate.convertAndSend(settlementExchange, onUsCompletedRoutingKey, event);
    }
}
