package org.morozov.transferring.core.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.morozov.transferring.core.dao.DaoFactory;
import org.morozov.transferring.core.dao.DataDaoAPI;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OperationServiceImpl implements OperationService {

    /**
     * Better to use CDI. {@see UserService}
     */
    private DataDaoAPI dataDao = DaoFactory.createDataDao();

    @Override
    public void createUser(@NotNull String login) {
        User user = new User();
        user.setLogin(login);
        dataDao.persistUser(user);
    }

    @Nullable
    @Override
    public User loadUserByLogin(@NotNull String login) {
        return dataDao.loadUserByLogin(login);
    }

    @NotNull
    @Override
    public List<User> loadAllUsers() {
        return dataDao.loadAllUsers();
    }

    @Override
    public void createAccount(@NotNull User user, @NotNull String number, @NotNull BigDecimal amount) {
        Account account = new Account();
        account.setNumber(number);
        account.setAmount(amount);
        account.setAccountHolder(user);
        dataDao.persistAccount(account);
    }

    @Nullable
    @Override
    public Account loadAccountByNumber(@NotNull String number) {
        return dataDao.loadAccountByNumber(number);
    }

    @NotNull
    @Override
    public List<Account> loadAllAccounts() {
        return dataDao.loadAllAccounts();
    }

    @Override
    public void processAmountTransfer(
            @NotNull Account fromAccount, @NotNull Account toAccount, @NotNull BigDecimal amount
    ) {
        fromAccount.setAmount(fromAccount.getAmount().subtract(amount));
        toAccount.setAmount(toAccount.getAmount().add(amount));
        TransactionHistoryRecord record = new TransactionHistoryRecord();
        record.setFromAccount(fromAccount);
        record.setToAccount(toAccount);
        record.setAmount(amount);
        record.setTransactionDate(new Date());
        dataDao.commitProcessChanges(fromAccount, toAccount, record);
    }

    @NotNull
    @Override
    public List<TransactionHistoryRecord> loadRecordsByAccount(@NotNull Account account) {
        return dataDao.loadRecordsByAccountId(Collections.singletonList(account.getId()));
    }

    @NotNull
    @Override
    public List<TransactionHistoryRecord> loadRecordsByUser(@NotNull User user) {
        List<UUID> ids = user.getAccounts().stream().map(Account::getId).collect(Collectors.toList());
        return dataDao.loadRecordsByAccountId(ids);
    }
}
