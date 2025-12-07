package org.cms.carrental.exception;

public class VehicleApiException extends RuntimeException {
    public VehicleApiException(String message) {
        super(message);
    }

    public VehicleApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

