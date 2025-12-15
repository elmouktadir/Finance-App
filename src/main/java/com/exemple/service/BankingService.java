package com.exemple.service;


import com.exemple.model.Account;
import com.exemple.model.User;
import com.exemple.pattern.factory.AccountFactory;
import com.exemple.pattern.factory.UserFactory;

import java.util.*;

/**
 * Service principal pour la gestion des comptes et utilisateurs
 */
public class BankingService {

    private Map<String, User> users;
    private Map<String, Account> accounts;
    private Map<String, List<Account>> userAccounts;

    public BankingService() {
        this.users = new HashMap<>();
        this.accounts = new HashMap<>();
        this.userAccounts = new HashMap<>();
    }

    /**
     * Enregistre un nouvel utilisateur
     */
    public User registerUser(String username, String password, String email, String userType) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                throw new IllegalArgumentException("Le nom d'utilisateur existe déjà");
            }
            if (user.getEmail().equals(email)) {
                throw new IllegalArgumentException("L'email est déjà utilisé");
            }
        }

        User newUser = UserFactory.createUser(username, password, email, userType);
        users.put(newUser.getUserId(), newUser);
        userAccounts.put(newUser.getUserId(), new ArrayList<>());

        return newUser;
    }

    /**
     * Crée un nouveau compte pour un utilisateur
     */
    public Account createAccount(String userId, double initialBalance, String accountType) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("L'utilisateur est désactivé");
        }

        Account newAccount = AccountFactory.createAccount(user, initialBalance, accountType);
        accounts.put(newAccount.getAccountNumber(), newAccount);
        userAccounts.get(userId).add(newAccount);

        return newAccount;
    }

    /**
     * Récupère un utilisateur par son username
     */
    public User getUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(String userId) {
        return users.get(userId);
    }

    /**
     * Récupère un compte par son numéro
     */
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Récupère tous les comptes d'un utilisateur
     */
    public List<Account> getUserAccounts(String userId) {
        return userAccounts.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * Authentifie un utilisateur
     */
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Nom d'utilisateur ou mot de passe incorrect");
        }

        String hashedPassword = "HASHED_" + password.hashCode();
        if (!user.verifyPassword(hashedPassword)) {
            throw new IllegalArgumentException("Nom d'utilisateur ou mot de passe incorrect");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Ce compte est désactivé");
        }

        user.updateLastLogin();
        return user;
    }

    /**
     * Désactive un utilisateur et tous ses comptes
     */
    public void deactivateUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        user.deactivate();

        List<Account> userAccountsList = userAccounts.get(userId);
        if (userAccountsList != null) {
            for (Account account : userAccountsList) {
                account.deactivate();
            }
        }
    }

    /**
     * Ferme un compte spécifique
     */
    public void closeAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Compte non trouvé");
        }

        if (account.getBalance() > 0) {
            throw new IllegalStateException(
                    "Impossible de fermer un compte avec un solde positif. Solde actuel: "
                            + account.getBalance()
            );
        }

        account.deactivate();
    }

    /**
     * Retourne le nombre total d'utilisateurs
     */
    public int getTotalUsers() {
        return users.size();
    }

    /**
     * Retourne le nombre total de comptes
     */
    public int getTotalAccounts() {
        return accounts.size();
    }

    /**
     * Retourne tous les utilisateurs actifs
     */
    public List<User> getActiveUsers() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }

    /**
     * Retourne tous les comptes actifs
     */
    public List<Account> getActiveAccounts() {
        List<Account> activeAccounts = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.isActive()) {
                activeAccounts.add(account);
            }
        }
        return activeAccounts;
    }
}
