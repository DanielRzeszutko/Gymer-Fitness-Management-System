package com.gymer.accountlogin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class HandlerResponse {

    private final String message;
    private final boolean error;

}
