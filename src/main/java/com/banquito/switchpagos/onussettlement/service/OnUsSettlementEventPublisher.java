package com.banquito.switchpagos.onussettlement.service;

import com.banquito.switchpagos.onussettlement.dto.event.OnUsSettlementCompletedEvent;

public interface OnUsSettlementEventPublisher {

    void publishCompleted(OnUsSettlementCompletedEvent event);
}
