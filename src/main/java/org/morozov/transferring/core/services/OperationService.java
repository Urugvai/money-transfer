package org.morozov.transferring.core.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Main processing interface. Better to use several smaller interfaces, but for simplicity use just one.
 */
public interface OperationService {

    /**
     * Create new {@link User}
     *
     * @param login for creating
     */
    void createUser(@NotNull String login);

    /**
     * Load {@link User} by login
     *
     * @param login for loading
     * @return {@link User} or null if user with inputted login is absent
     */
    @Nullable
    User loadUserByLogin(@NotNull String login);

    /**
     * Load users in system
     *
     * @return all users
     */
    @NotNull
    List<User> loadAllUsers();

    /**
     * Create new {@link Account}
     *
     * @param number for creating
     */
    void createAccount(@NotNull User user, @NotNull String number, @NotNull BigDecimal amount);

    /**
     * Load {@link Account} by number
     *
     * @param number for loading
     * @return {@link Account} or null if account with inputted number is absent
     */
    @Nullable
    Account loadAccountByNumber(@NotNull String number);

    /**
     * Load accounts in system
     *
     * @return all accounts
     */
    @NotNull
    List<Account> loadAllAccounts();

    /**
     * Process amount of transfer between accounts
     *
     * @param fromAccount
     * @param toAccount
     * @param amount      for transfer
     */
    void processAmountTransfer(@NotNull Account fromAccount, @NotNull Account toAccount, @NotNull BigDecimal amount);

    /**
     * Load all {@link TransactionHistoryRecord} (from and to) by account
     *
     * @param account for loading
     * @return all {@link TransactionHistoryRecord}
     */
    @NotNull
    List<TransactionHistoryRecord> loadRecordsByAccount(@NotNull Account account);

    /**
     * Load all {@link TransactionHistoryRecord} (from and to) by user
     *
     * @param user for loading
     * @return all {@link TransactionHistoryRecord}
     */
    @NotNull
    List<TransactionHistoryRecord> loadRecordsByUser(@NotNull User user);
}
