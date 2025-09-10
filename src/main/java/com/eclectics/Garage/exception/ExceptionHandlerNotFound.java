package com.eclectics.Garage.exception;

public class ExceptionHandlerNotFound extends RuntimeException {
  public ExceptionHandlerNotFound(String message, Throwable cause){
    super(message, cause);
  }
    public ExceptionHandlerNotFound(String message) {
        super(message);
    }
}
