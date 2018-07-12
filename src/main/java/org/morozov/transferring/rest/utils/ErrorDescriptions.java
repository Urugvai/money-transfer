package org.morozov.transferring.rest.utils;

public interface ErrorDescriptions {

    String USER_NOT_FOUND = "User with requested login is not found";
    String USER_NOT_FOUND_PARAM = "User with requested login '%s' is not found";

    String USER_ALREADY_EXISTS = "User with requested login already exists in system";
    String USER_ALREADY_EXISTS_PARAM = "User with requested login '%s' already exists in system";

    String ACCOUNT_NOT_FOUND = "Account with requested number is not found";
    String ACCOUNT_NOT_FOUND_PARAM = "Account with requested number '%s' is not found";
    String FROM_ACCOUNT_NOT_FOUND = "From account with requested number is not found";
    String FROM_ACCOUNT_NOT_FOUND_PARAM = "From account with requested number '%s' is not found";
    String TO_ACCOUNT_NOT_FOUND = "To account with requested number is not found";
    String TO_ACCOUNT_NOT_FOUND_PARAM = "To account with requested number '%s' is not found";

    String REQUEST_INVALID_DATA = "Invalid requested data";
    String ACCOUNT_CREATING_INVALID_DATA_PARAM =
            "Invalid requested data: user login - '%s', number - '%s', amount - '%s'";
    String PROCESS_REQUEST_INVALID_DATA_PARAM =
            "Invalid requested data: from account - '%s', to account - '%s', amount - '%s'";

    String ACCOUNT_ALREADY_EXISTS = "Account with requested number already exists in system";
    String ACCOUNT_ALREADY_EXISTS_PARAM = "Account with requested number '%s' already exists in system";

    String SAME_ACCOUNT = "Attempt to transfer amount from account to the same account is forbidden";
    String SAME_ACCOUNT_PARAM = "Attempt to transfer amount from account '%s' to the same account is forbidden";

    String ACCOUNTS_BY_USER_NOT_FOUND = "Accounts for user with requested login are not found";
    String ACCOUNTS_BY_USER_NOT_FOUND_PARAM = "Accounts for user with requested login '%s' are not found";
}
