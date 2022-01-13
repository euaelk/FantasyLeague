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
    void fetchActivePlayers() throws IOException;
    void fetchPlayers() throws IOException;
    //void savePlayer(String team, Element e) throws ExecutionException, InterruptedException;
    //CompletableFuture<Player> asyncPlayer(String name, String position, String height, Integer lbs, String dob, String college, Team team);
    List<Player> getAllPlayers();
    Player findPlayer(String name);
    Integer returnInt(String s);
    //Map<String, String> setTeamNames();
}
