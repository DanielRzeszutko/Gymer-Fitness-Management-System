package com.gymer.mailandsmsnotifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class SmsDetails {

    @JsonProperty(value = "callback_url")
    private final String callbackUrl;

    @JsonProperty(value = "sender_id")
    private final String senderId;

    @JsonProperty(value = "message")
    private final String message;

    @JsonProperty(value = "to")
    private final String to;

}
