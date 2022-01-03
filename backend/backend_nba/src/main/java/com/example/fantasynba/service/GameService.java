package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.GameRequest;

public interface GameService {
    public Game saveNewGame(GameRequest game);

}
