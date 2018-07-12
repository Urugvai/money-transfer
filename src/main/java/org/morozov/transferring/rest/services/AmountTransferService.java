package org.morozov.transferring.rest.services;

import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.services.OperationService;
import org.morozov.transferring.core.services.ServiceFactory;
import org.morozov.transferring.rest.requests.ProcessRequest;
import org.morozov.transferring.rest.responses.BaseResponse;
import org.morozov.transferring.rest.responses.ResponseFactory;
import org.morozov.transferring.rest.utils.ErrorDescriptions;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Api(value = "Process")
@Path("/process")
public class AmountTransferService {

    private static final Logger logger = LogManager.getLogger(AmountTransferService.class);

    /**
     * Better to use CDI. {@see UserService}
     */
    private OperationService operationService = ServiceFactory.createOperationService();

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BaseResponse create(ProcessRequest request) {
        if (!isValidProcessRequest(request)) {
            logger.warn(
                    String.format(
                            ErrorDescriptions.PROCESS_REQUEST_INVALID_DATA_PARAM,
                            request.getFromAccount(), request.getToAccount(), request.getAmount()
                    )
            );
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.REQUEST_INVALID_DATA
            );
        }

        if (request.getFromAccount().equals(request.getToAccount())) {
            logger.warn(String.format(ErrorDescriptions.SAME_ACCOUNT_PARAM, request.getFromAccount()));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.SAME_ACCOUNT
            );
        }

        Account fromAccount = operationService.loadAccountByNumber(request.getFromAccount());
        if (fromAccount == null) {
            logger.warn(String.format(ErrorDescriptions.FROM_ACCOUNT_NOT_FOUND_PARAM, request.getFromAccount()));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.FROM_ACCOUNT_NOT_FOUND
            );
        }

        Account toAccount = operationService.loadAccountByNumber(request.getToAccount());
        if (toAccount == null) {
            logger.warn(String.format(ErrorDescriptions.TO_ACCOUNT_NOT_FOUND_PARAM, request.getToAccount()));
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), ErrorDescriptions.TO_ACCOUNT_NOT_FOUND
            );
        }

        try {
            operationService.processAmountTransfer(fromAccount, toAccount, request.getAmount());
        } catch (Throwable throwable) {
            return ResponseFactory.produceBadResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(), throwable.getMessage()
            );
        }
        return ResponseFactory.produceOkResponse();
    }

    private boolean isValidProcessRequest(ProcessRequest request) {
        return request.getFromAccount() != null
                && request.getToAccount() != null
                && (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
