package com.se191116.studymanagement.exception;

public class InvalidStateTransitionException extends BusinessException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
