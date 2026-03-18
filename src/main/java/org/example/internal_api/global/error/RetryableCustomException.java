package org.example.internal_api.global.error;

public class RetryableCustomException extends CustomException{
    public RetryableCustomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
