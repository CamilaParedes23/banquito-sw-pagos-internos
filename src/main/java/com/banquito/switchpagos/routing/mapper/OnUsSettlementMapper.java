package com.banquito.switchpagos.onussettlement.mapper;

import com.banquito.switchpagos.onussettlement.dto.event.OnUsSettlementCompletedEvent;
import com.banquito.switchpagos.onussettlement.dto.event.PaymentLineRoutedOnUsEvent;
import com.banquito.switchpagos.onussettlement.dto.request.CoreOnUsSettlementRequest;
import com.banquito.switchpagos.onussettlement.dto.response.CoreOnUsSettlementResponse;
import com.banquito.switchpagos.onussettlement.enums.OnUsSettlementStatus;
import com.banquito.switchpagos.onussettlement.enums.SettlementAttemptStatus;
import com.banquito.switchpagos.onussettlement.model.OnUsSettlement;
import com.banquito.switchpagos.onussettlement.model.OnUsSettlementAttempt;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class OnUsSettlementMapper {

    private static final String SOURCE_SERVICE = "banquito-switch-on-us-settlement-service";

    public OnUsSettlement toSettlement(PaymentLineRoutedOnUsEvent event, OffsetDateTime now) {
        OnUsSettlement settlement = new OnUsSettlement(UUID.randomUUID());
        settlement.setSourceEventId(event.getEventId());
        settlement.setBatchId(event.getBatchId());
        settlement.setLineId(event.getLineId());
        settlement.setCorrelationId(event.getCorrelationId());
        settlement.setSequenceNumber(event.getSequenceNumber());
        settlement.setCompanyRuc(event.getCompanyRuc());
        settlement.setSourceAccountNumber(event.getSourceAccountNumber());
        settlement.setCoreFundingId(event.getCoreFundingId());
        settlement.setBeneficiaryIdentification(event.getBeneficiaryIdentification());
        settlement.setBeneficiaryName(event.getBeneficiaryName());
        settlement.setDestinationAccountNumber(event.getDestinationAccountNumber());
        settlement.setRoutingCode(event.getRoutingCode());
        settlement.setDestinationInstitutionName(event.getDestinationInstitutionName());
        settlement.setAmount(event.getAmount());
        settlement.setCurrency(event.getCurrency());
        settlement.setReference(event.getReference());
        settlement.setNotificationEmail(event.getNotificationEmail());
        settlement.setStatus(OnUsSettlementStatus.RECIBIDA.name());
        settlement.setBillable(Boolean.FALSE);
        settlement.setIdempotencyKey("ONUS-" + event.getLineId());
        settlement.setCreatedAt(now);
        settlement.setUpdatedAt(now);
        return settlement;
    }

    public CoreOnUsSettlementRequest toCoreRequest(OnUsSettlement settlement) {
        CoreOnUsSettlementRequest request = new CoreOnUsSettlementRequest();
        request.setBatchId(settlement.getBatchId());
        request.setLineId(settlement.getLineId());
        request.setCoreFundingId(settlement.getCoreFundingId());
        request.setDestinationAccountNumber(settlement.getDestinationAccountNumber());
        request.setBeneficiaryIdentification(settlement.getBeneficiaryIdentification());
        request.setAmount(settlement.getAmount());
        request.setCurrency(settlement.getCurrency());
        request.setReference(settlement.getReference());
        request.setIdempotencyKey(settlement.getIdempotencyKey());
        return request;
    }

    public OnUsSettlementAttempt toStartedAttempt(OnUsSettlement settlement, Integer attemptNumber, OffsetDateTime now) {
        OnUsSettlementAttempt attempt = new OnUsSettlementAttempt(UUID.randomUUID());
        attempt.setSettlementId(settlement.getSettlementId());
        attempt.setBatchId(settlement.getBatchId());
        attempt.setLineId(settlement.getLineId());
        attempt.setAttemptNumber(attemptNumber);
        attempt.setRequestedAt(now);
        attempt.setAttemptStatus(SettlementAttemptStatus.SOLICITADO.name());
        return attempt;
    }

    public void applyCoreApproved(
            OnUsSettlement settlement,
            OnUsSettlementAttempt attempt,
            CoreOnUsSettlementResponse response,
            OffsetDateTime now) {
        settlement.setStatus(OnUsSettlementStatus.ACREDITADA_ON_US.name());
        settlement.setBillable(Boolean.TRUE);
        settlement.setCoreTransactionId(response.getCoreTransactionId());
        settlement.setAccountingDate(response.getAccountingDate());
        settlement.setRejectionCode(null);
        settlement.setRejectionReason(null);
        settlement.setUpdatedAt(now);
        settlement.setCompletedAt(now);

        attempt.setAttemptStatus(SettlementAttemptStatus.APROBADO.name());
        attempt.setCoreStatus(response.getStatus());
        attempt.setCoreTransactionId(response.getCoreTransactionId());
        attempt.setAccountingDate(response.getAccountingDate());
        attempt.setRespondedAt(now);
    }

    public void applyCoreRejected(
            OnUsSettlement settlement,
            OnUsSettlementAttempt attempt,
            CoreOnUsSettlementResponse response,
            OffsetDateTime now) {
        String rejectionCode = normalizeRejectionCode(response.getStatus());
        String rejectionReason = response.getMessage();
        settlement.setStatus(OnUsSettlementStatus.RECHAZADA.name());
        settlement.setBillable(Boolean.FALSE);
        settlement.setCoreTransactionId(response.getCoreTransactionId());
        settlement.setAccountingDate(response.getAccountingDate());
        settlement.setRejectionCode(rejectionCode);
        settlement.setRejectionReason(rejectionReason);
        settlement.setUpdatedAt(now);
        settlement.setCompletedAt(now);

        attempt.setAttemptStatus(SettlementAttemptStatus.RECHAZADO.name());
        attempt.setCoreStatus(response.getStatus());
        attempt.setCoreTransactionId(response.getCoreTransactionId());
        attempt.setAccountingDate(response.getAccountingDate());
        attempt.setErrorCode(rejectionCode);
        attempt.setErrorMessage(rejectionReason);
        attempt.setRespondedAt(now);
    }

    public void applyTechnicalFailure(
            OnUsSettlement settlement,
            OnUsSettlementAttempt attempt,
            String errorCode,
            String errorMessage,
            OffsetDateTime now) {
        settlement.setStatus(OnUsSettlementStatus.FALLIDA.name());
        settlement.setBillable(Boolean.FALSE);
        settlement.setRejectionCode(errorCode);
        settlement.setRejectionReason(errorMessage);
        settlement.setUpdatedAt(now);
        settlement.setCompletedAt(now);

        attempt.setAttemptStatus(SettlementAttemptStatus.FALLIDO.name());
        attempt.setErrorCode(errorCode);
        attempt.setErrorMessage(errorMessage);
        attempt.setRespondedAt(now);
    }

    public OnUsSettlementCompletedEvent toCompletedEvent(OnUsSettlement settlement, UUID eventId, OffsetDateTime now) {
        OnUsSettlementCompletedEvent event = new OnUsSettlementCompletedEvent();
        event.setEventId(eventId);
        event.setEventType("ON_US_SETTLEMENT_COMPLETED");
        event.setOccurredAt(now);
        event.setBatchId(settlement.getBatchId());
        event.setLineId(settlement.getLineId());
        event.setCorrelationId(settlement.getCorrelationId());
        event.setSourceService(SOURCE_SERVICE);
        event.setFinalStatus(settlement.getStatus());
        event.setBillable(settlement.getBillable());
        event.setAmount(settlement.getAmount());
        event.setCurrency(settlement.getCurrency());
        event.setCoreTransactionId(settlement.getCoreTransactionId());
        event.setRejectionCode(settlement.getRejectionCode());
        event.setRejectionReason(settlement.getRejectionReason());
        event.setNotificationEmail(settlement.getNotificationEmail());
        return event;
    }

    private String normalizeRejectionCode(String coreStatus) {
        if (coreStatus == null || coreStatus.isBlank()) {
            return "CORE_REJECTED";
        }
        return "CORE_" + coreStatus.trim().toUpperCase();
    }
}
