package com.example.fantasynba.scraping;


import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public interface Scraping {
    void getBoxScoreData() throws IOException;
    CompletableFuture<String> openFutureGames(String url);
    String[] getBoxScores();
    Integer largeStringToInt(String s);
    Integer stringConversion(String s);
}
