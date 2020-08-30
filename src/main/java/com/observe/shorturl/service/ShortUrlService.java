package com.observe.shorturl.service;

import com.observe.shorturl.dto.request.ShortUrlRequest;

public interface ShortUrlService {
    String createShortUrl(ShortUrlRequest shortUrlRequest);

    String getOriginalURL(String shortUrlKey);

    Integer getRedirectCount(String shortUrlKey);
}
