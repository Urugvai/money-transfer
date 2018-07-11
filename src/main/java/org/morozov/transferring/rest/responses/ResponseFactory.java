package org.morozov.transferring.rest.responses;

import org.jetbrains.annotations.NotNull;
import org.morozov.transferring.core.entities.Account;
import org.morozov.transferring.core.entities.TransactionHistoryRecord;
import org.morozov.transferring.core.entities.User;
import org.morozov.transferring.rest.dto.AccountDto;
import org.morozov.transferring.rest.dto.HistoryRecordDto;
import org.morozov.transferring.rest.dto.UserDto;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseFactory {

    @NotNull
    public static BaseResponse produceOkResponse() {
        return new BaseResponse(Response.Status.OK.getStatusCode());
    }

    @NotNull
    public static BaseResponse produceBadResponse(int code, @NotNull String errorMessage) {
        return new BaseResponse(code, errorMessage);
    }

    @NotNull
    public static UserResponse produceUserResponse(@NotNull User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUser(mapUser(user));
        userResponse.setCode(Response.Status.OK.getStatusCode());
        return userResponse;
    }

    @NotNull
    public static AccountResponse produceAccountResponse(@NotNull Account account) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccount(mapAccount(account));
        accountResponse.setCode(Response.Status.OK.getStatusCode());
        return accountResponse;
    }

    @NotNull
    public static List<UserDto> mapUsers(@NotNull List<User> users) {
        return users.stream().map(ResponseFactory::mapUser).collect(Collectors.toList());
    }

    @NotNull
    public static List<AccountDto> mapAccounts(@NotNull List<Account> accounts) {
        return accounts.stream().map(ResponseFactory::mapAccount).collect(Collectors.toList());
    }

    @NotNull
    public static HistoryRecordsResponse produceHistoryRecordsResponse(@NotNull List<TransactionHistoryRecord> records) {
        HistoryRecordsResponse response = new HistoryRecordsResponse();
        response.setRecords(mapRecords(records));
        response.setCode(Response.Status.OK.getStatusCode());
        return response;
    }

    @NotNull
    private static List<HistoryRecordDto> mapRecords(@NotNull List<TransactionHistoryRecord> records) {
        return records.stream().map(record -> {
            HistoryRecordDto dto = new HistoryRecordDto();
            dto.fromAccount = record.getFromAccount().getNumber();
            dto.toAccount = record.getToAccount().getNumber();
            dto.amount = record.getAmount();
            dto.timeStamp = record.getTransactionDate();
            return dto;
        }).collect(Collectors.toList());
    }

    @NotNull
    private static UserDto mapUser(@NotNull User user) {
        UserDto userDto = new UserDto();
        userDto.login = user.getLogin();
        userDto.accounts = user.getAccounts().stream().map(ResponseFactory::mapAccount).collect(Collectors.toList());

        return userDto;
    }

    @NotNull
    private static AccountDto mapAccount(@NotNull Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.amount = account.getAmount();
        accountDto.number = account.getNumber();
        accountDto.accountHolder = account.getAccountHolder().getLogin();
        return accountDto;
    }
}
