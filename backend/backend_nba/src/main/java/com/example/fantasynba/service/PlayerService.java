package com.example.fantasynba.service;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface PlayerService {
    void fetchActivePlayers();
    void fetchPlayers();
    List<Player> getAllPlayers();
    Player findPlayer(String name);
    Player buildPlayer(String name, String position, String height, Integer lbs, String dob, String college, Team team);
    Integer returnInt(String s);
    Map<String, String> getTeamAbv();
    void savePlayerDB(Player p);
    void fillPlayerInfo(Element e, String team);
}
