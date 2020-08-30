package com.observe.shorturl.service.mapper;


import com.observe.shorturl.dao.entity.Url;
import com.observe.shorturl.dto.request.ShortUrlRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.observe.shorturl.constant.ApplicationConstants.HTTP;
import static com.observe.shorturl.constant.ApplicationConstants.HTTPS_REGEX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShortUrlServiceMapper {

    public static Url requestToEntity(ShortUrlRequest shortUrlRequest) {
        return Url
                .builder()
                .hits(0)
                .isActive(Boolean.TRUE)
                .longUrl(shortUrlRequest.getLongUrl())
                .clientId(shortUrlRequest.getClientId())
                .build();
    }

    public static String generateLongUrl(String longUrl) {

        if (!longUrl.toLowerCase().matches(HTTPS_REGEX)) {
            longUrl = HTTP + longUrl;
        }
        return longUrl;
    }
}
