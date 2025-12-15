package com.exemple.pattern.factory;


import com.exemple.model.Account;
import com.exemple.model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour AccountFactory
 */
public class AccountFactoryTest {

    private User testUser;

    @Before
    public void setUp() {
        AccountFactory.resetCounter();
        testUser = UserFactory.createStandardUser("testuser", "password123", "test@email.com");
    }

    @Test
    public void testCreateCheckingAccount() {
        Account account = AccountFactory.createCheckingAccount(testUser, 1000.0);

        assertNotNull("Le compte ne doit pas être null", account);
        assertEquals("Le type doit être CHECKING", "CHECKING", account.getAccountType());
        assertEquals("Le solde doit correspondre", 1000.0, account.getBalance(), 0.01);
        assertTrue("Le numéro doit commencer par CHK",
                account.getAccountNumber().startsWith("CHK"));
    }

    @Test
    public void testCreateSavingsAccount() {
        Account account = AccountFactory.createSavingsAccount(testUser, 500.0);

        assertNotNull("Le compte ne doit pas être null", account);
        assertEquals("Le type doit être SAVINGS", "SAVINGS", account.getAccountType());
        assertEquals("Le solde doit correspondre", 500.0, account.getBalance(), 0.01);
        assertTrue("Le numéro doit commencer par SAV",
                account.getAccountNumber().startsWith("SAV"));
    }

    @Test
    public void testCreateBusinessAccount() {
        Account account = AccountFactory.createBusinessAccount(testUser, 2000.0);

        assertNotNull("Le compte ne doit pas être null", account);
        assertEquals("Le type doit être BUSINESS", "BUSINESS", account.getAccountType());
        assertEquals("Le solde doit correspondre", 2000.0, account.getBalance(), 0.01);
        assertTrue("Le numéro doit commencer par BUS",
                account.getAccountNumber().startsWith("BUS"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBusinessAccount_InsufficientInitialDeposit_ThrowsException() {
        AccountFactory.createBusinessAccount(testUser, 500.0);
    }

    @Test
    public void testCreateAccount_WithTypeString() {
        Account checkingAccount = AccountFactory.createAccount(testUser, 1000.0, "CHECKING");
        Account savingsAccount = AccountFactory.createAccount(testUser, 500.0, "SAVINGS");
        Account businessAccount = AccountFactory.createAccount(testUser, 2000.0, "BUSINESS");

        assertEquals("CHECKING", checkingAccount.getAccountType());
        assertEquals("SAVINGS", savingsAccount.getAccountType());
        assertEquals("BUSINESS", businessAccount.getAccountType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccount_InvalidType_ThrowsException() {
        AccountFactory.createAccount(testUser, 1000.0, "INVALID_TYPE");
    }

    @Test
    public void testAccountNumbers_AreUnique() {
        Account account1 = AccountFactory.createCheckingAccount(testUser, 1000.0);
        Account account2 = AccountFactory.createCheckingAccount(testUser, 1000.0);
        Account account3 = AccountFactory.createCheckingAccount(testUser, 1000.0);

        assertNotEquals("Les numéros de compte doivent être uniques",
                account1.getAccountNumber(),
                account2.getAccountNumber());
        assertNotEquals("Les numéros de compte doivent être uniques",
                account2.getAccountNumber(),
                account3.getAccountNumber());
        assertNotEquals("Les numéros de compte doivent être uniques",
                account1.getAccountNumber(),
                account3.getAccountNumber());
    }

    @Test
    public void testAccountNumbers_AreSequential() {
        Account account1 = AccountFactory.createCheckingAccount(testUser, 1000.0);
        Account account2 = AccountFactory.createCheckingAccount(testUser, 1000.0);

        String number1 = account1.getAccountNumber();
        String number2 = account2.getAccountNumber();

        int seq1 = Integer.parseInt(number1.split("-")[1]);
        int seq2 = Integer.parseInt(number2.split("-")[1]);

        assertEquals("Les numéros doivent être séquentiels", seq1 + 1, seq2);
    }

    @Test
    public void testCreateAccount_OwnerIdIsSet() {
        Account account = AccountFactory.createCheckingAccount(testUser, 1000.0);

        assertEquals("L'ID du propriétaire doit correspondre",
                testUser.getUserId(),
                account.getOwnerId());
    }

    @Test
    public void testCreateAccount_IsActive() {
        Account account = AccountFactory.createCheckingAccount(testUser, 1000.0);

        assertTrue("Le compte doit être actif par défaut", account.isActive());
    }

    @Test
    public void testCreateAccount_WithZeroBalance() {
        Account account = AccountFactory.createCheckingAccount(testUser, 0.0);

        assertEquals("Le solde doit être 0", 0.0, account.getBalance(), 0.01);
    }

    @Test
    public void testCreateAccount_CaseInsensitiveType() {
        Account account1 = AccountFactory.createAccount(testUser, 1000.0, "checking");
        Account account2 = AccountFactory.createAccount(testUser, 1000.0, "CHECKING");
        Account account3 = AccountFactory.createAccount(testUser, 1000.0, "ChEcKiNg");

        assertEquals("CHECKING", account1.getAccountType());
        assertEquals("CHECKING", account2.getAccountType());
        assertEquals("CHECKING", account3.getAccountType());
    }
}