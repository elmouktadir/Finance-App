package com.exemple.pattern.strategy;


import com.exemple.model.Account;
import com.exemple.model.Transaction;
import java.time.LocalDateTime;

/**
 * Stratégie pour les opérations de dépôt
 */
public class DepositStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account source, Account destination, double amount)
            throws IllegalArgumentException {

        if (!validate(source, destination, amount)) {
            throw new IllegalArgumentException("Paramètres de dépôt invalides");
        }

        destination.credit(amount);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                "DEPOSIT",
                null,
                destination.getAccountNumber(),
                amount,
                LocalDateTime.now(),
                "Dépôt effectué avec succès"
        );

        return transaction;
    }

    @Override
    public boolean validate(Account source, Account destination, double amount) {
        return destination != null && amount > 0;
    }

    @Override
    public String getTransactionType() {
        return "DEPOSIT";
    }

    private String generateTransactionId() {
        return "DEP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}