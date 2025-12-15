package com.exemple.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Modèle représentant une transaction bancaire
 */
public class Transaction {

    private String transactionId;
    private String type;
    private String sourceAccount;
    private String destinationAccount;
    private double amount;
    private LocalDateTime timestamp;
    private String description;
    private String status;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String transactionId, String type, String sourceAccount,
                       String destinationAccount, double amount,
                       LocalDateTime timestamp, String description) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de transaction ne peut pas être vide");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        this.transactionId = transactionId;
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
        this.status = "COMPLETED";
    }

    public Transaction(String transactionId, String type, String sourceAccount,
                       String destinationAccount, double amount,
                       LocalDateTime timestamp, String description, String status) {
        this(transactionId, type, sourceAccount, destinationAccount,
                amount, timestamp, description);
        this.status = status;
    }

    public void markAsFailed(String reason) {
        this.status = "FAILED";
        this.description = description + " | Raison: " + reason;
    }

    public void markAsCancelled() {
        this.status = "CANCELLED";
    }

    public boolean isSuccessful() {
        return "COMPLETED".equals(status);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getFormattedTransaction() {
        return String.format(
                "[%s] %s | %s | Montant: %.2f | De: %s | Vers: %s | Statut: %s",
                timestamp.format(FORMATTER),
                transactionId,
                type,
                amount,
                sourceAccount != null ? sourceAccount : "N/A",
                destinationAccount != null ? destinationAccount : "N/A",
                status
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return getFormattedTransaction();
    }
}