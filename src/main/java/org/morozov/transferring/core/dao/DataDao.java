package org.morozov.transferring.core.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.utils.PersistenceProvider;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class DataDao implements DataDaoAPI {

    @Override
    public void persistUser(@NotNull User user) {
        PersistenceProvider.runInTransaction(em -> em.persist(user));
    }

    @Nullable
    @Override
    public User loadUserByLogin(@NotNull String login) {
        TypedQuery<User> query = PersistenceProvider.getEntityManager().createQuery(
                "select u from trans$User u where u.login = :login", User.class
        )
                .setParameter("login", login)
                .setMaxResults(1);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @NotNull
    @Override
    public List<User> loadAllUsers() {
        TypedQuery<User> query = PersistenceProvider.getEntityManager().createQuery(
                "select u from trans$User u", User.class
        );
        return query.getResultList();
    }

    @Override
    public void persistAccount(@NotNull Account account) {
        PersistenceProvider.runInTransaction(em -> em.persist(account));
    }

    @Nullable
    @Override
    public Account loadAccountByNumber(@NotNull String number) {
        TypedQuery<Account> query = PersistenceProvider.getEntityManager().createQuery(
                "select a from trans$Account a where a.number = :number", Account.class
        )
                .setParameter("number", number)
                .setMaxResults(1);
        List<Account> accounts = query.getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @NotNull
    @Override
    public List<Account> loadAllAccounts() {
        TypedQuery<Account> query = PersistenceProvider.getEntityManager().createQuery(
                "select a from trans$Account a", Account.class
        );
        return query.getResultList();
    }

    @Override
    public void commitProcessChanges(
            @NotNull Account fromAccount, @NotNull Account toAccount, @NotNull TransactionHistoryRecord record
    ) {
        PersistenceProvider.runInTransaction(em -> {
            em.merge(fromAccount);
            em.merge(toAccount);
            em.persist(record);
        });
    }

    @NotNull
    @Override
    public List<TransactionHistoryRecord> loadRecordsByAccountId(@NotNull List<UUID> accountIds) {
        TypedQuery<TransactionHistoryRecord> query = PersistenceProvider.getEntityManager().createQuery(
                "select r from trans$HistoryRecord r " +
                        "where r.fromAccount.id in (:ids) or r.toAccount.id in (:ids) " +
                        "order by r.transactionDate",
                TransactionHistoryRecord.class
        )
                .setParameter("ids", accountIds);
        return query.getResultList();
    }
}
