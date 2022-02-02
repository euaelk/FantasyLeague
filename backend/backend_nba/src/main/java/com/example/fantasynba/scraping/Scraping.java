package com.example.fantasynba.scraping;


import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface Scraping {
    List<Document> openFutureGames();
    Elements getListOfBoxScores(Document doc);
    Map<String, String> getScoresByDate(Elements games);
    void processVisitorHome(String link, String date);
    String[] getBoxScores();
    Integer stringConversion(String s);


}
