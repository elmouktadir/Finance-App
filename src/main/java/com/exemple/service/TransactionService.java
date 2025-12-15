package com.exemple.service;


import com.exemple.Exceptions.InsufficientFundsException;
import com.exemple.model.Account;
import com.exemple.model.Transaction;
import com.exemple.pattern.observer.TransactionObserver;
import com.exemple.pattern.strategy.TransactionStrategy;

import java.util.*;

/**
 * Service de gestion des transactions avec pattern Strategy et Observer
 */
public class TransactionService {

    private Map<String, Transaction> transactionHistory;
    private List<TransactionObserver> observers;

    public TransactionService() {
        this.transactionHistory = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Ajoute un observateur
     */
    public void addObserver(TransactionObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observateur ajouté: " + observer.getObserverName());
        }
    }

    /**
     * Retire un observateur
     */
    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    /**
     * Exécute une transaction en utilisant une stratégie
     */
    public Transaction executeTransaction(TransactionStrategy strategy,
                                          Account source,
                                          Account destination,
                                          double amount) {
        Transaction transaction = null;

        try {
            transaction = strategy.execute(source, destination, amount);

            transactionHistory.put(transaction.getTransactionId(), transaction);

            notifyObserversSuccess(transaction);

            return transaction;

        } catch (IllegalArgumentException | InsufficientFundsException e) {
            if (transaction != null) {
                transaction.markAsFailed(e.getMessage());
            }

            notifyObserversFailure(transaction, e.getMessage());

            throw new RuntimeException("Échec de la transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Notifie tous les observateurs du succès d'une transaction
     */
    private void notifyObserversSuccess(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            try {
                observer.onTransactionExecuted(transaction);
            } catch (Exception e) {
                System.err.println("Erreur dans l'observateur " +
                        observer.getObserverName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Notifie tous les observateurs de l'échec d'une transaction
     */
    private void notifyObserversFailure(Transaction transaction, String reason) {
        for (TransactionObserver observer : observers) {
            try {
                observer.onTransactionFailed(transaction, reason);
            } catch (Exception e) {
                System.err.println("Erreur dans l'observateur " +
                        observer.getObserverName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Récupère une transaction par son ID
     */
    public Transaction getTransaction(String transactionId) {
        return transactionHistory.get(transactionId);
    }

    /**
     * Récupère toutes les transactions d'un compte
     */
    public List<Transaction> getAccountTransactions(String accountNumber) {
        List<Transaction> accountTransactions = new ArrayList<>();

        for (Transaction transaction : transactionHistory.values()) {
            if (accountNumber.equals(transaction.getSourceAccount()) ||
                    accountNumber.equals(transaction.getDestinationAccount())) {
                accountTransactions.add(transaction);
            }
        }

        accountTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        return accountTransactions;
    }

    /**
     * Récupère toutes les transactions d'un certain type
     */
    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> typeTransactions = new ArrayList<>();

        for (Transaction transaction : transactionHistory.values()) {
            if (type.equals(transaction.getType())) {
                typeTransactions.add(transaction);
            }
        }

        return typeTransactions;
    }

    /**
     * Calcule le total des transactions pour un compte
     */
    public double getTotalTransactionAmount(String accountNumber) {
        double total = 0.0;

        for (Transaction transaction : transactionHistory.values()) {
            if (accountNumber.equals(transaction.getDestinationAccount())) {
                total += transaction.getAmount();
            }
            if (accountNumber.equals(transaction.getSourceAccount())) {
                total -= transaction.getAmount();
            }
        }

        return total;
    }

    /**
     * Retourne le nombre total de transactions
     */
    public int getTotalTransactionCount() {
        return transactionHistory.size();
    }

    /**
     * Retourne le nombre de transactions réussies
     */
    public int getSuccessfulTransactionCount() {
        int count = 0;
        for (Transaction transaction : transactionHistory.values()) {
            if (transaction.isSuccessful()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retourne toutes les transactions
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactionHistory.values());
    }

    /**
     * Efface l'historique des transactions
     */
    public void clearHistory() {
        transactionHistory.clear();
    }
}