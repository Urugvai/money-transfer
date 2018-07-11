package org.morozov.transferring.core.entities;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Information about account
 */
@Entity(name = "trans$Account")
@Table(name = "trans_account")
public class Account extends BaseEntity {

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User accountHolder;

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

    public User getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(User accountHolder) {
        this.accountHolder = accountHolder;
    }
}
