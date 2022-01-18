package com.example.fantasynba.scraping;

import org.jsoup.nodes.Element;

import java.util.List;

public interface Scraping {
    void getBoxScoreData();
    //void setTeamMatchup(Element e);
    void open_boxScore_link(Element element);
    void process_boxScore_data(String link, String date);
    void process_team_data(Element e, String date);
    void savePlayerStats(Element stat, String date);
    Integer stringToInt(String s);
}
