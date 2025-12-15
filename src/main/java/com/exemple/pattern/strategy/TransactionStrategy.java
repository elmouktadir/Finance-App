package com.exemple.pattern.strategy;


import com.exemple.Exceptions.InsufficientFundsException;
import com.exemple.model.Account;
import com.exemple.model.Transaction;

/**
 * Interface Strategy pour les différents types de transactions bancaires
 */
public interface TransactionStrategy {

    /**
     * Exécute une transaction selon la stratégie implémentée
     */
    Transaction execute(Account source, Account destination, double amount)
            throws IllegalArgumentException, InsufficientFundsException;

    /**
     * Valide les paramètres de la transaction avant exécution
     */
    boolean validate(Account source, Account destination, double amount);

    /**
     * Retourne le type de transaction
     */
    String getTransactionType();
}

