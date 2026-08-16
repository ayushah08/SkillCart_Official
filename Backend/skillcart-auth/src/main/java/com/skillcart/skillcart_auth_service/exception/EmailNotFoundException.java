package com.skillcart.skillcart_auth_service.exception;

public class EmailNotFoundException extends RuntimeException{

    public  EmailNotFoundException(String message){
         super(message);
    }
}
