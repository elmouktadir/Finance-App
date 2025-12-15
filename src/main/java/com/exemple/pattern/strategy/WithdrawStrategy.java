package com.exemple.pattern.strategy;


import com.exemple.Exceptions.InsufficientFundsException;
import com.exemple.model.Account;
import com.exemple.model.Transaction;
import java.time.LocalDateTime;

/**
 * Stratégie pour les opérations de retrait
 */
public class WithdrawStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account source, Account destination, double amount)
            throws IllegalArgumentException, InsufficientFundsException {

        if (!validate(source, destination, amount)) {
            throw new IllegalArgumentException("Paramètres de retrait invalides");
        }

        if (source.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Solde insuffisant. Disponible: " + source.getBalance() +
                            ", Demandé: " + amount
            );
        }

        source.debit(amount);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                "WITHDRAW",
                source.getAccountNumber(),
                null,
                amount,
                LocalDateTime.now(),
                "Retrait effectué avec succès"
        );

        return transaction;
    }

    @Override
    public boolean validate(Account source, Account destination, double amount) {
        return source != null && amount > 0;
    }

    @Override
    public String getTransactionType() {
        return "WITHDRAW";
    }

    private String generateTransactionId() {
        return "WTH-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}