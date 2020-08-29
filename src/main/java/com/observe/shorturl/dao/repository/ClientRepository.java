package com.observe.shorturl.dao.repository;

import com.observe.shorturl.dao.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
