package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepo extends JpaRepository<Board, Long> {

    Optional<Board> findByUrl(String url);
    List<Board> findAll();
    Board save(Board board);
}
