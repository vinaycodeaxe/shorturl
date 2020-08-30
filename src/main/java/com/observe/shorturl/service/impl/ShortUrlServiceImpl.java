package com.observe.shorturl.service.impl;

import com.observe.shorturl.dao.entity.Url;
import com.observe.shorturl.dao.repository.UrlRepository;
import com.observe.shorturl.dto.request.ShortUrlRequest;
import com.observe.shorturl.exception.BusinessException;
import com.observe.shorturl.exception.NoDataFoundException;
import com.observe.shorturl.service.KeyGenerationService;
import com.observe.shorturl.service.ShortUrlService;
import com.observe.shorturl.service.mapper.ShortUrlServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {
    private final KeyGenerationService keyGenerationService;
    private final UrlRepository urlRepository;
    @Value("${service.base.url}")
    private String baseUrl;
    @Value("${url.not.found.fallback}")
    private String notFoundUrl;


    @Override
    public String createShortUrl(ShortUrlRequest shortUrlRequest) {
        log.info("createShortUrl {}", shortUrlRequest);
        Url url = urlRepository.findByLongUrlAndClientIdAndIsActiveTrue(shortUrlRequest.getLongUrl(), shortUrlRequest.getClientId())
                .orElse(null);
        if (url != null) {
            return baseUrl + url.getShortUrlKey();
        }
        String shortUrlKey;
        try {
            shortUrlKey = keyGenerationService.generateShortUrlKey(shortUrlRequest);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("Failed to generate ShortUrl");
        }

        url = ShortUrlServiceMapper.requestToEntity(shortUrlRequest);
        url.setShortUrlKey(shortUrlKey);
        urlRepository.save(url);
        return baseUrl + shortUrlKey;
    }

    @Override
    public String getOriginalURL(String shortUrlKey) {
        log.info("getOriginalURL with shortUrlKey {}", shortUrlKey);
        Url url = urlRepository.findByShortUrlKey(shortUrlKey).orElse(null);
        if (url == null || !url.getIsActive()) {
            return notFoundUrl;
        }
        url.setHits(url.getHits() + 1);
        urlRepository.save(url);
        return ShortUrlServiceMapper.generateLongUrl(url.getLongUrl());
    }

    @Override
    public Integer getRedirectCount(String shortUrlKey) {
        log.info("getRedirectCount with shortUrlKey {}", shortUrlKey);
        Url url = urlRepository.findByShortUrlKey(shortUrlKey).orElseThrow(
                () -> new NoDataFoundException("No ShortUrl found with given shortUrlKey " + shortUrlKey)
        );
        return url.getHits();
    }
}
