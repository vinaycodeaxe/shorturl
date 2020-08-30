package com.observe.shorturl.service.impl;

import com.observe.shorturl.dao.repository.UrlRepository;
import com.observe.shorturl.dto.request.ShortUrlRequest;
import com.observe.shorturl.exception.BusinessException;
import com.observe.shorturl.service.KeyGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyGenerationServiceImpl implements KeyGenerationService {
    private final UrlRepository urlRepository;
    public static final Integer MAX_TRY = 10;

    @Override
    public String generateShortUrlKey(ShortUrlRequest shortUrlRequest) throws NoSuchAlgorithmException {
        log.info("generateShortUrlKey with shortUrlRequest {}", shortUrlRequest);
        String encodedKey = getEncodedKey(shortUrlRequest.getLongUrl(), shortUrlRequest.getClientId(), false);
        int tryCount = 0;
        while (urlRepository.findByShortUrlKey(encodedKey).isPresent()) {

            if (tryCount == MAX_TRY) {
                throw new BusinessException("Failed to generate short Url, Please Try again after sometime.");
            }
            tryCount++;
            encodedKey = getEncodedKey(shortUrlRequest.getLongUrl(), shortUrlRequest.getClientId(), true);
        }
        return encodedKey;
    }

    private String getEncodedKey(String url, Long clientId, boolean random) throws NoSuchAlgorithmException {
        log.info("getEncodedKey url {} , clientId {}, random {}", url, clientId, random);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String baseString = url + clientId + (!random ? "" : new Random(System.currentTimeMillis()).nextInt(100000));
        byte[] hash = digest.digest(baseString.getBytes(StandardCharsets.UTF_8));
        byte[] encoded = Base64.getEncoder().encode(hash);
        return (new String(encoded)).replaceAll("[+/]+", "").substring(0, 8);


    }




}
