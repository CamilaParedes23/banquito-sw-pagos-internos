package com.banquito.switchpagos.onussettlement.client;

import com.banquito.switchpagos.onussettlement.dto.request.CoreOnUsSettlementRequest;
import com.banquito.switchpagos.onussettlement.dto.response.CoreOnUsSettlementResponse;
import com.banquito.switchpagos.onussettlement.exception.CoreBankingClientException;
import com.banquito.switchpagos.onussettlement.grpc.core.CoreBankingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CoreBankingClient {

    private final CoreBankingServiceGrpc.CoreBankingServiceBlockingStub coreBankingStub;
    private final Integer deadlineMs;

    public CoreBankingClient(
            ManagedChannel coreBankingManagedChannel,
            @Value("${core.grpc.deadline-ms}") Integer deadlineMs) {
        this.coreBankingStub = CoreBankingServiceGrpc.newBlockingStub(coreBankingManagedChannel);
        this.deadlineMs = deadlineMs;
    }

    public CoreOnUsSettlementResponse requestOnUsSettlement(CoreOnUsSettlementRequest request) {
        com.banquito.switchpagos.onussettlement.grpc.core.CoreOnUsSettlementRequest grpcRequest =
                com.banquito.switchpagos.onussettlement.grpc.core.CoreOnUsSettlementRequest.newBuilder()
                        .setBatchId(toString(request.getBatchId()))
                        .setLineId(toString(request.getLineId()))
                        .setCoreFundingId(toString(request.getCoreFundingId()))
                        .setDestinationAccountNumber(toString(request.getDestinationAccountNumber()))
                        .setBeneficiaryIdentification(toString(request.getBeneficiaryIdentification()))
                        .setAmount(toString(request.getAmount()))
                        .setCurrency(toString(request.getCurrency()))
                        .setReference(toString(request.getReference()))
                        .setIdempotencyKey(toString(request.getIdempotencyKey()))
                        .build();
        try {
            return toResponse(coreBankingStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
                    .requestOnUsSettlement(grpcRequest));
        } catch (StatusRuntimeException exception) {
            throw new CoreBankingClientException("Error calling Core Banking gRPC service", exception);
        }
    }

    private CoreOnUsSettlementResponse toResponse(
            com.banquito.switchpagos.onussettlement.grpc.core.CoreOnUsSettlementResponse grpcResponse) {
        CoreOnUsSettlementResponse response = new CoreOnUsSettlementResponse();
        response.setLineId(toUuid(grpcResponse.getLineId()));
        response.setStatus(grpcResponse.getStatus());
        response.setCoreTransactionId(grpcResponse.getCoreTransactionId());
        response.setAccountingDate(toLocalDate(grpcResponse.getAccountingDate()));
        response.setMessage(grpcResponse.getMessage());
        return response;
    }

    private String toString(Object value) {
        if (value instanceof BigDecimal decimalValue) {
            return decimalValue.toPlainString();
        }
        return value == null ? "" : value.toString();
    }

    private UUID toUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    private LocalDate toLocalDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
