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
@Table(name = "\"LIQUIDACION_ON_US\"")
public class OnUsSettlement {

    @Id
    @Column(name = "\"ID_LIQUIDACION\"")
    private UUID settlementId;

    @Column(name = "\"ID_EVENTO_ORIGEN\"", nullable = false)
    private UUID sourceEventId;

    @Column(name = "\"ID_LOTE\"", nullable = false)
    private UUID batchId;

    @Column(name = "\"ID_LINEA\"", nullable = false, unique = true)
    private UUID lineId;

    @Column(name = "\"ID_CORRELACION\"", nullable = false)
    private UUID correlationId;

    @Column(name = "\"NUMERO_SECUENCIA\"")
    private Integer sequenceNumber;

    @Column(name = "\"RUC_EMPRESA\"", length = 20)
    private String companyRuc;

    @Column(name = "\"NUMERO_CUENTA_MATRIZ\"", length = 40)
    private String sourceAccountNumber;

    @Column(name = "\"ID_FONDEO_CORE\"", nullable = false, length = 80)
    private String coreFundingId;

    @Column(name = "\"IDENTIFICACION_BENEFICIARIO\"", nullable = false, length = 30)
    private String beneficiaryIdentification;

    @Column(name = "\"NOMBRE_BENEFICIARIO\"", nullable = false, length = 120)
    private String beneficiaryName;

    @Column(name = "\"NUMERO_CUENTA_DESTINO\"", nullable = false, length = 40)
    private String destinationAccountNumber;

    @Column(name = "\"CODIGO_ENRUTAMIENTO\"", nullable = false, length = 10)
    private String routingCode;

    @Column(name = "\"NOMBRE_INSTITUCION_DESTINO\"", length = 120)
    private String destinationInstitutionName;

    @Column(name = "\"MONTO\"", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "\"MONEDA\"", nullable = false, length = 3)
    private String currency;

    @Column(name = "\"REFERENCIA\"", length = 140)
    private String reference;

    @Column(name = "\"EMAIL_NOTIFICACION\"", length = 120)
    private String notificationEmail;

    @Column(name = "\"ESTADO\"", nullable = false, length = 40)
    private String status;

    @Column(name = "\"COBRABLE\"", nullable = false)
    private Boolean billable;

    @Column(name = "\"CLAVE_IDEMPOTENCIA\"", nullable = false, length = 80)
    private String idempotencyKey;

    @Column(name = "\"ID_TRANSACCION_CORE\"", length = 80)
    private String coreTransactionId;

    @Column(name = "\"FECHA_CONTABLE\"")
    private LocalDate accountingDate;

    @Column(name = "\"CODIGO_RECHAZO\"", length = 80)
    private String rejectionCode;

    @Column(name = "\"MOTIVO_RECHAZO\"", length = 500)
    private String rejectionReason;

    @Column(name = "\"FECHA_CREACION\"", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "\"FECHA_ACTUALIZACION\"", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "\"FECHA_FINALIZACION\"")
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
