package com.eclectics.Garage.exception;

public class GarageExceptions extends RuntimeException {
  public GarageExceptions(String message, Throwable cause){
    super(message, cause);
  }

    public GarageExceptions(String message) {
        super(message);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends RuntimeException{
      public ForbiddenException(String message){
          super(message);
      }
    }

    public static class FailedToReadMultiPartFile extends RuntimeException{
        public FailedToReadMultiPartFile(String message){
            super(message);
        }
    }
}
