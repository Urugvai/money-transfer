package org.morozov.transferring.core.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Person, who can execute operations.
 */
@Entity(name = "trans$User")
@Table(name = "trans_user")
public class User extends BaseEntity {

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @OneToMany(mappedBy = "accountHolder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private List<Account> accounts;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
