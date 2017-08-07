package org.iotp.iothub.server.exception;

public class IoTHubException extends Exception {

    private static final long serialVersionUID = 1L;

    private IoTHubErrorCode errorCode;

    public IoTHubException() {
        super();
    }

    public IoTHubException(IoTHubErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public IoTHubException(String message, IoTHubErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public IoTHubException(String message, Throwable cause, IoTHubErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public IoTHubException(Throwable cause, IoTHubErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public IoTHubErrorCode getErrorCode() {
        return errorCode;
    }

}
