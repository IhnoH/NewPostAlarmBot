package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.domain.Board;
import com.example.NewPostAlarmBot.repository.BoardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepo boardRepo;

    public void save(BoardDto dto){
        boardRepo.save(dto.toEntity());
    }

    public String update(String url, BoardDto dto){
        Board board = boardRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("update: Not found URL"));
        Board tmp = dto.toEntity();

        if (tmp.title != null && !tmp.title.equals("")) board.setTitle(tmp.title);
        if (tmp.num != null && !tmp.num.equals("")) board.setNum(tmp.num);

        return url;
    }

    public BoardDto findByUrl(String url){
        Board board = boardRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("findByUrl: Not found URL"));
        return new BoardDto(board);
    }


}
