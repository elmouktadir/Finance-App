package com.exemple.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modèle représentant un compte bancaire
 */
public class Account {

    private String accountNumber;
    private String ownerId;
    private double balance;
    private String accountType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isActive;

    public Account(String accountNumber, String ownerId, double balance, String accountType) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de compte ne peut pas être vide");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du propriétaire ne peut pas être vide");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Le solde ne peut pas être négatif");
        }

        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        this.balance = balance;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.isActive = true;
    }

    public synchronized void credit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du crédit doit être positif");
        }
        if (!isActive) {
            throw new IllegalStateException("Le compte est désactivé");
        }

        this.balance += amount;
        this.lastModified = LocalDateTime.now();
    }

    public synchronized void debit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du débit doit être positif");
        }
        if (!isActive) {
            throw new IllegalStateException("Le compte est désactivé");
        }
        if (balance < amount) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        this.balance -= amount;
        this.lastModified = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.lastModified = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.lastModified = LocalDateTime.now();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format(
                "Account{number='%s', type='%s', balance=%.2f, active=%s}",
                accountNumber, accountType, balance, isActive
        );
    }
}