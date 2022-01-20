package com.example.fantasynba.controller;


import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.scraping.Scraping;
import com.example.fantasynba.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.time.LocalDate;

import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@EnableAsync
public class GameController {

    private final GameRepository gameRepository;
    private final GameScraper gameScraper;
    private final DateService dateService;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final StatsRepository statsRepository;
    private final Scraping statScraper;

    @GetMapping("")
    public ResponseEntity<List<Game>> getSeasonSchedule() {
        teamService.nbaStandings();
        gameScraper.fetchGameData();
        return new ResponseEntity<>(gameScraper.getAllGames(), HttpStatus.OK);
    }

    @GetMapping("/team")
    public ResponseEntity<List<Team>> getNBATeams() {
        return new ResponseEntity<>(teamService.getAllTeams(), HttpStatus.OK);
    }

    @GetMapping("/player")
    public ResponseEntity<List<Player>> getNBAPlayers() {
        playerService.fetchActivePlayers();
        return new ResponseEntity<>(playerService.getAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/allPlayers")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return new ResponseEntity<>(playerService.getAllPlayers(), HttpStatus.OK);
    }

    @GetMapping("/games")
    @ResponseBody
    public ResponseEntity<List<Game>> getAllGames(){
        List<Game> games = gameScraper.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @GetMapping("/games/date")
    public ResponseEntity<List<Game>> getTodaysGames(){
        LocalDate ld = dateService.getDateFromString("Wed, Dec 1, 2021");
        List<Game> todayGames = gameRepository.findByDate(ld);
        return new ResponseEntity<>(todayGames, HttpStatus.OK);
    }

    @GetMapping("/team/{team}")
    public ResponseEntity<Team> getTeam(@PathVariable String team){
        return new ResponseEntity<>(teamService.findTeam(team), HttpStatus.OK);
    }

    @GetMapping("/team/{team}/games")
    public ResponseEntity<List<Game>> getTeamGames(@PathVariable String team){
        return new ResponseEntity<>(teamService.getTeamGames(team), HttpStatus.OK);
    }

    @GetMapping("/player/{name}")
    public ResponseEntity<Player> getPlayer(@PathVariable String name){
        return new ResponseEntity<>(playerService.findPlayer(name), HttpStatus.OK);
    }

    @GetMapping("/player/stats")
    public ResponseEntity<List<PlayerStats>> getAllStatistics(){
        statScraper.getBoxScoreData();
        return new ResponseEntity<>(statsRepository.findAll(), HttpStatus.OK);
    }

}
