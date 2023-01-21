package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.domain.Board;
import com.example.NewPostAlarmBot.domain.DomainInfo;
import com.example.NewPostAlarmBot.repository.BoardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepo boardRepo;

    public void save(BoardDto dto){
        Optional<Board> b = boardRepo.findByUrl(dto.url);
        if(b.isPresent())
            update(dto.url, dto);
        else
            boardRepo.save(dto.toEntity());
    }

    public String update(String url, BoardDto dto){
        Board board = boardRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("update: Not found URL"));
        board.setNum(dto.num);
        board.setTitle(dto.title);
        board.setWriter(dto.writer);
        return url;
    }

    public BoardDto findByUrl(String url){
        Board board = boardRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("findByUrl: Not found URL"));
        return new BoardDto(board);
    }

    public List<BoardDto> findAll(){
        List<BoardDto> tmp = new ArrayList<>();
        for(Board board: boardRepo.findAll()) tmp.add(new BoardDto(board));
        return tmp;
    }


}
