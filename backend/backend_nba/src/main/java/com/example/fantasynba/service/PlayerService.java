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
    void fetchActivePlayers() throws Exception;
    void fetchPlayers() throws Exception;
    Element openTeamLinkThenReturnRoster(String rosterLink, String team) throws Exception;
    List<Player> getAllPlayers();
    Player findPlayer(String name);
    Integer returnInt(String s);
}
