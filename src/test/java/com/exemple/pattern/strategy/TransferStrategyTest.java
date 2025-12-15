package com.exemple. pattern.strategy;


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
 * Tests unitaires pour TransferStrategy
 */
public class TransferStrategyTest {

    private TransferStrategy transferStrategy;
    private User sourceUser;
    private User destUser;
    private Account sourceAccount;
    private Account destAccount;

    @Before
    public void setUp() {
        transferStrategy = new TransferStrategy();
        sourceUser = UserFactory.createStandardUser("source", "password123", "source@email.com");
        destUser = UserFactory.createStandardUser("dest", "password456", "dest@email.com");
        sourceAccount = AccountFactory.createCheckingAccount(sourceUser, 1000.0);
        destAccount = AccountFactory.createCheckingAccount(destUser, 500.0);
    }

    @Test
    public void testExecuteTransfer_Success() throws InsufficientFundsException {
        double initialSourceBalance = sourceAccount.getBalance();
        double initialDestBalance = destAccount.getBalance();
        double transferAmount = 300.0;

        Transaction transaction = transferStrategy.execute(sourceAccount, destAccount, transferAmount);

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être TRANSFER", "TRANSFER", transaction.getType());
        assertEquals("Le montant doit correspondre", transferAmount, transaction.getAmount(), 0.01);
        assertEquals("Le solde source doit être débité",
                initialSourceBalance - transferAmount,
                sourceAccount.getBalance(),
                0.01);
        assertEquals("Le solde destination doit être crédité",
                initialDestBalance + transferAmount,
                destAccount.getBalance(),
                0.01);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testExecuteTransfer_InsufficientFunds_ThrowsException()
            throws InsufficientFundsException {
        double transferAmount = 1500.0;

        transferStrategy.execute(sourceAccount, destAccount, transferAmount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteTransfer_SameAccount_ThrowsException()
            throws InsufficientFundsException {
        transferStrategy.execute(sourceAccount, sourceAccount, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteTransfer_NullSource_ThrowsException()
            throws InsufficientFundsException {
        transferStrategy.execute(null, destAccount, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteTransfer_NullDestination_ThrowsException()
            throws InsufficientFundsException {
        transferStrategy.execute(sourceAccount, null, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteTransfer_NegativeAmount_ThrowsException()
            throws InsufficientFundsException {
        transferStrategy.execute(sourceAccount, destAccount, -50.0);
    }

    @Test
    public void testExecuteTransfer_ExactBalance() throws InsufficientFundsException {
        double transferAmount = sourceAccount.getBalance();

        Transaction transaction = transferStrategy.execute(sourceAccount, destAccount, transferAmount);

        assertEquals("Le solde source doit être 0", 0.0, sourceAccount.getBalance(), 0.01);
        assertEquals("Le solde destination doit inclure le transfert",
                1500.0,
                destAccount.getBalance(),
                0.01);
        assertNotNull("La transaction doit être créée", transaction);
    }

    @Test
    public void testValidate_ValidParameters_ReturnsTrue() {
        boolean result = transferStrategy.validate(sourceAccount, destAccount, 100.0);

        assertTrue("Devrait retourner true pour des paramètres valides", result);
    }

    @Test
    public void testValidate_NullSource_ReturnsFalse() {
        boolean result = transferStrategy.validate(null, destAccount, 100.0);

        assertFalse("Devrait retourner false pour une source null", result);
    }

    @Test
    public void testValidate_NullDestination_ReturnsFalse() {
        boolean result = transferStrategy.validate(sourceAccount, null, 100.0);

        assertFalse("Devrait retourner false pour une destination null", result);
    }

    @Test
    public void testGetTransactionType() {
        String type = transferStrategy.getTransactionType();

        assertEquals("Le type doit être TRANSFER", "TRANSFER", type);
    }

    @Test
    public void testMultipleTransfers() throws InsufficientFundsException {
        double initialSourceBalance = sourceAccount.getBalance();
        double initialDestBalance = destAccount.getBalance();

        transferStrategy.execute(sourceAccount, destAccount, 100.0);
        transferStrategy.execute(sourceAccount, destAccount, 150.0);
        transferStrategy.execute(sourceAccount, destAccount, 200.0);

        assertEquals("Le solde source doit refléter tous les transferts",
                initialSourceBalance - 450.0,
                sourceAccount.getBalance(),
                0.01);
        assertEquals("Le solde destination doit refléter tous les transferts",
                initialDestBalance + 450.0,
                destAccount.getBalance(),
                0.01);
    }

    @Test
    public void testTransactionContainsSourceAndDestination()
            throws InsufficientFundsException {
        Transaction transaction = transferStrategy.execute(sourceAccount, destAccount, 100.0);

        assertEquals("La transaction doit contenir la source",
                sourceAccount.getAccountNumber(),
                transaction.getSourceAccount());
        assertEquals("La transaction doit contenir la destination",
                destAccount.getAccountNumber(),
                transaction.getDestinationAccount());
    }

    @Test
    public void testTransferSmallAmount() throws InsufficientFundsException {
        Transaction transaction = transferStrategy.execute(sourceAccount, destAccount, 0.01);

        assertEquals("Devrait pouvoir transférer un très petit montant",
                999.99,
                sourceAccount.getBalance(),
                0.01);
        assertEquals("La destination devrait recevoir le petit montant",
                500.01,
                destAccount.getBalance(),
                0.01);
    }
}