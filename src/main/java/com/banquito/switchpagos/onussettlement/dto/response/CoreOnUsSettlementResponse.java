package com.banquito.switchpagos.onussettlement.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public class CoreOnUsSettlementResponse {

    private UUID lineId;
    private String status;
    private String coreTransactionId;
    private LocalDate accountingDate;
    private String message;

    public UUID getLineId() {
        return lineId;
    }

    public void setLineId(UUID lineId) {
        this.lineId = lineId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoreTransactionId() {
        return coreTransactionId;
    }

    public void setCoreTransactionId(String coreTransactionId) {
        this.coreTransactionId = coreTransactionId;
    }

    public LocalDate getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate(LocalDate accountingDate) {
        this.accountingDate = accountingDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
