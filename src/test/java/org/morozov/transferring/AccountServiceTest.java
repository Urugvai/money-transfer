package org.morozov.transferring;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.rest.requests.AccountCreatingRequest;
import org.morozov.transferring.rest.services.AccountService;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URISyntaxException;

public class AccountServiceTest extends BaseTest {

    private static final String BASE_ACCOUNTS_PATH = "/accounts/";

    private static final String EXPECTED_ACCOUNTS_RESPONSE =
            "[{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber\"}," +
                    "{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber2\"}]";

    private static final String EXPECTED_ACCOUNT_NOT_FOUND_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Account with requested number is not found\"}";

    private static final String EXPECTED_ACCOUNT_RESPONSE =
            "{\"code\":200,\"errorMessage\":null," +
                    "\"account\":{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber\"}}";

    private static final String EXPECTED_ACCOUNT_EXISTS_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Account with requested number already exists in system\"}";

    @BeforeClass
    public static void setupAccountsTest() {
        setup();
        dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(AccountService.class));
    }

    @Test
    public void getAllTest() throws URISyntaxException {
        MockHttpResponse response = invoke(MockHttpRequest.get(BASE_ACCOUNTS_PATH), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_ACCOUNTS_RESPONSE, response.getContentAsString());
    }

    @Test
    public void getByNumberTest() throws URISyntaxException {
        MockHttpResponse response =
                invoke(MockHttpRequest.get(BASE_ACCOUNTS_PATH + ABSENT_ACCOUNT_NUMBER), new MockHttpResponse());

        Assert.assertEquals(EXPECTED_ACCOUNT_NOT_FOUND_RESPONSE, response.getContentAsString());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response =
                invoke(MockHttpRequest.get(BASE_ACCOUNTS_PATH + EXISTED_ACCOUNT_NUMBER), new MockHttpResponse());

        Assert.assertEquals(EXPECTED_ACCOUNT_RESPONSE, response.getContentAsString());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void createByNumberTest() throws URISyntaxException, JsonProcessingException {
        String numberForCreating = "testNumberForCreating";
        MockHttpRequest request = MockHttpRequest.post(BASE_ACCOUNTS_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(ABSENT_USER_LOGIN, EXISTED_ACCOUNT_NUMBER, BigDecimal.valueOf(-1)).getBytes());

        MockHttpResponse response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_INVALID_DATA_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.post(BASE_ACCOUNTS_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(ABSENT_USER_LOGIN, EXISTED_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_NOT_FOUND_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.post(BASE_ACCOUNTS_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_USER_LOGIN, EXISTED_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_ACCOUNT_EXISTS_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.post(BASE_ACCOUNTS_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_USER_LOGIN, numberForCreating, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_OK_RESPONSE, response.getContentAsString());

        Account account = loadAccountByLogin(numberForCreating);

        Assert.assertNotNull(account);
        Assert.assertEquals(numberForCreating, account.getNumber());
        Assert.assertEquals(EXISTED_USER_LOGIN, account.getAccountHolder().getLogin());
        Assert.assertEquals(0, BigDecimal.TEN.compareTo(account.getAmount()));
    }

    @Test
    public void getAllTransactionsTest() throws URISyntaxException {
        MockHttpResponse response =
                invoke(MockHttpRequest.get(
                        BASE_ACCOUNTS_PATH + "transactions/" + ABSENT_ACCOUNT_NUMBER), new MockHttpResponse()
                );

        Assert.assertEquals(EXPECTED_ACCOUNT_NOT_FOUND_RESPONSE, response.getContentAsString());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response =
                invoke(MockHttpRequest.get(
                        BASE_ACCOUNTS_PATH + "transactions/" + EXISTED_ACCOUNT_NUMBER), new MockHttpResponse()
                );

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(EXPECTED_RECORDS_RESPONSE));
    }

    private String produceCreatingBody(
            @NotNull String userLogin, @NotNull String accountNumber, @NotNull BigDecimal amount
    ) throws JsonProcessingException {
        AccountCreatingRequest request = new AccountCreatingRequest();
        request.setUserLogin(userLogin);
        request.setNumber(accountNumber);
        request.setAmount(amount);
        return mapper.writeValueAsString(request);
    }
}
