package com.banquito.switchpagos.onussettlement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "on_us_settlement")
public class OnUsSettlement {

    @Id
    @Column(name = "settlement_id")
    private UUID settlementId;

    @Column(name = "source_event_id", nullable = false)
    private UUID sourceEventId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(name = "line_id", nullable = false, unique = true)
    private UUID lineId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "company_ruc", length = 20)
    private String companyRuc;

    @Column(name = "source_account_number", length = 40)
    private String sourceAccountNumber;

    @Column(name = "core_funding_id", nullable = false, length = 80)
    private String coreFundingId;

    @Column(name = "beneficiary_identification", nullable = false, length = 30)
    private String beneficiaryIdentification;

    @Column(name = "beneficiary_name", nullable = false, length = 120)
    private String beneficiaryName;

    @Column(name = "destination_account_number", nullable = false, length = 40)
    private String destinationAccountNumber;

    @Column(name = "routing_code", nullable = false, length = 10)
    private String routingCode;

    @Column(name = "destination_institution_name", length = 120)
    private String destinationInstitutionName;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "reference", length = 140)
    private String reference;

    @Column(name = "notification_email", length = 120)
    private String notificationEmail;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "billable", nullable = false)
    private Boolean billable;

    @Column(name = "idempotency_key", nullable = false, length = 80)
    private String idempotencyKey;

    @Column(name = "core_transaction_id", length = 80)
    private String coreTransactionId;

    @Column(name = "accounting_date")
    private LocalDate accountingDate;

    @Column(name = "rejection_code", length = 80)
    private String rejectionCode;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public OnUsSettlement() {
    }

    public OnUsSettlement(UUID settlementId) {
        this.settlementId = settlementId;
    }

    public UUID getSettlementId() { return settlementId; }
    public void setSettlementId(UUID settlementId) { this.settlementId = settlementId; }
    public UUID getSourceEventId() { return sourceEventId; }
    public void setSourceEventId(UUID sourceEventId) { this.sourceEventId = sourceEventId; }
    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public UUID getLineId() { return lineId; }
    public void setLineId(UUID lineId) { this.lineId = lineId; }
    public UUID getCorrelationId() { return correlationId; }
    public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public String getCompanyRuc() { return companyRuc; }
    public void setCompanyRuc(String companyRuc) { this.companyRuc = companyRuc; }
    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public void setSourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; }
    public String getCoreFundingId() { return coreFundingId; }
    public void setCoreFundingId(String coreFundingId) { this.coreFundingId = coreFundingId; }
    public String getBeneficiaryIdentification() { return beneficiaryIdentification; }
    public void setBeneficiaryIdentification(String beneficiaryIdentification) { this.beneficiaryIdentification = beneficiaryIdentification; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }
    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }
    public String getRoutingCode() { return routingCode; }
    public void setRoutingCode(String routingCode) { this.routingCode = routingCode; }
    public String getDestinationInstitutionName() { return destinationInstitutionName; }
    public void setDestinationInstitutionName(String destinationInstitutionName) { this.destinationInstitutionName = destinationInstitutionName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNotificationEmail() { return notificationEmail; }
    public void setNotificationEmail(String notificationEmail) { this.notificationEmail = notificationEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getBillable() { return billable; }
    public void setBillable(Boolean billable) { this.billable = billable; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getCoreTransactionId() { return coreTransactionId; }
    public void setCoreTransactionId(String coreTransactionId) { this.coreTransactionId = coreTransactionId; }
    public LocalDate getAccountingDate() { return accountingDate; }
    public void setAccountingDate(LocalDate accountingDate) { this.accountingDate = accountingDate; }
    public String getRejectionCode() { return rejectionCode; }
    public void setRejectionCode(String rejectionCode) { this.rejectionCode = rejectionCode; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof OnUsSettlement that)) {
            return false;
        }
        return Objects.equals(settlementId, that.settlementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settlementId);
    }

    @Override
    public String toString() {
        return "OnUsSettlement{" +
                "settlementId=" + settlementId +
                ", batchId=" + batchId +
                ", lineId=" + lineId +
                ", status='" + status + '\'' +
                '}';
    }
}
