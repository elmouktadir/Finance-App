package com.exemple.pattern.observer;


import com.exemple.model.Transaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Observer qui gère les notifications pour les utilisateurs
 */
public class NotificationService implements TransactionObserver {

    private List<String> notifications;
    private double alertThreshold;

    public NotificationService() {
        this.notifications = new ArrayList<>();
        this.alertThreshold = 1000.0;
    }

    public NotificationService(double alertThreshold) {
        this.notifications = new ArrayList<>();
        this.alertThreshold = alertThreshold;
    }

    @Override
    public void onTransactionExecuted(Transaction transaction) {
        String message;

        switch (transaction.getType()) {
            case "DEPOSIT":
                message = String.format(
                        "Dépôt de %.2f effectué sur le compte %s",
                        transaction.getAmount(),
                        transaction.getDestinationAccount()
                );
                break;

            case "WITHDRAW":
                message = String.format(
                        "Retrait de %.2f effectué depuis le compte %s",
                        transaction.getAmount(),
                        transaction.getSourceAccount()
                );
                break;

            case "TRANSFER":
                message = String.format(
                        "Transfert de %.2f du compte %s vers %s",
                        transaction.getAmount(),
                        transaction.getSourceAccount(),
                        transaction.getDestinationAccount()
                );
                break;

            default:
                message = "Transaction effectuée: " + transaction.getTransactionId();
        }

        sendNotification(message);

        if (transaction.getAmount() >= alertThreshold) {
            String alertMessage = String.format(
                    "⚠️ ALERTE: Transaction importante de %.2f détectée! ID: %s",
                    transaction.getAmount(),
                    transaction.getTransactionId()
            );
            sendNotification(alertMessage);
        }
    }

    @Override
    public void onTransactionFailed(Transaction transaction, String reason) {
        String message = String.format(
                "❌ Transaction échouée: %s. Raison: %s",
                transaction != null ? transaction.getTransactionId() : "N/A",
                reason
        );
        sendNotification(message);
    }

    @Override
    public String getObserverName() {
        return "NotificationService";
    }

    /**
     * Simule l'envoi d'une notification
     */
    private void sendNotification(String message) {
        notifications.add(message);
        System.out.println("[NOTIFICATION] " + message);
    }

    /**
     * Retourne toutes les notifications
     */
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Efface toutes les notifications
     */
    public void clearNotifications() {
        notifications.clear();
    }

    /**
     * Retourne le nombre de notifications envoyées
     */
    public int getNotificationCount() {
        return notifications.size();
    }

    /**
     * Définit le seuil d'alerte
     */
    public void setAlertThreshold(double threshold) {
        this.alertThreshold = threshold;
    }
}
