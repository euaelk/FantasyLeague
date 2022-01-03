package com.example.fantasynba.service;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface GameScraper {

    public void fetchGameData(String url);

    public Integer stringToInt(String s);

    public void createGame(Element e);

    public void gameDataExtraction(List<String> links) throws IOException;
}
