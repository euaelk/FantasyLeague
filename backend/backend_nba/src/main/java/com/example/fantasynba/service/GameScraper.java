package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface GameScraper {

    void fetchGameData(String url);

    Integer stringToInt(String s);

    void createGame(Element e) throws ExecutionException, InterruptedException;

    void gameDataExtraction(List<String> links) throws IOException, ExecutionException, InterruptedException;

    List<Game> getAllGames();
}
