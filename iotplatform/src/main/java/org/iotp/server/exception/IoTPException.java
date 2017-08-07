package org.iotp.server.exception;

public class IoTPException extends Exception {

    private static final long serialVersionUID = 1L;

    private IoTPErrorCode errorCode;

    public IoTPException() {
        super();
    }

    public IoTPException(IoTPErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public IoTPException(String message, IoTPErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public IoTPException(String message, Throwable cause, IoTPErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public IoTPException(Throwable cause, IoTPErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public IoTPErrorCode getErrorCode() {
        return errorCode;
    }

}
