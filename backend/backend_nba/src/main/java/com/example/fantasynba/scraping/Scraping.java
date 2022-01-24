package com.example.fantasynba.scraping;


import java.io.IOException;


public interface Scraping {
    void getBoxScoreData(String boxScore) throws IOException;
    void openFutureGames(String url);
    String[] getBoxScores();
    Integer largeStringToInt(String s);
    Integer stringConversion(String s);
}
