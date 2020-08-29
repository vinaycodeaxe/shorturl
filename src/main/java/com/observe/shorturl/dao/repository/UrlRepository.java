package com.observe.shorturl.dao.repository;

import com.observe.shorturl.dao.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
}
