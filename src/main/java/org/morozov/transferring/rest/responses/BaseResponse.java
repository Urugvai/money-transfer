package org.morozov.transferring.rest.responses;

public class BaseResponse {

    private int code;

    private String errorMessage;

    public BaseResponse() {
    }

    public BaseResponse(int code) {
        this.code = code;
    }

    public BaseResponse(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
