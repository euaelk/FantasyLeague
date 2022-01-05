package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.GameRequest;

import java.util.concurrent.Future;

public interface GameService {

    Future<Game> saveNewGame(GameRequest game);

}
