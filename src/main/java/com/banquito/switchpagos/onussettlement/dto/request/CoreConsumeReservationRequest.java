package com.banquito.switchpagos.onussettlement.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CoreConsumeReservationRequest {

    private UUID paymentLineUuid;
    private String destinationType;
    private String routingCode;
    private String destinationAccountNumber;
    private String externalDestinationAccount;
    private String beneficiaryIdentification;
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String concept;
    private BigDecimal amount;
    private LocalDate accountingDate;
    private UUID correlationId;

    public UUID getPaymentLineUuid() {
        return paymentLineUuid;
    }

    public void setPaymentLineUuid(UUID paymentLineUuid) {
        this.paymentLineUuid = paymentLineUuid;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public String getExternalDestinationAccount() {
        return externalDestinationAccount;
    }

    public void setExternalDestinationAccount(String externalDestinationAccount) {
        this.externalDestinationAccount = externalDestinationAccount;
    }

    public String getBeneficiaryIdentification() {
        return beneficiaryIdentification;
    }

    public void setBeneficiaryIdentification(String beneficiaryIdentification) {
        this.beneficiaryIdentification = beneficiaryIdentification;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate(LocalDate accountingDate) {
        this.accountingDate = accountingDate;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}
