package com.banquito.switchpagos.onussettlement.service;

import com.banquito.switchpagos.onussettlement.dto.event.PaymentLineRoutedOnUsEvent;

public interface OnUsSettlementService {

    void processOnUsLine(PaymentLineRoutedOnUsEvent event);
}
