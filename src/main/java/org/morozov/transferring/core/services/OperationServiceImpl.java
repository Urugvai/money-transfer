package org.morozov.transferring.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.morozov.transferring.core.dao.DaoFactory;
import org.morozov.transferring.core.dao.DataDaoAPI;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.utils.PersistenceProvider;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class OperationServiceImpl implements OperationService {

    private static final Logger logger = LogManager.getLogger(OperationServiceImpl.class);

    private static final Lock processLock = new ReentrantLock();
    private static final Lock userLock = new ReentrantLock();
    private static final Lock accountLock = new ReentrantLock();

    /**
     * Better to use CDI. {@see UserService}
     */
    private DataDaoAPI dataDao = DaoFactory.createDataDao();

    @Override
    public void createUser(@NotNull String login) {
        userLock.lock();
        try {
            User user = dataDao.loadUserByLogin(login);

            if (user != null) {
                logger.error(String.format("User with requested login '%s' already exists in system", login));
                throw new IllegalArgumentException("User with requested login already exists in system");
            }

            user = new User();
            user.setLogin(login);
            dataDao.persistUser(user);
        } finally {
            userLock.unlock();
        }
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
        accountLock.lock();
        try {
            Account account = dataDao.loadAccountByNumber(number);
            if (account != null) {
                logger.error(String.format("Account with requested number '%s' already exists in system", number));
                throw new IllegalArgumentException("Account with requested number already exists in system");
            }

            account = new Account();
            account.setNumber(number);
            account.setAmount(amount);
            account.setAccountHolder(user);
            dataDao.persistAccount(account);
        } finally {
            accountLock.unlock();
        }
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
        /*
          This type of synchronization may bring troubles with performance,
          if count of operations and accounts is large (this is bottleneck, using only for simplicity).

          Better to use {select for update} locking on db level on table rows.
          But it brings possible troubles with deadlocks,
          so before locking need to sort locking accounts (e.g. by number)
          to guarantee the same blocking order in different threads.
         */
        processLock.lock();
        try {
            EntityManager em = PersistenceProvider.getEntityManager();

            Account reloadedFromAccount = em.find(Account.class, fromAccount.getId());
            if (reloadedFromAccount == null) {
                logger.error(String.format("Can't load fromAccount '%s'", fromAccount.getNumber()));
                throw new IllegalStateException("Can't load fromAccount");
            }

            if (reloadedFromAccount.getAmount().compareTo(amount) < 0) {
                logger.error(
                        String.format(
                                "Not enough amount of account to process operation. Account amount: '%s'. Required: '%s'",
                                fromAccount.getAmount(), amount
                        )
                );
                throw new IllegalStateException("Not enough amount of account to process operation");
            }

            Account reloadedToAccount = em.find(Account.class, toAccount.getId());
            if (reloadedToAccount == null) {
                logger.error(String.format("Can't load toAccount '%s'", toAccount.getNumber()));
                throw new IllegalStateException("Can't load toAccount");
            }
            reloadedFromAccount.setAmount(reloadedFromAccount.getAmount().subtract(amount));
            reloadedToAccount.setAmount(reloadedToAccount.getAmount().add(amount));
            TransactionHistoryRecord record = new TransactionHistoryRecord();
            record.setFromAccount(reloadedFromAccount);
            record.setToAccount(reloadedToAccount);
            record.setAmount(amount);
            record.setTransactionDate(new Date());
            dataDao.commitProcessChanges(reloadedFromAccount, reloadedToAccount, record);
        } finally {
            processLock.unlock();
        }
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
