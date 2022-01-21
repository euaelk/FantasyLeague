package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface TeamService {
    void nbaStandings() throws IOException;
    void updateStandings(Element table);
    CompletableFuture<Team> saveTeam(Element team);
    List<Team> getAllTeams();
    Team findTeam(String name);
    List<Game> getTeamGames(String team);
}
