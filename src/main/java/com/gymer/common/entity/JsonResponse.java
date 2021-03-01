package com.gymer.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResponse {

    private String message;
    private Response response;

    public boolean isResponseNotValid() {
        return response.equals(Response.INVALID);
    }

    public static JsonResponse invalidMessage(String message) {
        return new JsonResponse(message, Response.INVALID);
    }

    public static JsonResponse validMessage(String message) {
        return new JsonResponse(message, Response.VALID);
    }

}
