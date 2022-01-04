package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.GameRequest;
import com.example.fantasynba.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{




    @Override
    @Async
    public Future<Game> saveNewGame(GameRequest game) {
        System.out.println("Execute method asynchronously" + Thread.currentThread().getName());
        Game g = Game.builder()
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
        return new AsyncResult<Game>(g);
    }



}
