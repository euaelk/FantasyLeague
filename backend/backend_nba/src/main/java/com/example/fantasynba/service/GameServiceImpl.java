package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.GameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{



    @Override
    public Game saveNewGame(GameRequest game) {
        return Game.builder()
                .date(game.getDate())
                .time(game.getTime())
                .visitor(game.getVisitor())
                .home(game.getHome())
                .visitor_name(game.getVisitor().getName())
                .home_name(game.getHome().getName())
                .vPts(game.getVPts())
                .hPts(game.getHPts())
                .overtime(game.getOvertime())
                .attendance(game.getAttendance()).build();
    }



}
