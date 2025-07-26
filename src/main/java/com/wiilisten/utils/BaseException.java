package com.wiilisten.utils;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    /**
     *
     */
    public BaseException() {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * @param status
     * @param message
     */
    public BaseException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * @param status
     * @param message
     * @param cause
     */
    public BaseException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * @param status
     * @param cause
     */
    public BaseException(final HttpStatus status, final Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * @return
     */
    public HttpStatus getStatus() {
        return status;
    }
}
