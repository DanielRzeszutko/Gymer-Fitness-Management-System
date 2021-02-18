package com.gymer.security.common.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class JsonError {

    private String message;
    private HttpStatus status;

}
