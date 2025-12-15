package com.exemple.pattern.observer;


import com.exemple.model.Transaction;

/**
 * Interface Observer pour le pattern Observer
 */
public interface TransactionObserver {

    /**
     * Méthode appelée quand une transaction est effectuée
     */
    void onTransactionExecuted(Transaction transaction);

    /**
     * Méthode appelée quand une transaction échoue
     */
    void onTransactionFailed(Transaction transaction, String reason);

    /**
     * Retourne le nom de l'observateur
     */
    String getObserverName();
}