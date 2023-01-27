package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.Crawl;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlRepo extends JpaRepository<Crawl, Long> {

    Optional<Crawl> findByUrl(String url);
    Optional<Crawl> findById(Long id);
    Crawl save(Crawl crawl);

    List<Crawl> findAll();
}
