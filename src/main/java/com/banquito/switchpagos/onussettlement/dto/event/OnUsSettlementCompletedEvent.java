package com.banquito.switchpagos.onussettlement.dto.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class OnUsSettlementCompletedEvent {

    private UUID eventId;
    private String eventType;
    private OffsetDateTime occurredAt;
    private UUID batchId;
    private UUID lineId;
    private UUID correlationId;
    private String sourceService;
    private String finalStatus;
    private Boolean billable;
    private BigDecimal amount;
    private String currency;
    private String coreTransactionId;
    private String rejectionCode;
    private String rejectionReason;
    private String notificationEmail;

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public UUID getLineId() { return lineId; }
    public void setLineId(UUID lineId) { this.lineId = lineId; }
    public UUID getCorrelationId() { return correlationId; }
    public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
    public String getSourceService() { return sourceService; }
    public void setSourceService(String sourceService) { this.sourceService = sourceService; }
    public String getFinalStatus() { return finalStatus; }
    public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }
    public Boolean getBillable() { return billable; }
    public void setBillable(Boolean billable) { this.billable = billable; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCoreTransactionId() { return coreTransactionId; }
    public void setCoreTransactionId(String coreTransactionId) { this.coreTransactionId = coreTransactionId; }
    public String getRejectionCode() { return rejectionCode; }
    public void setRejectionCode(String rejectionCode) { this.rejectionCode = rejectionCode; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getNotificationEmail() { return notificationEmail; }
    public void setNotificationEmail(String notificationEmail) { this.notificationEmail = notificationEmail; }
}
