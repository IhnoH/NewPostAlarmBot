package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.DomainId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDomainRepo extends JpaRepository<DomainId, Long> {

    Optional<DomainId> findByUrl(String url);

    List<DomainId> findAll();

    @NotNull
    DomainId save(@NotNull DomainId domainId);
}
