package com.example.fantasynba.service;

import com.example.fantasynba.domain.Player;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    void fetchActivePlayers() throws IOException;
    void fetchPlayers() throws IOException;
    void savePlayer(String team, Element e);
    List<Player> getAllPlayers();
    Player findPlayer(String name);
    Integer returnInt(String s);
}
