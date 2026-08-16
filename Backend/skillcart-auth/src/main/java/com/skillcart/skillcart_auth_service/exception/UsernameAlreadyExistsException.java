package com.skillcart.skillcart_auth_service.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message){
        super(message);
    }
}
