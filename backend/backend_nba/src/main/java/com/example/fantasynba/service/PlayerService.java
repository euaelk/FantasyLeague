package com.example.fantasynba.service;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface PlayerService {
    Map<String, String> fetchTeamRosterLinks();
    void fetchPlayerData(String url, String team);
    List<Player> getAllPlayers();
    boolean findPlayerAlreadyExists(Player player, String name);
    Integer returnInt(String s);
    Map<String, String> getTeamAbv();
    Map<String, String> getTeamLinks();
    Player savePlayerDB(Player p);
    Player getPlayer(String name);
    Document getDocument(String url);
}
