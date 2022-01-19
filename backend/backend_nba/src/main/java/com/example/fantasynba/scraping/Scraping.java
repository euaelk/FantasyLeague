package com.example.fantasynba.scraping;

import org.jsoup.nodes.Element;

import java.util.List;

public interface Scraping {
    void getBoxScoreData();
    void processBoxScore(Element element);
    void processEastAndWest(String link, String date);
    void processTeamData(Element e, String date);
    void savePlayerStats(Element stat, String date);
    Integer stringToInt(String s);
}
