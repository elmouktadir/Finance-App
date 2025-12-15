package com.exemple.pattern.strategy;


import com.exemple.Exceptions.InsufficientFundsException;
import com.exemple.model.Account;
import com.exemple.model.Transaction;
import com.exemple.model.User;
import com.exemple.pattern.factory.AccountFactory;
import com.exemple.pattern.factory.UserFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour WithdrawStrategy
 */
public class WithdrawStrategyTest {

    private WithdrawStrategy withdrawStrategy;
    private User testUser;
    private Account testAccount;

    @Before
    public void setUp() {
        withdrawStrategy = new WithdrawStrategy();
        testUser = UserFactory.createStandardUser("testuser", "password123", "test@email.com");
        testAccount = AccountFactory.createCheckingAccount(testUser, 1000.0);
    }

    @Test
    public void testExecuteWithdraw_Success() throws InsufficientFundsException {
        double initialBalance = testAccount.getBalance();
        double withdrawAmount = 300.0;

        Transaction transaction = withdrawStrategy.execute(testAccount, null, withdrawAmount);

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être WITHDRAW", "WITHDRAW", transaction.getType());
        assertEquals("Le montant doit correspondre", withdrawAmount, transaction.getAmount(), 0.01);
        assertEquals("Le nouveau solde doit être correct",
                initialBalance - withdrawAmount,
                testAccount.getBalance(),
                0.01);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testExecuteWithdraw_InsufficientFunds_ThrowsException()
            throws InsufficientFundsException {
        double withdrawAmount = 1500.0;

        withdrawStrategy.execute(testAccount, null, withdrawAmount);
    }

    @Test
    public void testExecuteWithdraw_ExactBalance() throws InsufficientFundsException {
        double withdrawAmount = testAccount.getBalance();

        Transaction transaction = withdrawStrategy.execute(testAccount, null, withdrawAmount);

        assertEquals("Le solde doit être 0", 0.0, testAccount.getBalance(), 0.01);
        assertNotNull("La transaction doit être créée", transaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithdraw_NullAccount_ThrowsException()
            throws InsufficientFundsException {
        withdrawStrategy.execute(null, null, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithdraw_NegativeAmount_ThrowsException()
            throws InsufficientFundsException {
        withdrawStrategy.execute(testAccount, null, -50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithdraw_ZeroAmount_ThrowsException()
            throws InsufficientFundsException {
        withdrawStrategy.execute(testAccount, null, 0.0);
    }

    @Test
    public void testValidate_ValidParameters_ReturnsTrue() {
        boolean result = withdrawStrategy.validate(testAccount, null, 100.0);

        assertTrue("Devrait retourner true pour des paramètres valides", result);
    }

    @Test
    public void testValidate_NullAccount_ReturnsFalse() {
        boolean result = withdrawStrategy.validate(null, null, 100.0);

        assertFalse("Devrait retourner false pour un compte null", result);
    }

    @Test
    public void testGetTransactionType() {
        String type = withdrawStrategy.getTransactionType();

        assertEquals("Le type doit être WITHDRAW", "WITHDRAW", type);
    }

    @Test
    public void testMultipleWithdrawals() throws InsufficientFundsException {
        double initialBalance = testAccount.getBalance();

        withdrawStrategy.execute(testAccount, null, 100.0);
        withdrawStrategy.execute(testAccount, null, 200.0);
        withdrawStrategy.execute(testAccount, null, 150.0);

        assertEquals("Le solde doit refléter tous les retraits",
                initialBalance - 450.0,
                testAccount.getBalance(),
                0.01);
    }

    @Test
    public void testTransactionId_IsUnique() throws InsufficientFundsException {
        Transaction t1 = withdrawStrategy.execute(testAccount, null, 100.0);
        Transaction t2 = withdrawStrategy.execute(testAccount, null, 200.0);

        assertNotEquals("Les IDs doivent être uniques",
                t1.getTransactionId(),
                t2.getTransactionId());
    }

    @Test
    public void testWithdrawSmallAmount() throws InsufficientFundsException {
        Transaction transaction = withdrawStrategy.execute(testAccount, null, 0.01);

        assertEquals("Devrait pouvoir retirer un très petit montant",
                999.99,
                testAccount.getBalance(),
                0.01);
    }
}