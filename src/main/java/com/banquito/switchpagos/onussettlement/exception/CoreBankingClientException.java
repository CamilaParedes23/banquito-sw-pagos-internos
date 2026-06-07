package com.banquito.switchpagos.onussettlement.exception;

public class CoreBankingClientException extends RuntimeException {

    public CoreBankingClientException(String message) {
        super(message);
    }

    public CoreBankingClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
