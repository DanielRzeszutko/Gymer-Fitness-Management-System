package com.gymer.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResponse {

    private String message;
    private boolean error;

    public static JsonResponse invalidMessage(String message) {
        return new JsonResponse(message, true);
    }

    public static JsonResponse validMessage(String message) {
        return new JsonResponse(message, false);
    }

    public boolean isResponseNotValid() {
        return !error;
    }

}
