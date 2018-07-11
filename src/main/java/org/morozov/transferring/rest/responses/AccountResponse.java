package org.morozov.transferring.rest.responses;

import org.morozov.transferring.rest.dto.AccountDto;

public class AccountResponse extends BaseResponse {

    private AccountDto account;

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }
}
