package com.exemple.service;


import com.exemple.model.Account;
import com.exemple.model.Transaction;
import com.exemple.model.User;
import com.exemple.pattern.factory.AccountFactory;
import com.exemple.pattern.factory.UserFactory;
import com.exemple.pattern.observer.TransactionObserver;
import com.exemple.pattern.strategy.DepositStrategy;
import com.exemple.pattern.strategy.WithdrawStrategy;
import com.exemple.pattern.strategy.TransferStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour TransactionService
 */
public class TransactionServiceTest {

    private TransactionService transactionService;
    private User testUser;
    private Account sourceAccount;
    private Account destAccount;

    @Before
    public void setUp() {
        transactionService = new TransactionService();
        testUser = UserFactory.createStandardUser("testuser", "password123", "test@email.com");
        sourceAccount = AccountFactory.createCheckingAccount(testUser, 1000.0);
        destAccount = AccountFactory.createCheckingAccount(testUser, 500.0);
    }

    @Test
    public void testExecuteTransaction_Deposit_Success() {
        DepositStrategy strategy = new DepositStrategy();
        double amount = 300.0;

        Transaction transaction = transactionService.executeTransaction(
                strategy, null, destAccount, amount
        );

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être DEPOSIT", "DEPOSIT", transaction.getType());
        assertEquals("Le montant doit correspondre", amount, transaction.getAmount(), 0.01);
    }

    @Test
    public void testExecuteTransaction_Withdraw_Success() {
        WithdrawStrategy strategy = new WithdrawStrategy();
        double amount = 200.0;

        Transaction transaction = transactionService.executeTransaction(
                strategy, sourceAccount, null, amount
        );

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être WITHDRAW", "WITHDRAW", transaction.getType());
        assertEquals("Le montant doit correspondre", amount, transaction.getAmount(), 0.01);
    }

    @Test
    public void testExecuteTransaction_Transfer_Success() {
        TransferStrategy strategy = new TransferStrategy();
        double amount = 250.0;

        Transaction transaction = transactionService.executeTransaction(
                strategy, sourceAccount, destAccount, amount
        );

        assertNotNull("La transaction ne doit pas être null", transaction);
        assertEquals("Le type doit être TRANSFER", "TRANSFER", transaction.getType());
        assertEquals("Le montant doit correspondre", amount, transaction.getAmount(), 0.01);
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteTransaction_InsufficientFunds_ThrowsException() {
        WithdrawStrategy strategy = new WithdrawStrategy();
        double amount = 2000.0;

        transactionService.executeTransaction(strategy, sourceAccount, null, amount);
    }

    @Test
    public void testAddObserver() {
        TransactionObserver mockObserver = Mockito.mock(TransactionObserver.class);
        when(mockObserver.getObserverName()).thenReturn("MockObserver");

        transactionService.addObserver(mockObserver);
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );

        verify(mockObserver, times(1)).onTransactionExecuted(any(Transaction.class));
    }

    @Test
    public void testRemoveObserver() {
        TransactionObserver mockObserver = Mockito.mock(TransactionObserver.class);
        when(mockObserver.getObserverName()).thenReturn("MockObserver");
        transactionService.addObserver(mockObserver);

        transactionService.removeObserver(mockObserver);
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );

        verify(mockObserver, never()).onTransactionExecuted(any(Transaction.class));
    }

    @Test
    public void testObserverNotifiedOnFailure() {
        TransactionObserver mockObserver = Mockito.mock(TransactionObserver.class);
        when(mockObserver.getObserverName()).thenReturn("MockObserver");
        transactionService.addObserver(mockObserver);

        try {
            transactionService.executeTransaction(
                    new WithdrawStrategy(), sourceAccount, null, 2000.0
            );
        } catch (RuntimeException e) {
            // Exception attendue
        }

        verify(mockObserver, times(1))
                .onTransactionFailed(any(), anyString());
    }

    @Test
    public void testGetTransaction() {
        Transaction transaction = transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );

        Transaction retrieved = transactionService.getTransaction(transaction.getTransactionId());

        assertNotNull("La transaction doit être retrouvée", retrieved);
        assertEquals("Les IDs doivent correspondre",
                transaction.getTransactionId(),
                retrieved.getTransactionId());
    }

    @Test
    public void testGetAccountTransactions() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 200.0
        );
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 300.0
        );

        List<Transaction> transactions = transactionService.getAccountTransactions(
                destAccount.getAccountNumber()
        );

        assertEquals("Devrait y avoir 3 transactions", 3, transactions.size());
    }

    @Test
    public void testGetTransactionsByType() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new WithdrawStrategy(), sourceAccount, null, 50.0
        );
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 200.0
        );

        List<Transaction> deposits = transactionService.getTransactionsByType("DEPOSIT");
        List<Transaction> withdraws = transactionService.getTransactionsByType("WITHDRAW");

        assertEquals("Devrait y avoir 2 dépôts", 2, deposits.size());
        assertEquals("Devrait y avoir 1 retrait", 1, withdraws.size());
    }

    @Test
    public void testGetTotalTransactionAmount() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 200.0
        );
        transactionService.executeTransaction(
                new WithdrawStrategy(), destAccount, null, 50.0
        );

        double total = transactionService.getTotalTransactionAmount(
                destAccount.getAccountNumber()
        );

        assertEquals("Le total doit être +100 +200 -50 = 250", 250.0, total, 0.01);
    }

    @Test
    public void testGetTotalTransactionCount() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new WithdrawStrategy(), sourceAccount, null, 50.0
        );

        int count = transactionService.getTotalTransactionCount();

        assertEquals("Devrait y avoir 2 transactions", 2, count);
    }

    @Test
    public void testGetSuccessfulTransactionCount() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new WithdrawStrategy(), sourceAccount, null, 50.0
        );

        try {
            transactionService.executeTransaction(
                    new WithdrawStrategy(), sourceAccount, null, 5000.0
            );
        } catch (RuntimeException e) {
            // Exception attendue
        }

        int count = transactionService.getSuccessfulTransactionCount();

        assertEquals("Devrait y avoir 2 transactions réussies", 2, count);
    }

    @Test
    public void testClearHistory() {
        transactionService.executeTransaction(
                new DepositStrategy(), null, destAccount, 100.0
        );
        transactionService.executeTransaction(
                new WithdrawStrategy(), sourceAccount, null, 50.0
        );

        transactionService.clearHistory();

        assertEquals("L'historique devrait être vide", 0,
                transactionService.getTotalTransactionCount());
    }
}