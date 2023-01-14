package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.Crawl;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCrawlRepo extends JpaRepository<Crawl, String> {

    Optional<Crawl> findByUrl(String url);
    Optional<Crawl> findById(int id);

    @NotNull
    Crawl save(Crawl crawl);

    List<Crawl> findAll();
}
