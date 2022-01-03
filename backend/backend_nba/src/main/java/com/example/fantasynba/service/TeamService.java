package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import org.jsoup.nodes.Element;

import java.util.List;

public interface TeamService {
    void nbaStandings();
    void updateStandings(Element table);
    void teamStanding(Element team);
    List<Team> getAllTeams();
    Team findTeam(String name);
    List<Game> getTeamGames(String team);
}
