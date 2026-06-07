package com.banquito.switchpagos.onussettlement.service.impl;

import com.banquito.switchpagos.onussettlement.client.CoreBankingClient;
import com.banquito.switchpagos.onussettlement.dto.event.OnUsSettlementCompletedEvent;
import com.banquito.switchpagos.onussettlement.dto.event.PaymentLineRoutedOnUsEvent;
import com.banquito.switchpagos.onussettlement.dto.response.CoreOnUsSettlementResponse;
import com.banquito.switchpagos.onussettlement.enums.OnUsSettlementStatus;
import com.banquito.switchpagos.onussettlement.exception.CoreBankingClientException;
import com.banquito.switchpagos.onussettlement.mapper.OnUsSettlementMapper;
import com.banquito.switchpagos.onussettlement.model.OnUsSettlement;
import com.banquito.switchpagos.onussettlement.model.OnUsSettlementAttempt;
import com.banquito.switchpagos.onussettlement.repository.OnUsSettlementAttemptRepository;
import com.banquito.switchpagos.onussettlement.repository.OnUsSettlementRepository;
import com.banquito.switchpagos.onussettlement.service.OnUsSettlementEventPublisher;
import com.banquito.switchpagos.onussettlement.service.OnUsSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class OnUsSettlementServiceImpl implements OnUsSettlementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnUsSettlementServiceImpl.class);
    private static final Set<String> FINAL_STATUSES = Set.of(
            OnUsSettlementStatus.ACREDITADA_ON_US.name(),
            OnUsSettlementStatus.RECHAZADA.name(),
            OnUsSettlementStatus.FALLIDA.name());

    private final OnUsSettlementRepository settlementRepository;
    private final OnUsSettlementAttemptRepository attemptRepository;
    private final OnUsSettlementMapper settlementMapper;
    private final CoreBankingClient coreBankingClient;
    private final OnUsSettlementEventPublisher eventPublisher;

    public OnUsSettlementServiceImpl(
            OnUsSettlementRepository settlementRepository,
            OnUsSettlementAttemptRepository attemptRepository,
            OnUsSettlementMapper settlementMapper,
            CoreBankingClient coreBankingClient,
            OnUsSettlementEventPublisher eventPublisher) {
        this.settlementRepository = settlementRepository;
        this.attemptRepository = attemptRepository;
        this.settlementMapper = settlementMapper;
        this.coreBankingClient = coreBankingClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void processOnUsLine(PaymentLineRoutedOnUsEvent event) {
        validateEvent(event);
        OnUsSettlement existingSettlement = settlementRepository.findByLineId(event.getLineId()).orElse(null);
        if (existingSettlement != null && FINAL_STATUSES.contains(existingSettlement.getStatus())) {
            LOGGER.info("Linea On-Us ya procesada. batchId={}, lineId={}, status={}",
                    existingSettlement.getBatchId(), existingSettlement.getLineId(), existingSettlement.getStatus());
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        OnUsSettlement settlement = existingSettlement;
        if (settlement == null) {
            settlement = settlementMapper.toSettlement(event, now);
            settlementRepository.save(settlement);
        }

        Long existingAttempts = attemptRepository.countByLineId(settlement.getLineId());
        Integer attemptNumber = existingAttempts.intValue() + 1;
        OnUsSettlementAttempt attempt = settlementMapper.toStartedAttempt(settlement, attemptNumber, now);
        attemptRepository.save(attempt);

        settlement.setStatus(OnUsSettlementStatus.ENVIADA_CORE.name());
        settlement.setUpdatedAt(now);
        settlementRepository.save(settlement);

        try {
            CoreOnUsSettlementResponse response = coreBankingClient.requestOnUsSettlement(settlementMapper.toCoreRequest(settlement));
            OffsetDateTime responseTime = OffsetDateTime.now();
            if (isApproved(response.getStatus())) {
                settlementMapper.applyCoreApproved(settlement, attempt, response, responseTime);
            } else {
                settlementMapper.applyCoreRejected(settlement, attempt, response, responseTime);
            }
        } catch (CoreBankingClientException exception) {
            OffsetDateTime failureTime = OffsetDateTime.now();
            settlementMapper.applyTechnicalFailure(
                    settlement,
                    attempt,
                    "CORE_GRPC_ERROR",
                    limitMessage(exception.getMessage()),
                    failureTime);
        }

        settlementRepository.save(settlement);
        attemptRepository.save(attempt);
        OnUsSettlementCompletedEvent completedEvent = settlementMapper.toCompletedEvent(settlement, UUID.randomUUID(), OffsetDateTime.now());
        eventPublisher.publishCompleted(completedEvent);
        LOGGER.info("Resultado On-Us publicado. batchId={}, lineId={}, status={}, billable={}",
                settlement.getBatchId(), settlement.getLineId(), settlement.getStatus(), settlement.getBillable());
    }

    private boolean isApproved(String coreStatus) {
        return "APPROVED".equalsIgnoreCase(coreStatus);
    }

    private void validateEvent(PaymentLineRoutedOnUsEvent event) {
        if (event == null || event.getBatchId() == null || event.getLineId() == null || event.getCorrelationId() == null) {
            throw new IllegalArgumentException("PaymentLineRoutedOnUsEvent debe incluir batchId, lineId y correlationId");
        }
        if (event.getCoreFundingId() == null || event.getCoreFundingId().isBlank()) {
            throw new IllegalArgumentException("PaymentLineRoutedOnUsEvent debe incluir coreFundingId");
        }
        if (event.getAmount() == null || event.getCurrency() == null || event.getCurrency().isBlank()) {
            throw new IllegalArgumentException("PaymentLineRoutedOnUsEvent debe incluir amount y currency");
        }
    }

    private String limitMessage(String message) {
        if (message == null) {
            return "Error tecnico al invocar Core Bancario";
        }
        if (message.length() <= 500) {
            return message;
        }
        return message.substring(0, 500);
    }
}
