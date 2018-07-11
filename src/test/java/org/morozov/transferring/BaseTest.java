package org.morozov.transferring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.utils.PersistenceProvider;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class BaseTest {

    protected static final String EXPECTED_OK_RESPONSE =
            "{\"code\":200,\"errorMessage\":null}";

    protected static final String EXPECTED_USER_NOT_FOUND_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"User with requested login is not found\"}";

    protected static final String EXPECTED_RECORDS_RESPONSE =
            "{\"code\":200,\"errorMessage\":null," +
                    "\"records\":[{\"fromAccount\":\"accountNumber\",\"toAccount\":\"accountNumber2\",\"amount\":10.00,";

    protected static final String EXPECTED_INVALID_DATA_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Invalid requested data\"}";


    protected static String EXISTED_USER_LOGIN = "testUser";
    protected static String EXISTED_ACCOUNT_NUMBER = "accountNumber";
    protected static String EXISTED_SECOND_ACCOUNT_NUMBER = "accountNumber2";
    protected static String EXISTED_USER_LOGIN_WITHOUT_ACCOUNT = "testUserWithoutAccounts";
    protected static String ABSENT_USER_LOGIN = "testUser2";
    protected static String ABSENT_ACCOUNT_NUMBER = "accountNumber3";

    protected static Dispatcher dispatcher;
    protected static ObjectMapper mapper;

    protected static void setup() {
        PersistenceProvider.init("amount_transfer_test");
        dispatcher = MockDispatcherFactory.createDispatcher();
        mapper = new ObjectMapper();
    }

    @Before
    public void before() {
        setupInitData();
    }

    @After
    public void after() {
        PersistenceProvider.runInTransaction(em -> {
            em.createNativeQuery("DELETE FROM trans_history_record").executeUpdate();
            em.createNativeQuery("DELETE FROM trans_account").executeUpdate();
            em.createNativeQuery("DELETE FROM trans_user").executeUpdate();
        });
    }

    protected MockHttpResponse invoke(MockHttpRequest request, MockHttpResponse response) {
        dispatcher.invoke(request, response);
        return response;
    }

    @Nullable
    protected Account loadAccountByLogin(@NotNull String number) {
        TypedQuery<Account> query = PersistenceProvider.getEntityManager().createQuery(
                "select a from trans$Account a where a.number = :number", Account.class
        )
                .setParameter("number", number);
        List<Account> accounts = query.getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    private static void setupInitData() {
        User userWithoutAccounts = new User();
        userWithoutAccounts.setLogin(EXISTED_USER_LOGIN_WITHOUT_ACCOUNT);

        User user = new User();
        user.setLogin(EXISTED_USER_LOGIN);

        Account account = new Account();
        account.setAccountHolder(user);
        account.setAmount(BigDecimal.valueOf(100));
        account.setNumber("accountNumber");

        Account account2 = new Account();
        account2.setAccountHolder(user);
        account2.setAmount(BigDecimal.valueOf(100));
        account2.setNumber("accountNumber2");

        TransactionHistoryRecord record = new TransactionHistoryRecord();
        record.setFromAccount(account);
        record.setToAccount(account2);
        record.setAmount(BigDecimal.valueOf(10));
        record.setTransactionDate(new Date());

        PersistenceProvider.runInTransaction(em -> {
            em.persist(userWithoutAccounts);
            em.persist(user);
            em.persist(account);
            em.persist(account2);
            em.persist(record);
        });
    }
}
