package org.morozov.transferring.rest.responses;

import org.morozov.transferring.rest.dto.UserDto;

public class UserResponse extends BaseResponse {

    private UserDto user;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
