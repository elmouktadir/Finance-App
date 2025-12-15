package com.exemple.pattern.observer;


import com.exemple.model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

/**
 * Observer qui enregistre toutes les transactions dans un fichier d'audit
 */
public class AuditLogger implements TransactionObserver {

    private static final String AUDIT_FILE = "transactions_audit.log";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onTransactionExecuted(Transaction transaction) {
        String logEntry = String.format(
                "[SUCCESS] %s | Type: %s | From: %s | To: %s | Amount: %.2f | ID: %s",
                transaction.getTimestamp().format(DATE_FORMATTER),
                transaction.getType(),
                transaction.getSourceAccount() != null ? transaction.getSourceAccount() : "N/A",
                transaction.getDestinationAccount() != null ? transaction.getDestinationAccount() : "N/A",
                transaction.getAmount(),
                transaction.getTransactionId()
        );

        writeToLog(logEntry);
        System.out.println("[AUDIT] Transaction enregistrée: " + transaction.getTransactionId());
    }

    @Override
    public void onTransactionFailed(Transaction transaction, String reason) {
        String logEntry = String.format(
                "[FAILED] %s | Type: %s | From: %s | To: %s | Amount: %.2f | Reason: %s",
                java.time.LocalDateTime.now().format(DATE_FORMATTER),
                transaction != null ? transaction.getType() : "UNKNOWN",
                transaction != null && transaction.getSourceAccount() != null ?
                        transaction.getSourceAccount() : "N/A",
                transaction != null && transaction.getDestinationAccount() != null ?
                        transaction.getDestinationAccount() : "N/A",
                transaction != null ? transaction.getAmount() : 0.0,
                reason
        );

        writeToLog(logEntry);
        System.err.println("[AUDIT] Transaction échouée: " + reason);
    }

    @Override
    public String getObserverName() {
        return "AuditLogger";
    }

    /**
     * Écrit une entrée dans le fichier de log
     */
    private void writeToLog(String entry) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
            writer.println(entry);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du log: " + e.getMessage());
        }
    }
}