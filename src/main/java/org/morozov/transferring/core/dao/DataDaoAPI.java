package org.morozov.transferring.core.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;

import java.util.List;
import java.util.UUID;

public interface DataDaoAPI {

    /**
     * Persist {@link User}
     *
     * @param user for persisting
     */
    void persistUser(@NotNull User user);

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
     * Persist {@link Account}
     *
     * @param account for persisting
     */
    void persistAccount(@NotNull Account account);

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
     * Commit changes of amount transfer processing.
     *
     * @param fromAccount
     * @param toAccount
     * @param record
     */
    void commitProcessChanges(
            @NotNull Account fromAccount, @NotNull Account toAccount, @NotNull TransactionHistoryRecord record
    );

    /**
     * Load all {@link TransactionHistoryRecord} (from and to) by account ids
     *
     * @param accountIds for loading
     * @return all {@link TransactionHistoryRecord}
     */
    @NotNull
    List<TransactionHistoryRecord> loadRecordsByAccountId(@NotNull List<UUID> accountIds);

}
