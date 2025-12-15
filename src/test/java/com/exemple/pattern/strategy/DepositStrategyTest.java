package com.exemple.pattern. strategy;

import com.exemple.model.Account;
import com.exemple.model.Transaction;
import com.exemple.model.User;
import com.exemple.pattern.factory.AccountFactory;
import com.exemple.pattern.factory.UserFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour DepositStrategy
 */
public class DepositStrategyTest {

    private DepositStrategy depositStrategy;
    private User testUser;
    private Account testAccount;

    @Before
    public void setUp() {
        depositStrategy = new DepositStrategy();
        testUser = UserFactory.createStandardUser("testuser", "password123", "test@email.com");
        testAccount = AccountFactory.createCheckingAccount(testUser, 1000.0);
    }

    @Test
    public void testExecuteDeposit_Success() {
        double initialBalance = testAccount.getBalance();
        double depositAmount = 500.0;

        Transaction transaction = depositStrategy.execute(null, testAccount, depositAmount);

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être DEPOSIT", "DEPOSIT", transaction.getType());
        assertEquals("Le montant doit correspondre", depositAmount, transaction.getAmount(), 0.01);
        assertEquals("Le nouveau solde doit être correct",
                initialBalance + depositAmount,
                testAccount.getBalance(),
                0.01);
    }

    @Test
    public void testExecuteDeposit_UpdatesBalance() {
        double depositAmount = 250.75;

        depositStrategy.execute(null, testAccount, depositAmount);

        assertEquals("Le solde doit être mis à jour", 1250.75, testAccount.getBalance(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteDeposit_NullAccount_ThrowsException() {
        depositStrategy.execute(null, null, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteDeposit_NegativeAmount_ThrowsException() {
        depositStrategy.execute(null, testAccount, -50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteDeposit_ZeroAmount_ThrowsException() {
        depositStrategy.execute(null, testAccount, 0.0);
    }

    @Test
    public void testValidate_ValidParameters_ReturnsTrue() {
        boolean result = depositStrategy.validate(null, testAccount, 100.0);

        assertTrue("Devrait retourner true pour des paramètres valides", result);
    }

    @Test
    public void testValidate_NullAccount_ReturnsFalse() {
        boolean result = depositStrategy.validate(null, null, 100.0);

        assertFalse("Devrait retourner false pour un compte null", result);
    }

    @Test
    public void testValidate_NegativeAmount_ReturnsFalse() {
        boolean result = depositStrategy.validate(null, testAccount, -100.0);

        assertFalse("Devrait retourner false pour un montant négatif", result);
    }

    @Test
    public void testGetTransactionType() {
        String type = depositStrategy.getTransactionType();

        assertEquals("Le type doit être DEPOSIT", "DEPOSIT", type);
    }

    @Test
    public void testMultipleDeposits() {
        double initialBalance = testAccount.getBalance();

        depositStrategy.execute(null, testAccount, 100.0);
        depositStrategy.execute(null, testAccount, 200.0);
        depositStrategy.execute(null, testAccount, 300.0);

        assertEquals("Le solde doit refléter tous les dépôts",
                initialBalance + 600.0,
                testAccount.getBalance(),
                0.01);
    }

    @Test
    public void testTransactionId_IsUnique() {
        Transaction t1 = depositStrategy.execute(null, testAccount, 100.0);
        Transaction t2 = depositStrategy.execute(null, testAccount, 200.0);

        assertNotEquals("Les IDs doivent être uniques",
                t1.getTransactionId(),
                t2.getTransactionId());
    }

    @Test
    public void testTransactionContainsTimestamp() {
        Transaction transaction = depositStrategy.execute(null, testAccount, 100.0);

        assertNotNull("La transaction doit avoir un timestamp", transaction.getTimestamp());
    }

    @Test
    public void testTransactionDescription() {
        Transaction transaction = depositStrategy.execute(null, testAccount, 100.0);

        assertNotNull("La description ne doit pas être null", transaction.getDescription());
        assertTrue("La description doit mentionner le succès",
                transaction.getDescription().contains("succès"));
    }
}