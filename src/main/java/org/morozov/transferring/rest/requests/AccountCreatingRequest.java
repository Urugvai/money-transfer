package org.morozov.transferring.rest.requests;

import java.math.BigDecimal;

public class AccountCreatingRequest {

    private String userLogin;

    private String number;

    private BigDecimal amount;

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
