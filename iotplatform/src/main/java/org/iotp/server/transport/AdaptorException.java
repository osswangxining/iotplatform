package org.iotp.server.transport;

public class AdaptorException extends Exception {

  private static final long serialVersionUID = 1L;

  public AdaptorException() {
    super();
  }

  public AdaptorException(String cause) {
    super(cause);
  }

  public AdaptorException(Exception cause) {
    super(cause);
  }

}
