package org.morozov.transferring;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.utils.PersistenceProvider;
import org.morozov.transferring.rest.services.UserService;

import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;

public class UserServiceTest extends BaseTest {

    private static final String BASE_USERS_PATH = "/users/";

    private static final String EXPECTED_USERS_RESPONSE =
            "[{\"login\":\"testUserWithoutAccounts\",\"accounts\":[]}," +
                    "{\"login\":\"testUser\",\"accounts\":[" +
                    "{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber\"}," +
                    "{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber2\"}]}]";

    private static final String EXPECTED_USER_HAS_NO_ACCOUNTS_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"Accounts for user with requested login are not found\"}";

    private static final String EXPECTED_USER_RESPONSE =
            "{\"code\":200,\"errorMessage\":null," +
                    "\"user\":{\"login\":\"testUser\",\"accounts\":[" +
                    "{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber\"}," +
                    "{\"accountHolder\":\"testUser\",\"amount\":100.00,\"number\":\"accountNumber2\"}]}}";

    private static final String EXPECTED_USER_EXISTS_RESPONSE =
            "{\"code\":400,\"errorMessage\":\"User with requested login already exists in system\"}";


    @BeforeClass
    public static void setupUsersTest() {
        setup();
        dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(UserService.class));
    }

    @Test
    public void getAllTest() throws URISyntaxException {
        MockHttpResponse response = invoke(MockHttpRequest.get(BASE_USERS_PATH), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USERS_RESPONSE, response.getContentAsString());
    }

    @Test
    public void getByLoginTest() throws URISyntaxException {
        MockHttpResponse response =
                invoke(MockHttpRequest.get(BASE_USERS_PATH + ABSENT_USER_LOGIN), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_NOT_FOUND_RESPONSE, response.getContentAsString());

        response =
                invoke(MockHttpRequest.get(BASE_USERS_PATH + EXISTED_USER_LOGIN), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_RESPONSE, response.getContentAsString());
    }

    @Test
    public void createByLoginTest() throws URISyntaxException {
        String loginForCreating = "testUser3";
        MockHttpResponse response =
                invoke(MockHttpRequest.post(BASE_USERS_PATH + EXISTED_USER_LOGIN), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_EXISTS_RESPONSE, response.getContentAsString());

        response =
                invoke(MockHttpRequest.post(BASE_USERS_PATH + loginForCreating), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_OK_RESPONSE, response.getContentAsString());

        User user = loadUserByLogin(loginForCreating);

        Assert.assertNotNull(user);
        Assert.assertEquals(loginForCreating, user.getLogin());
    }

    @Test
    public void getAllTransactionsTest() throws URISyntaxException {
        MockHttpResponse response =
                invoke(MockHttpRequest.get(BASE_USERS_PATH + "transactions/" + ABSENT_USER_LOGIN), new MockHttpResponse());

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_NOT_FOUND_RESPONSE, response.getContentAsString());

        response =
                invoke(
                        MockHttpRequest.get(BASE_USERS_PATH + "transactions/" + EXISTED_USER_LOGIN_WITHOUT_ACCOUNT),
                        new MockHttpResponse()
                );

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(EXPECTED_USER_HAS_NO_ACCOUNTS_RESPONSE, response.getContentAsString());

        response =
                invoke(
                        MockHttpRequest.get(BASE_USERS_PATH + "transactions/" + EXISTED_USER_LOGIN),
                        new MockHttpResponse()
                );

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.getContentAsString().contains(EXPECTED_RECORDS_RESPONSE));
    }

    @Nullable
    private User loadUserByLogin(@NotNull String login) {
        TypedQuery<User> query = PersistenceProvider.getEntityManager().createQuery(
                "select u from trans$User u where u.login = :login", User.class
        )
                .setParameter("login", login);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
}
