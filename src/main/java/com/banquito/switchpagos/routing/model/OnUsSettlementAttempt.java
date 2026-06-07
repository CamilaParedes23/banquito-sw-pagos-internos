package com.banquito.switchpagos.onussettlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "on_us_settlement_attempt")
public class OnUsSettlementAttempt {

    @Id
    @Column(name = "attempt_id")
    private UUID attemptId;

    @Column(name = "settlement_id", nullable = false)
    private UUID settlementId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(name = "line_id", nullable = false)
    private UUID lineId;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @Column(name = "attempt_status", nullable = false, length = 40)
    private String attemptStatus;

    @Column(name = "core_status", length = 40)
    private String coreStatus;

    @Column(name = "core_transaction_id", length = 80)
    private String coreTransactionId;

    @Column(name = "accounting_date")
    private LocalDate accountingDate;

    @Column(name = "error_code", length = 80)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    public OnUsSettlementAttempt() {
    }

    public OnUsSettlementAttempt(UUID attemptId) {
        this.attemptId = attemptId;
    }

    public UUID getAttemptId() { return attemptId; }
    public void setAttemptId(UUID attemptId) { this.attemptId = attemptId; }
    public UUID getSettlementId() { return settlementId; }
    public void setSettlementId(UUID settlementId) { this.settlementId = settlementId; }
    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public UUID getLineId() { return lineId; }
    public void setLineId(UUID lineId) { this.lineId = lineId; }
    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }
    public OffsetDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(OffsetDateTime requestedAt) { this.requestedAt = requestedAt; }
    public OffsetDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(OffsetDateTime respondedAt) { this.respondedAt = respondedAt; }
    public String getAttemptStatus() { return attemptStatus; }
    public void setAttemptStatus(String attemptStatus) { this.attemptStatus = attemptStatus; }
    public String getCoreStatus() { return coreStatus; }
    public void setCoreStatus(String coreStatus) { this.coreStatus = coreStatus; }
    public String getCoreTransactionId() { return coreTransactionId; }
    public void setCoreTransactionId(String coreTransactionId) { this.coreTransactionId = coreTransactionId; }
    public LocalDate getAccountingDate() { return accountingDate; }
    public void setAccountingDate(LocalDate accountingDate) { this.accountingDate = accountingDate; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof OnUsSettlementAttempt that)) {
            return false;
        }
        return Objects.equals(attemptId, that.attemptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptId);
    }

    @Override
    public String toString() {
        return "OnUsSettlementAttempt{" +
                "attemptId=" + attemptId +
                ", batchId=" + batchId +
                ", lineId=" + lineId +
                ", attemptStatus='" + attemptStatus + '\'' +
                '}';
    }
}
