package com.exemple.pattern.strategy;

import com.exemple.Exceptions.InsufficientFundsException;
import com.exemple.model.Account;
import com.exemple.model.Transaction;
import java.time.LocalDateTime;

/**
 * Stratégie pour les opérations de transfert entre comptes
 */
public class TransferStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account source, Account destination, double amount)
            throws IllegalArgumentException, InsufficientFundsException {

        if (!validate(source, destination, amount)) {
            throw new IllegalArgumentException("Paramètres de transfert invalides");
        }

        if (source.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Solde insuffisant pour le transfert. Disponible: " + source.getBalance() +
                            ", Demandé: " + amount
            );
        }

        if (source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new IllegalArgumentException("Impossible de transférer vers le même compte");
        }

        source.debit(amount);
        destination.credit(amount);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                "TRANSFER",
                source.getAccountNumber(),
                destination.getAccountNumber(),
                amount,
                LocalDateTime.now(),
                "Transfert de " + source.getAccountNumber() +
                        " vers " + destination.getAccountNumber()
        );

        return transaction;
    }

    @Override
    public boolean validate(Account source, Account destination, double amount) {
        return source != null && destination != null && amount > 0;
    }

    @Override
    public String getTransactionType() {
        return "TRANSFER";
    }

    private String generateTransactionId() {
        return "TRF-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}