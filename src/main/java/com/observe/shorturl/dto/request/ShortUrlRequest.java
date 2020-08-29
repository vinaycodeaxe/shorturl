package com.observe.shorturl.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortUrlRequest {

    @NotEmpty
    private String longUrl;
    private String clientId;
}
