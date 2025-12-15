package com.exemple.pattern.factory;


import com.exemple.model.Account;
import com.exemple.model.User;

/**
 * Factory pour la création de comptes bancaires
 */
public class AccountFactory {

    private static long accountCounter = 1000;

    /**
     * Crée un compte courant standard
     */
    public static Account createCheckingAccount(User owner, double initialBalance) {
        String accountNumber = generateAccountNumber("CHK");
        return new Account(accountNumber, owner.getUserId(), initialBalance, "CHECKING");
    }

    /**
     * Crée un compte épargne
     */
    public static Account createSavingsAccount(User owner, double initialBalance) {
        String accountNumber = generateAccountNumber("SAV");
        return new Account(accountNumber, owner.getUserId(), initialBalance, "SAVINGS");
    }

    /**
     * Crée un compte business
     */
    public static Account createBusinessAccount(User owner, double initialBalance) {
        if (initialBalance < 1000.0) {
            throw new IllegalArgumentException(
                    "Les comptes business nécessitent un dépôt minimum de 1000.0"
            );
        }
        String accountNumber = generateAccountNumber("BUS");
        return new Account(accountNumber, owner.getUserId(), initialBalance, "BUSINESS");
    }

    /**
     * Crée un compte avec type spécifié
     */
    public static Account createAccount(User owner, double initialBalance, String accountType) {
        switch (accountType.toUpperCase()) {
            case "CHECKING":
                return createCheckingAccount(owner, initialBalance);
            case "SAVINGS":
                return createSavingsAccount(owner, initialBalance);
            case "BUSINESS":
                return createBusinessAccount(owner, initialBalance);
            default:
                throw new IllegalArgumentException("Type de compte invalide: " + accountType);
        }
    }

    /**
     * Génère un numéro de compte unique
     */
    private static synchronized String generateAccountNumber(String prefix) {
        accountCounter++;
        return prefix + "-" + String.format("%08d", accountCounter);
    }

    /**
     * Réinitialise le compteur (pour les tests)
     */
    public static void resetCounter() {
        accountCounter = 1000;
    }
}