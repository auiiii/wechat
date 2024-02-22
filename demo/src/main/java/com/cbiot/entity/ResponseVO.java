package com.cbiot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseVO<T> {

    private String status;

    private String message;

    private T result;

    public ResponseVO(String status, String message) {
        this.status=status;
        this.message=message;
    }
    public ResponseVO(String status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}

