package com.example.fantasynba.controller;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.repository.TeamRepository;
import com.example.fantasynba.scraping.Scraping;
import com.example.fantasynba.service.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/standings")
@RequiredArgsConstructor
@EnableAsync
public class GameController {

    private final GameRepository gameRepository;
    private final GameScraper gameScraper;
    private final DateService dateService;
    private final TeamService teamService;
    private final TeamRepository teamRepository;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PostConstruct
    public void init() throws IOException{
        teamService.nbaStandings();
    }



    @GetMapping("/scores")
    public List<Game> getGameData() throws IOException {
        String url = "https://www.basketball-reference.com/leagues/NBA_2022_games.html";
        gameScraper.fetchAllGames(url);
        return gameRepository.findAll();
    }

    @GetMapping("/teams")
    public List<Team> getAllGames(){
        return teamRepository.findAll();
    }

    @GetMapping("/games/{date}")
    public ResponseEntity<List<Game>> getTodaysGames(@PathVariable String date){
        //LocalDate ld = dateService.getDateFromString("Wed, Dec 1, 2021");
        List<Game> todayGames = gameRepository.findByDate(dateService.getDateFromString(date));
        return new ResponseEntity<>(todayGames, HttpStatus.OK);
    }

    @GetMapping("/teams/{team}")
    public ResponseEntity<Team> getTeam(@PathVariable String team){
        return new ResponseEntity<>(teamService.findTeam(team), HttpStatus.OK);
    }

    @GetMapping("/teams/{team}/games")
    public ResponseEntity<List<Game>> getTeamGames(@PathVariable String team){
        return new ResponseEntity<>(teamService.getTeamGames(team), HttpStatus.OK);
    }

}
