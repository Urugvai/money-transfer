package org.morozov.transferring.rest.services;

import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.core.services.OperationService;
import org.morozov.transferring.core.services.ServiceFactory;
import org.morozov.transferring.rest.dto.AccountDto;
import org.morozov.transferring.rest.requests.AccountCreatingRequest;
import org.morozov.transferring.rest.responses.BaseResponse;
import org.morozov.transferring.rest.responses.HistoryRecordsResponse;
import org.morozov.transferring.rest.responses.ResponseFactory;
import org.morozov.transferring.rest.utils.ErrorDescriptions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Api(value = "Accounts")
@Path("/accounts")
public class AccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);

    /**
     * Better to use CDI. {@see UserService}
     */
    private OperationService operationService = ServiceFactory.createOperationService();

    /**
     * Load existed {@link Account}.
     *
     * @param number for loading
     * @return response with {@link AccountDto} if account is presented,
     * otherwise response with error message
     */
    @GET
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse load(@PathParam("number") String number) {
        Account account = operationService.loadAccountByNumber(number);
        if (account == null) {
            logger.warn(String.format(ErrorDescriptions.ACCOUNT_NOT_FOUND_PARAM, number));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.ACCOUNT_NOT_FOUND
            );
        }
        return ResponseFactory.produceAccountResponse(account);
    }

    /**
     * Load existed {@link HistoryRecordsResponse}.
     *
     * @param number for loading
     * @return response with {@link HistoryRecordsResponse} if account is presented,
     * otherwise response with error message
     */
    @GET
    @Path("/transactions/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse loadTransactions(@PathParam("number") String number) {
        Account account = operationService.loadAccountByNumber(number);
        if (account == null) {
            logger.warn(String.format(ErrorDescriptions.ACCOUNT_NOT_FOUND_PARAM, number));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.ACCOUNT_NOT_FOUND
            );
        }
        return ResponseFactory.produceHistoryRecordsResponse(operationService.loadRecordsByAccount(account));
    }

    /**
     * Load all existed {@link Account}.
     *
     * @return response with list of {@link AccountDto}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccountDto> loadAll() {
        List<Account> accounts = operationService.loadAllAccounts();
        return ResponseFactory.mapAccounts(accounts);
    }

    /**
     * Create new {@link Account} in system.
     *
     * @param request for creating
     * @return response of result
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BaseResponse create(AccountCreatingRequest request) {
        if (!isValidAccountCreatingRequest(request)) {
            logger.warn(
                    String.format(
                            ErrorDescriptions.ACCOUNT_CREATING_INVALID_DATA_PARAM,
                            request.getUserLogin(), request.getNumber(), request.getAmount()
                    )
            );
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.REQUEST_INVALID_DATA
            );
        }

        User user = operationService.loadUserByLogin(request.getUserLogin());
        if (user == null) {
            logger.warn(String.format(ErrorDescriptions.USER_NOT_FOUND_PARAM, request.getUserLogin()));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.USER_NOT_FOUND
            );
        }

        Account account = operationService.loadAccountByNumber(request.getNumber());
        if (account != null) {
            logger.warn(String.format(ErrorDescriptions.ACCOUNT_ALREADY_EXISTS_PARAM, request.getNumber()));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.ACCOUNT_ALREADY_EXISTS
            );
        }
        operationService.createAccount(user, request.getNumber(), request.getAmount());
        return ResponseFactory.produceOkResponse();
    }

    private boolean isValidAccountCreatingRequest(AccountCreatingRequest request) {
        return request.getUserLogin() != null
                && request.getNumber() != null
                && (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
