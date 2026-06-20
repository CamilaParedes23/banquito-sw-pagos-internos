package com.banquito.switchpagos.onussettlement.client;

import com.banquito.switchpagos.onussettlement.dto.request.CoreConsumeReservationRequest;
import com.banquito.switchpagos.onussettlement.dto.request.CoreOnUsSettlementRequest;
import com.banquito.switchpagos.onussettlement.dto.response.CoreReservationResponse;
import com.banquito.switchpagos.onussettlement.dto.response.CoreOnUsSettlementResponse;
import com.banquito.switchpagos.onussettlement.exception.CoreBankingClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;

@Component
public class CoreBankingClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreBankingClient.class);
    private static final String DESTINATION_TYPE_ON_US = "ON_US";
    private static final String ROUTING_CODE_BANQUITO = "BANQUITO";

    private final RestClient coreKongRestClient;
    private final String switchCorePath;
    private final String paymentReservationsPath;
    private final CoreKongTokenProvider tokenProvider;
    private final String defaultAccountingDate;

    public CoreBankingClient(
            @Qualifier("coreKongRestClient") RestClient coreKongRestClient,
            @Value("${core.kong.switch-core-path}") String switchCorePath,
            @Value("${core.kong.payment-reservations-path}") String paymentReservationsPath,
            CoreKongTokenProvider tokenProvider,
            @Value("${core.switch.default-accounting-date}") String defaultAccountingDate) {
        this.coreKongRestClient = coreKongRestClient;
        this.switchCorePath = switchCorePath;
        this.paymentReservationsPath = paymentReservationsPath;
        this.tokenProvider = tokenProvider;
        this.defaultAccountingDate = defaultAccountingDate;
    }

    public CoreOnUsSettlementResponse requestOnUsSettlement(CoreOnUsSettlementRequest request) {
        validateRequest(request);
        CoreConsumeReservationRequest coreRequest = toCoreConsumeRequest(request);
        String uri = buildConsumeUri(request.getCoreFundingId());
        try {
            CoreReservationResponse coreResponse = coreKongRestClient.post()
                    .uri(uri)
                    .headers(headers -> applyAuthorization(headers))
                    .body(coreRequest)
                    .retrieve()
                    .body(CoreReservationResponse.class);
            return toSettlementResponse(request, coreResponse);
        } catch (RestClientResponseException exception) {
            Integer statusCode = exception.getStatusCode().value();
            Boolean functionalRejection = isFunctionalRejection(statusCode);
            String message = "Core REST/Kong rechazo consumo On-Us. httpStatus=" + statusCode
                    + ", body=" + sanitizeBody(exception.getResponseBodyAsString());
            throw new CoreBankingClientException(message, statusCode, functionalRejection, exception);
        } catch (ResourceAccessException exception) {
            throw new CoreBankingClientException("Error tecnico de conectividad contra Core REST/Kong", null, Boolean.FALSE, exception);
        } catch (RuntimeException exception) {
            throw new CoreBankingClientException("Error tecnico al invocar Core REST/Kong", null, Boolean.FALSE, exception);
        }
    }

    private CoreConsumeReservationRequest toCoreConsumeRequest(CoreOnUsSettlementRequest request) {
        CoreConsumeReservationRequest coreRequest = new CoreConsumeReservationRequest();
        coreRequest.setPaymentLineUuid(request.getLineId());
        coreRequest.setDestinationType(DESTINATION_TYPE_ON_US);
        coreRequest.setRoutingCode(ROUTING_CODE_BANQUITO);
        coreRequest.setDestinationAccountNumber(request.getDestinationAccountNumber());
        coreRequest.setExternalDestinationAccount(null);
        coreRequest.setBeneficiaryIdentification(request.getBeneficiaryIdentification());
        coreRequest.setBeneficiaryName(request.getBeneficiaryName());
        coreRequest.setBeneficiaryEmail(request.getBeneficiaryEmail());
        coreRequest.setConcept(request.getReference());
        coreRequest.setAmount(request.getAmount());
        coreRequest.setAccountingDate(resolveAccountingDate());
        coreRequest.setCorrelationId(request.getCorrelationId());
        return coreRequest;
    }

    private CoreOnUsSettlementResponse toSettlementResponse(
            CoreOnUsSettlementRequest request,
            CoreReservationResponse coreResponse) {
        CoreOnUsSettlementResponse response = new CoreOnUsSettlementResponse();
        response.setLineId(request.getLineId());
        response.setStatus(coreResponse == null ? null : coreResponse.getStatus());
        response.setCoreTransactionId(null);
        response.setAccountingDate(resolveAccountingDate());
        response.setMessage("Core ReservationResponse no incluye transactionUuid de acreditacion");
        return response;
    }

    private void validateRequest(CoreOnUsSettlementRequest request) {
        if (request == null) {
            throw new CoreBankingClientException("CoreOnUsSettlementRequest no puede ser null");
        }
        if (request.getCoreFundingId() == null || request.getCoreFundingId().isBlank()) {
            throw new CoreBankingClientException("coreFundingId legacy es requerido y debe contener reservationUuid");
        }
        if (request.getLineId() == null || request.getCorrelationId() == null) {
            throw new CoreBankingClientException("lineId y correlationId son requeridos para consumir reserva Core");
        }
        if (request.getDestinationAccountNumber() == null || request.getDestinationAccountNumber().isBlank()) {
            throw new CoreBankingClientException("destinationAccountNumber es requerido para consumo On-Us");
        }
        if (request.getBeneficiaryIdentification() == null || request.getBeneficiaryIdentification().isBlank()) {
            throw new CoreBankingClientException("beneficiaryIdentification es requerido para consumo On-Us");
        }
        if (request.getAmount() == null) {
            throw new CoreBankingClientException("amount es requerido para consumo On-Us");
        }
    }

    private String buildConsumeUri(String reservationUuid) {
        return normalizePath(switchCorePath)
                + normalizePath(paymentReservationsPath)
                + "/"
                + reservationUuid
                + "/consume";
    }

    private String normalizePath(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private void applyAuthorization(HttpHeaders headers) {
        headers.setBearerAuth(tokenProvider.getBearerToken());
    }

    private LocalDate resolveAccountingDate() {
        if (defaultAccountingDate != null && !defaultAccountingDate.isBlank()) {
            try {
                return LocalDate.parse(defaultAccountingDate);
            } catch (RuntimeException exception) {
                LOGGER.warn("CORE_SWITCH_DEFAULT_ACCOUNTING_DATE invalida. Se usa fecha local actual.");
            }
        }
        return LocalDate.now();
    }

    private Boolean isFunctionalRejection(Integer httpStatus) {
        return httpStatus != null && (httpStatus == 400 || httpStatus == 404 || httpStatus == 409 || httpStatus == 422);
    }

    private String sanitizeBody(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }
        String normalized = responseBody.replace('\n', ' ').replace('\r', ' ').trim();
        if (normalized.length() <= 300) {
            return normalized;
        }
        return normalized.substring(0, 300);
    }
}
