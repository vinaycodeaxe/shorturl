package com.observe.shorturl.dao.repository;

import com.observe.shorturl.dao.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortUrlKey(String shortUrlKey);

    Optional<Url> findByLongUrlAndClientIdAndIsActiveTrue(String longUrl, Long clientId);
}
