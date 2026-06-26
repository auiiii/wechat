package com.zj.wechat.dto;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    private int code;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(SUCCESS, "success", null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(SUCCESS, "success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(FAIL, message, null);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
