package com.exemple;


import com.exemple.model.Account;
import com.exemple.model.User;
import com.exemple.pattern.observer.AuditLogger;
import com.exemple.pattern.observer.NotificationService;
import com.exemple.pattern.strategy.*;
import com.exemple.service.BankingService;
import com.exemple.service.TransactionService;

import java.util.List;
import java.util.Scanner;

/**
 * Application principale refactorisée avec design patterns
 */
public class Main {

    private static BankingService bankingService;
    private static TransactionService transactionService;
    private static Scanner scanner;
    private static User currentUser;

    public static void main(String[] args) {
        bankingService = new BankingService();
        transactionService = new TransactionService();
        scanner = new Scanner(System.in);

        transactionService.addObserver(new AuditLogger());
        transactionService.addObserver(new NotificationService(500.0));

        initializeSampleData();

        showWelcomeMessage();
        mainLoop();

        scanner.close();
    }

    private static void initializeSampleData() {
        try {
            User user1 = bankingService.registerUser("alice", "password123",
                    "alice@email.com", "STANDARD");
            User user2 = bankingService.registerUser("bob", "password456",
                    "bob@email.com", "PREMIUM");

            bankingService.createAccount(user1.getUserId(), 1000.0, "CHECKING");
            bankingService.createAccount(user2.getUserId(), 500.0, "SAVINGS");

            System.out.println("✓ Données de test initialisées");
            System.out.println("  - Utilisateurs: alice/password123, bob/password456");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private static void showWelcomeMessage() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Système Bancaire Refactorisé v2.0    ║");
        System.out.println("║  Architecture avec Design Patterns     ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    private static void mainLoop() {
        boolean running = true;

        while (running) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Menu Principal ===");
        System.out.println("1. Se connecter");
        System.out.println("2. S'inscrire");
        System.out.println("0. Quitter");
        System.out.print("Choix: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 0:
                    System.out.println("Au revoir!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Choix invalide");
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            scanner.nextLine();
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== Bienvenue " + currentUser.getUsername() + " ===");
        System.out.println("1. Afficher mes comptes");
        System.out.println("2. Déposer de l'argent");
        System.out.println("3. Retirer de l'argent");
        System.out.println("4. Effectuer un transfert");
        System.out.println("5. Historique des transactions");
        System.out.println("6. Créer un nouveau compte");
        System.out.println("7. Statistiques");
        System.out.println("0. Se déconnecter");
        System.out.print("Choix: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showAccounts();
                    break;
                case 2:
                    handleDeposit();
                    break;
                case 3:
                    handleWithdrawal();
                    break;
                case 4:
                    handleTransfer();
                    break;
                case 5:
                    showTransactionHistory();
                    break;
                case 6:
                    createNewAccount();
                    break;
                case 7:
                    showStatistics();
                    break;
                case 0:
                    currentUser = null;
                    System.out.println("Déconnexion réussie");
                    break;
                default:
                    System.out.println("Choix invalide");
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            scanner.nextLine();
        }
    }

    private static void handleLogin() {
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            currentUser = bankingService.authenticate(username, password);
            System.out.println("✓ Connexion réussie! Bienvenue " + currentUser.getUsername());
        } catch (Exception e) {
            System.err.println("✗ Échec de connexion: " + e.getMessage());
        }
    }

    private static void handleRegister() {
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Type (STANDARD/PREMIUM): ");
        String type = scanner.nextLine();

        try {
            User newUser = bankingService.registerUser(username, password, email, type);
            System.out.println("✓ Inscription réussie! ID: " + newUser.getUserId());

            System.out.print("Dépôt initial: ");
            double deposit = scanner.nextDouble();
            scanner.nextLine();

            bankingService.createAccount(newUser.getUserId(), deposit, "CHECKING");
            System.out.println("✓ Compte créé avec succès");
        } catch (Exception e) {
            System.err.println("✗ Échec d'inscription: " + e.getMessage());
        }
    }

    private static void showAccounts() {
        List<Account> accounts = bankingService.getUserAccounts(currentUser.getUserId());

        System.out.println("\n=== Mes Comptes ===");
        for (Account account : accounts) {
            System.out.printf("Compte: %s | Type: %s | Solde: %.2f | Statut: %s\n",
                    account.getAccountNumber(),
                    account.getAccountType(),
                    account.getBalance(),
                    account.isActive() ? "Actif" : "Inactif"
            );
        }
    }

    private static void handleDeposit() {
        showAccounts();
        System.out.print("\nNuméro de compte: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Montant à déposer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            Account account = bankingService.getAccount(accountNumber);
            if (account == null) {
                System.err.println("Compte non trouvé");
                return;
            }

            TransactionStrategy depositStrategy = new DepositStrategy();
            transactionService.executeTransaction(depositStrategy, null, account, amount);

            System.out.println("✓ Dépôt effectué avec succès!");
            System.out.printf("Nouveau solde: %.2f\n", account.getBalance());
        } catch (Exception e) {
            System.err.println("✗ Échec du dépôt: " + e.getMessage());
        }
    }

    private static void handleWithdrawal() {
        showAccounts();
        System.out.print("\nNuméro de compte: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Montant à retirer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            Account account = bankingService.getAccount(accountNumber);
            if (account == null) {
                System.err.println("Compte non trouvé");
                return;
            }

            TransactionStrategy withdrawStrategy = new WithdrawStrategy();
            transactionService.executeTransaction(withdrawStrategy, account, null, amount);

            System.out.println("✓ Retrait effectué avec succès!");
            System.out.printf("Nouveau solde: %.2f\n", account.getBalance());
        } catch (Exception e) {
            System.err.println("✗ Échec du retrait: " + e.getMessage());
        }
    }

    private static void handleTransfer() {
        showAccounts();
        System.out.print("\nCompte source: ");
        String sourceNumber = scanner.nextLine();
        System.out.print("Compte destination: ");
        String destNumber = scanner.nextLine();
        System.out.print("Montant: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            Account source = bankingService.getAccount(sourceNumber);
            Account destination = bankingService.getAccount(destNumber);

            if (source == null || destination == null) {
                System.err.println("Un ou plusieurs comptes non trouvés");
                return;
            }

            TransactionStrategy transferStrategy = new TransferStrategy();
            transactionService.executeTransaction(transferStrategy, source, destination, amount);

            System.out.println("✓ Transfert effectué avec succès!");
            System.out.printf("Nouveau solde source: %.2f\n", source.getBalance());
            System.out.printf("Nouveau solde destination: %.2f\n", destination.getBalance());
        } catch (Exception e) {
            System.err.println("✗ Échec du transfert: " + e.getMessage());
        }
    }

    private static void showTransactionHistory() {
        List<Account> accounts = bankingService.getUserAccounts(currentUser.getUserId());

        System.out.println("\n=== Historique des Transactions ===");
        for (Account account : accounts) {
            System.out.println("\nCompte: " + account.getAccountNumber());
            List<com.exemple.model.Transaction> transactions =
                    transactionService.getAccountTransactions(account.getAccountNumber());

            if (transactions.isEmpty()) {
                System.out.println("  Aucune transaction");
            } else {
                for (com.exemple.model.Transaction t : transactions) {
                    System.out.println("  " + t.getFormattedTransaction());
                }
            }
        }
    }

    private static void createNewAccount() {
        System.out.print("Type de compte (CHECKING/SAVINGS/BUSINESS): ");
        String type = scanner.nextLine();
        System.out.print("Dépôt initial: ");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();

        try {
            Account newAccount = bankingService.createAccount(
                    currentUser.getUserId(), initialDeposit, type
            );
            System.out.println("✓ Compte créé avec succès!");
            System.out.println("  Numéro: " + newAccount.getAccountNumber());
        } catch (Exception e) {
            System.err.println("✗ Échec de création: " + e.getMessage());
        }
    }

    private static void showStatistics() {
        System.out.println("\n=== Statistiques ===");
        System.out.println("Utilisateurs actifs: " + bankingService.getActiveUsers().size());
        System.out.println("Comptes actifs: " + bankingService.getActiveAccounts().size());
        System.out.println("Transactions totales: " + transactionService.getTotalTransactionCount());
        System.out.println("Transactions réussies: " + transactionService.getSuccessfulTransactionCount());
    }
}