package de.rayzs.vit.api.request.exceptions;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(final String message) {
        super(message);
    }

}
