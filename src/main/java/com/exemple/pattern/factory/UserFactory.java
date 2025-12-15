package com.exemple.pattern.factory;


import com.exemple.model.User;
import java.util.regex.Pattern;

/**
 * Factory pour la création d'utilisateurs
 */
public class UserFactory {

    private static long userCounter = 1000;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Crée un utilisateur standard
     */
    public static User createStandardUser(String username, String password, String email) {
        validateUserData(username, password, email);

        String userId = generateUserId();
        return new User(userId, username, hashPassword(password), email, "STANDARD");
    }

    /**
     * Crée un utilisateur premium
     */
    public static User createPremiumUser(String username, String password, String email) {
        validateUserData(username, password, email);

        String userId = generateUserId();
        return new User(userId, username, hashPassword(password), email, "PREMIUM");
    }

    /**
     * Crée un administrateur
     */
    public static User createAdminUser(String username, String password, String email) {
        validateUserData(username, password, email);

        String userId = generateUserId();
        return new User(userId, username, hashPassword(password), email, "ADMIN");
    }

    /**
     * Crée un utilisateur avec type spécifié
     */
    public static User createUser(String username, String password, String email, String userType) {
        switch (userType.toUpperCase()) {
            case "STANDARD":
                return createStandardUser(username, password, email);
            case "PREMIUM":
                return createPremiumUser(username, password, email);
            case "ADMIN":
                return createAdminUser(username, password, email);
            default:
                throw new IllegalArgumentException("Type d'utilisateur invalide: " + userType);
        }
    }

    /**
     * Valide les données de l'utilisateur
     */
    private static void validateUserData(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException(
                    "Le nom d'utilisateur doit contenir au moins 3 caractères"
            );
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins 6 caractères"
            );
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
    }

    /**
     * Hash simple du mot de passe
     */
    private static String hashPassword(String password) {
        return "HASHED_" + password.hashCode();
    }

    /**
     * Génère un ID utilisateur unique
     */
    private static synchronized String generateUserId() {
        userCounter++;
        return "USR-" + String.format("%06d", userCounter);
    }

    /**
     * Réinitialise le compteur (pour les tests)
     */
    public static void resetCounter() {
        userCounter = 1000;
    }
}