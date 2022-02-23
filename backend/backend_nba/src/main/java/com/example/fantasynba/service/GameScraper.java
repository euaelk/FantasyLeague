package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface GameScraper {


    void fetchAllGames(String url) throws IOException;

    void openGamesForThisMonth(String url);

    void processSchedule(Document doc) throws IOException, InterruptedException, ExecutionException;

    Integer stringToInt(String s);

    void gameDataFromHtml(Element e) throws ExecutionException, InterruptedException;

    List<Game> getAllGames();

    Game findGame(LocalDate date, Team visitor, Team home);

    Game createGame(LocalDate date, String time, Integer hPts, String overtime, Integer attendance, Integer vPts, Team visitor, Team home);
}
