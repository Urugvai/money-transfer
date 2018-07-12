package org.morozov.transferring.rest.services;

import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.services.OperationService;
import org.morozov.transferring.core.services.ServiceFactory;
import org.morozov.transferring.rest.dto.UserDto;
import org.morozov.transferring.rest.responses.BaseResponse;
import org.morozov.transferring.rest.responses.HistoryRecordsResponse;
import org.morozov.transferring.rest.responses.ResponseFactory;
import org.morozov.transferring.rest.utils.ErrorDescriptions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "Users")
@Path("/users")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * Better to use CDI, but #*@/$!@ WELD doesn't work with RESTEasy's servlets in TomCat.
     * So, we have a creation of {@link OperationService} on each creation of UserService.
     */
    private OperationService operationService = ServiceFactory.createOperationService();

    /**
     * Load existed {@link User}.
     *
     * @param login for loading
     * @return response with {@link UserDto} if user is presented,
     * otherwise response with error message
     */
    @GET
    @Path("/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse load(@PathParam("login") String login) {
        User user = operationService.loadUserByLogin(login);
        if (user == null) {
            logger.warn(String.format(ErrorDescriptions.USER_NOT_FOUND_PARAM, login));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.USER_NOT_FOUND
            );
        }
        return ResponseFactory.produceUserResponse(user);
    }

    /**
     * Load existed {@link HistoryRecordsResponse}.
     *
     * @param login for loading
     * @return response with {@link HistoryRecordsResponse} if user is presented and has {@link Account},
     * otherwise response with error message
     */
    @GET
    @Path("/transactions/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse loadTransactions(@PathParam("login") String login) {
        User user = operationService.loadUserByLogin(login);
        if (user == null) {
            logger.warn(String.format(ErrorDescriptions.USER_NOT_FOUND_PARAM, login));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.USER_NOT_FOUND
            );
        }
        if (user.getAccounts().isEmpty()) {
            logger.warn(String.format(ErrorDescriptions.ACCOUNTS_BY_USER_NOT_FOUND_PARAM, login));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.ACCOUNTS_BY_USER_NOT_FOUND
            );
        }
        return ResponseFactory.produceHistoryRecordsResponse(operationService.loadRecordsByUser(user));
    }

    /**
     * Load all existed {@link User}.
     *
     * @return response with list of {@link UserDto}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDto> loadAll() {
        List<User> users = operationService.loadAllUsers();
        return ResponseFactory.mapUsers(users);
    }

    /**
     * Create new {@link User} in system.
     *
     * @param login for creating
     * @return response of result
     */
    @POST
    @Path("/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse create(@PathParam("login") String login) {
        try {
            operationService.createUser(login);
        } catch (IllegalArgumentException ex) {
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ex.getMessage()
            );
        }
        return ResponseFactory.produceOkResponse();
    }
}
