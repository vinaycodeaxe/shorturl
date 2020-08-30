package com.observe.shorturl.service;

import com.observe.shorturl.dto.request.ShortUrlRequest;

import java.security.NoSuchAlgorithmException;

public interface KeyGenerationService {
    String generateShortUrlKey(ShortUrlRequest shortUrlRequest) throws NoSuchAlgorithmException;
}
