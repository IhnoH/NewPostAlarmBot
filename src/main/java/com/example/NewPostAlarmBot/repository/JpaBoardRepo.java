package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBoardRepo extends JpaRepository<Board, Long> {

    Board findByUrl(String url);
    Board save(Board board);
}
