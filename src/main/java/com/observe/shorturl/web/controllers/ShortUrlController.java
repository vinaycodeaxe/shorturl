package com.observe.shorturl.web.controllers;


import com.observe.shorturl.dto.request.ShortUrlRequest;
import com.observe.shorturl.service.ShortUrlService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping("/api/shortUrl")
    @ApiOperation("Create new Short URl")
    public ResponseEntity<String> getShortenedURL(@RequestBody @Valid ShortUrlRequest shortUrlRequest) {
        return ResponseEntity.ok(shortUrlService.createShortUrl(shortUrlRequest));
    }

    @GetMapping("/{shortUrlKey}")
    @ApiOperation("Redirection to original url via http code 307.")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable("shortUrlKey") String shortUrlKey) {
        String originalUrl = shortUrlService.getOriginalURL(shortUrlKey);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(originalUrl));
        return new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
    }

    @GetMapping("/api/hit-count/{shortUrlKey}")
    @ApiOperation("Number of times ‘getOriginalURL’ function was called using that")
    public ResponseEntity<Integer> getRedirectCount(@PathVariable("shortUrlKey") String shortUrlKey) {
        return ResponseEntity.ok(shortUrlService.getRedirectCount(shortUrlKey));
    }

}
