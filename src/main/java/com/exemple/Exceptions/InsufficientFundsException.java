package com.exemple.Exceptions;

/**
 * Exception pour les fonds insuffisants
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
