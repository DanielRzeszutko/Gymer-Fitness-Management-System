package com.gymer.components.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResponse {

    private String message;
    private Response response;

    public JsonResponse(String message) {
        this.message = message;
        this.response = Response.VALID;
    }

    public boolean isResponseValid() {
        return response.equals(Response.VALID);
    }

    public static JsonResponse invalidMessage(String message) {
        return new JsonResponse(message, Response.INVALID);
    }

    public static JsonResponse validMessage(String message) {
        return new JsonResponse(message, Response.VALID);
    }

}
