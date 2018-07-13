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
import org.morozov.transferring.rest.requests.ProcessRequest;
import org.morozov.transferring.rest.services.AmountTransferService;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class AmountTransferServiceTest extends BaseTest {

    private static final String BASE_TRANSFER_PATH = "/process/";

    private static final String EXPECTED_SAME_ACCOUNTS_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Attempt to transfer amount from account to the same account is forbidden\"}";

    private static final String EXPECTED_AMOUNT_NOT_ENOUGH_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Not enough amount of account to process operation\"}";

    private static final String EXPECTED_TO_ACCOUNT_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"To account with requested number is not found\"}";

    private static final String EXPECTED_FROM_ACCOUNT_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"From account with requested number is not found\"}";

    @BeforeClass
    public static void setupUsersTest() {
        setup();
        dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(AmountTransferService.class));
    }

    @Test
    public void processTest() throws URISyntaxException, JsonProcessingException {
        MockHttpRequest request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, EXISTED_ACCOUNT_NUMBER, BigDecimal.valueOf(-1)).getBytes());

        MockHttpResponse response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_INVALID_DATA_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, EXISTED_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_SAME_ACCOUNTS_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(ABSENT_ACCOUNT_NUMBER, ABSENT_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_SAME_ACCOUNTS_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(ABSENT_ACCOUNT_NUMBER, EXISTED_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_FROM_ACCOUNT_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, EXISTED_SECOND_ACCOUNT_NUMBER, BigDecimal.valueOf(200)).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_AMOUNT_NOT_ENOUGH_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, ABSENT_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_TO_ACCOUNT_RESPONSE, response.getContentAsString());

        request = MockHttpRequest.put(BASE_TRANSFER_PATH);
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON_TYPE);
        request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, EXISTED_SECOND_ACCOUNT_NUMBER, BigDecimal.TEN).getBytes());

        response =
                invoke(request, new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_OK_RESPONSE, response.getContentAsString());

        Account account = loadAccountByLogin(EXISTED_ACCOUNT_NUMBER);
        Assert.assertNotNull(account);
        Assert.assertEquals(EXISTED_ACCOUNT_NUMBER, account.getNumber());
        Assert.assertEquals(0, BigDecimal.valueOf(90).compareTo(account.getAmount()));

        account = loadAccountByLogin(EXISTED_SECOND_ACCOUNT_NUMBER);
        Assert.assertNotNull(account);
        Assert.assertEquals(EXISTED_SECOND_ACCOUNT_NUMBER, account.getNumber());
        Assert.assertEquals(0, BigDecimal.valueOf(110).compareTo(account.getAmount()));
    }

    @Test
    public void loadTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        List<Future<MockHttpResponse>> futures = executor.invokeAll(
                Arrays.asList(
                        produceProcessThread(BigDecimal.ONE),
                        produceProcessThread(BigDecimal.valueOf(5)),
                        produceProcessThread(BigDecimal.TEN),
                        produceProcessThread(BigDecimal.valueOf(50))
                )
        );

        futures.forEach(future -> {
            MockHttpResponse response;
            try {
                response = future.get();

                Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                Assert.assertEquals(EXPECTED_OK_RESPONSE, response.getContentAsString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();

        Account account = loadAccountByLogin(EXISTED_ACCOUNT_NUMBER);
        Assert.assertNotNull(account);
        Assert.assertEquals(EXISTED_ACCOUNT_NUMBER, account.getNumber());
        Assert.assertEquals(0, BigDecimal.valueOf(34).compareTo(account.getAmount()));

        account = loadAccountByLogin(EXISTED_SECOND_ACCOUNT_NUMBER);
        Assert.assertNotNull(account);
        Assert.assertEquals(EXISTED_SECOND_ACCOUNT_NUMBER, account.getNumber());
        Assert.assertEquals(0, BigDecimal.valueOf(166).compareTo(account.getAmount()));
    }

    private String produceCreatingBody(
            @NotNull String fromAccount, @NotNull String toAccount, @NotNull BigDecimal amount
    ) throws JsonProcessingException {
        ProcessRequest request = new ProcessRequest();
        request.setFromAccount(fromAccount);
        request.setToAccount(toAccount);
        request.setAmount(amount);
        return mapper.writeValueAsString(request);
    }

    private Callable<MockHttpResponse> produceProcessThread(BigDecimal amount) {
        return () -> {
            MockHttpRequest request = MockHttpRequest.put(BASE_TRANSFER_PATH);
            request.accept(MediaType.APPLICATION_JSON);
            request.contentType(MediaType.APPLICATION_JSON_TYPE);
            request.content(produceCreatingBody(EXISTED_ACCOUNT_NUMBER, EXISTED_SECOND_ACCOUNT_NUMBER, amount).getBytes());

            return invoke(request, new MockHttpResponse());
        };
    }
}
