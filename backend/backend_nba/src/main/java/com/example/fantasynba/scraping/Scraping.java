package com.example.fantasynba.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Scraping {
//    void getBoxScoreData();
//    String processBoxScore(Document document);
//    CompletableFuture<Document> processEastAndWest(String link, String date);
    void processTeamData(Element e, String date);
    void savePlayerStats(Element stat, String date);
    Integer stringToInt(String s);
}
